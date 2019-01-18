DELIMITER $$
CREATE TRIGGER abuse_report_AFTER_UPDATE AFTER UPDATE ON abuse_report FOR EACH ROW
  BEGIN
    IF(NEW.type = 'CONTENT') THEN
      IF(NEW.abuse_report_status = 'COMPLETED') THEN
        UPDATE content SET is_deleted = 1 WHERE id = NEW.content_id AND is_deleted = 0;

        SET @pollId = (SELECT poll_id FROM content where id = NEW.content_id);
        IF(@pollId IS NOT NULL) THEN
          UPDATE poll set is_deleted = 1 WHERE id = @pollId AND is_deleted = 0;
        END IF;

        UPDATE notification SET is_deleted = 1 WHERE content_id  = NEW.content_id;
      END IF;
    ELSEIF(NEW.type = 'COMMENT') THEN
      IF(NEW.abuse_report_status = 'COMPLETED') THEN
        UPDATE comment SET is_deleted = 1 WHERE id = NEW.comment_id AND is_deleted = 0;

        SET @parentId = (SELECT parent_comment_id FROM comment where id = NEW.comment_id);
        IF(@parentId IS NULL) THEN
          UPDATE content SET r_comments = r_comments - 1 WHERE id = NEW.content_id;
        ELSE
          UPDATE comment SET r_replies = r_replies - 1 WHERE id = @parentId;
        END IF;

        UPDATE notification SET is_deleted = 1 WHERE comment_id  = NEW.comment_id;
      END IF;
    END IF;
  END;
$$
#
DELIMITER $$
CREATE TRIGGER comment_AFTER_INSERT AFTER INSERT ON comment FOR EACH ROW
  BEGIN
    IF(NEW.parent_comment_id IS NULL) THEN
      UPDATE content SET r_comments = r_comments + 1 WHERE id = NEW.content_id;
    END IF;
  END;
$$
#
DELIMITER $$
CREATE TRIGGER comment_AFTER_UPDATE AFTER UPDATE ON comment FOR EACH ROW
  BEGIN
    UPDATE content SET r_comments = IF(!NEW.is_deleted, r_comments + 1, r_comments - 1) WHERE id = NEW.content_id;
  END;
$$
#
DELIMITER $$
CREATE TRIGGER content_BEFORE_UPDATE BEFORE UPDATE ON content FOR EACH ROW
  BEGIN
    IF(NEW.poll_id IS NOT NULL) THEN
      SET @question = (SELECT question FROM poll WHERE id = NEW.poll_id);
      SET @firstChoice = (SELECT first_choice FROM poll WHERE id = NEW.poll_id);
      SET @secondChoice = (SELECT second_choice FROM poll WHERE id = NEW.poll_id);
      SET @thirdChoice = (SELECT third_choice FROM poll WHERE id = NEW.poll_id);
      SET @firstChoiceAmmount = (SELECT r_first_amount FROM poll WHERE id = NEW.poll_id);
      SET @secondChoiceAmmount = (SELECT r_second_amount FROM poll WHERE id = NEW.poll_id);
      SET @thirdChoiceAmmount = (SELECT r_third_amount FROM poll WHERE id = NEW.poll_id);
      SET NEW.r_poll_question = @question,
      NEW.r_poll_answers = CONCAT(@firstChoice, '/', @firstChoiceAmmount, '|', @secondChoice, '/', @secondChoiceAmmount, IF(@thirdchoice IS NULL, '', CONCAT('|', @thirdchoice, '/', @thirdChoiceAmmount)));
    END IF;
  END
$$
#
DELIMITER $$
CREATE TRIGGER content_AFTER_UPDATE AFTER UPDATE ON content FOR EACH ROW
  BEGIN
    IF(OLD.headline != NEW.headline OR OLD.is_deleted != NEW.is_deleted) THEN
      UPDATE notification SET r_content_headline = NEW.headline, is_deleted = NEW.is_deleted, update_time = NEW.update_time WHERE content_id = NEW.id;
    END IF;
  END;
$$
#
DELIMITER $$
CREATE TRIGGER content_like_AFTER_INSERT AFTER INSERT ON content_like FOR EACH ROW
  UPDATE content SET r_likes = r_likes + 1 WHERE id = NEW.content_id;
$$
#
DELIMITER $$
CREATE TRIGGER content_like_AFTER_UPDATE AFTER UPDATE ON content_like FOR EACH ROW
  UPDATE content SET r_likes = IF(!NEW.is_deleted, r_likes + 1, r_likes - 1) WHERE id = NEW.content_id;
$$
#
DELIMITER $$
CREATE TRIGGER content_media_AFTER_INSERT AFTER INSERT ON content_media FOR EACH ROW
  BEGIN
    UPDATE content SET r_has_video = CASE WHEN NEW.type = 'VIDEO' THEN 1 ELSE r_has_video END, r_media_url = CONCAT(IF(r_media_url IS NOT NULL, CONCAT(r_media_url, '|'), ''), NEW.url) WHERE id = NEW.content_id;
  END;
$$
#
DELIMITER $$
CREATE TRIGGER content_media_AFTER_UPDATE AFTER UPDATE ON content_media FOR EACH ROW
  BEGIN
    UPDATE content SET r_has_video = CASE WHEN NEW.type = 'VIDEO' AND NEW.is_deleted = 0 THEN 1 ELSE r_has_video END,
      r_media_url = IF(NEW.is_deleted, IF(r_media_url LIKE CONCAT('%', NEW.url, '|%'),
        REPLACE(r_media_url, CONCAT(NEW.url, '|'), ''),
        IF(r_media_url LIKE CONCAT('%|', NEW.url, '%'),
          REPLACE(r_media_url, CONCAT('|', NEW.url), ''),
          IF(r_media_url LIKE CONCAT('%', NEW.url, '%'),
            REPLACE(r_media_url, CONCAT(NEW.url), NULL),
            r_media_url
          )
        )
      ), r_media_url)
    WHERE id = NEW.content_id;
  END;
$$
#
DELIMITER $$
CREATE TRIGGER entity_tag_AFTER_INSERT AFTER INSERT ON entity_tag FOR EACH ROW
  IF (NEW.user_id IS NOT NULL) THEN
    UPDATE user SET r_tags = CONCAT(IF(r_tags IS NOT NULL, CONCAT(r_tags, '|'), ''), NEW.tag_id, '/', (SELECT text FROM tag where tag.id = NEW.tag_id)) WHERE id = NEW.user_id AND !NEW.is_deleted;
  ELSEIF (NEW.content_id IS NOT NULL) THEN
    UPDATE content SET r_tags = CONCAT(IF(r_tags IS NOT NULL, CONCAT(r_tags, '|'), ''), (SELECT text FROM tag where tag.id = NEW.tag_id), '/', NEW.tag_id) WHERE id = NEW.content_id AND !NEW.is_deleted;
  END IF;
$$
#
DELIMITER $$
CREATE TRIGGER entity_tag_AFTER_UPDATE AFTER UPDATE ON entity_tag FOR EACH ROW
  BEGIN
    SET @tagText = (SELECT text FROM tag where tag.id = NEW.tag_id);
    IF(!NEW.is_deleted AND OLD.is_deleted) THEN
      IF (NEW.user_id IS NOT NULL) THEN
        UPDATE user SET r_tags = CONCAT(IF(r_tags IS NOT NULL, CONCAT(r_tags, '|'), ''), NEW.tag_id, '/', @tagText) WHERE id = NEW.user_id;
      ELSEIF (NEW.content_id IS NOT NULL) THEN
        UPDATE content SET r_tags = CONCAT(IF(r_tags IS NOT NULL, CONCAT(r_tags, '|'), ''), @tagText, '/', NEW.tag_id) WHERE id = NEW.content_id;
      END IF;
    ELSEIF(NEW.is_deleted) THEN
      IF (NEW.user_id IS NOT NULL) THEN
        UPDATE user
        SET r_tags =
        IF(r_tags LIKE CONCAT('%', NEW.tag_id, '/', @tagText, '|%'),
           REPLACE(r_tags, CONCAT(NEW.tag_id, '/', @tagText, '|'), ''),
           IF(r_tags LIKE CONCAT('%|', NEW.tag_id, '/', @tagText, '%'),
              REPLACE(r_tags, CONCAT('|', NEW.tag_id, '/', @tagText), ''),
              IF(r_tags LIKE CONCAT('%', NEW.tag_id, '/', @tagText, '%'),
                 REPLACE(r_tags, CONCAT(NEW.tag_id, '/', @tagText), NULL),
                 r_tags
              )
           )
        ) WHERE id = NEW.user_id;
      ELSEIF (NEW.content_id IS NOT NULL) THEN
        UPDATE content
        SET r_tags =
        IF(r_tags LIKE CONCAT('%', @tagText, '/', NEW.tag_id, '|%'),
           REPLACE(r_tags, CONCAT(@tagText, '/', NEW.tag_id, '|'), ''),
           IF(r_tags LIKE CONCAT('%|', @tagText, '/', NEW.tag_id, '%'),
              REPLACE(r_tags, CONCAT('|', @tagText, '/', NEW.tag_id), ''),
              IF(r_tags LIKE CONCAT('%', @tagText, '/', NEW.tag_id, '%'),
                 REPLACE(r_tags, CONCAT(@tagText, '/', NEW.tag_id), NULL),
                 r_tags
              )
           )
        ) WHERE id = NEW.content_id;
      END IF;
    END IF;
  END;
$$
#
DELIMITER $$
CREATE TRIGGER follow_AFTER_INSERT AFTER INSERT ON follow FOR EACH ROW
  BEGIN
    IF(NEW.follow_user_id IS NOT NULL) THEN
      UPDATE user SET r_followers = r_followers + 1 WHERE id = NEW.follow_user_id;
    ELSEIF(NEW.follow_tag_id IS NOT NULL) THEN
      UPDATE tag SET r_followers = r_followers + 1 WHERE id = NEW.follow_tag_id;
    END IF;
    UPDATE user SET r_following = r_following + 1 WHERE id = NEW.user_id;
  END
$$
#
DELIMITER $$
CREATE TRIGGER follow_AFTER_UPDATE AFTER UPDATE ON follow FOR EACH ROW
  BEGIN
    IF(NEW.follow_user_id IS NOT NULL) THEN
      UPDATE user SET r_followers = IF(!NEW.is_deleted, r_followers + 1, r_followers - 1) WHERE id = NEW.follow_user_id;
    ELSEIF(NEW.follow_tag_id IS NOT NULL) THEN
      UPDATE tag SET r_followers = IF(!NEW.is_deleted, r_followers + 1, r_followers - 1) WHERE id = NEW.follow_tag_id;
    END IF;
    UPDATE user SET r_following = IF(!NEW.is_deleted, r_following + 1, r_following - 1) WHERE id = NEW.user_id;
  END
$$
#
DELIMITER $$
CREATE TRIGGER notification_AFTER_INSERT AFTER INSERT ON notification FOR EACH ROW
  UPDATE content SET r_shares = r_shares + 1 WHERE NEW.type = 'SHARED' and id = NEW.content_id;
$$
#
DELIMITER $$
CREATE TRIGGER poll_AFTER_UPDATE AFTER UPDATE ON poll FOR EACH ROW
  BEGIN
    UPDATE content SET
      r_poll_question = NEW.question,
      r_poll_answers = CONCAT(NEW.first_choice, '/', NEW.r_first_amount, '|', NEW.second_choice, '/', NEW.r_second_amount, IF(NEW.third_choice IS NULL, '', CONCAT('|', NEW.third_choice, '/', NEW.r_third_amount)))
    WHERE content.poll_id = NEW.id;
  END
$$
#
DELIMITER $$
CREATE TRIGGER tag_AFTER_UPDATE AFTER UPDATE ON tag FOR EACH ROW
  BEGIN
    IF(NEW.is_deleted) THEN
      UPDATE content JOIN entity_tag ON content.id = entity_tag.content_id
      SET r_tags=
      IF(content.r_tags LIKE CONCAT('%', NEW.text, '/', NEW.id, '|%'),
         REPLACE(content.r_tags, CONCAT(NEW.text, '/', NEW.id, '|'), ''),
         IF(content.r_tags LIKE CONCAT('%|', NEW.text, '/', NEW.id, '%'),
            REPLACE(content.r_tags, CONCAT('|', NEW.text, '/', NEW.id), ''),
            IF(content.r_tags LIKE CONCAT('%', NEW.text, '/', NEW.id, '%'),
               REPLACE(content.r_tags, CONCAT( NEW.text, '/', NEW.id), NULL),
               r_tags
            )
         )
      ) WHERE entity_tag.tag_id = NEW.id;
      UPDATE user JOIN entity_tag ON user.id = entity_tag.user_id
      SET r_tags=
      IF(content.r_tags LIKE CONCAT('%', NEW.id, '/', NEW.text, '|%'),
         REPLACE(content.r_tags, CONCAT(NEW.id, '/', NEW.text, '|'), ''),
         IF(content.r_tags LIKE CONCAT('%|', NEW.id, '/', NEW.text, '%'),
            REPLACE(content.r_tags, CONCAT('|', NEW.id, '/', NEW.text), ''),
            IF(content.r_tags LIKE CONCAT('%', NEW.id, '/', NEW.text, '%'),
               REPLACE(content.r_tags, CONCAT(NEW.id, '/', NEW.text), NULL),
               r_tags
            )
         )
      ) WHERE entity_tag.tag_id = NEW.id;
    END IF;
  END;
$$
#
DELIMITER $$
CREATE TRIGGER user_AFTER_UPDATE AFTER UPDATE ON user FOR EACH ROW
  BEGIN
    IF(NEW.username != OLD.username OR NEW.image_url != OLD.image_url) THEN
      UPDATE comment SET r_username = NEW.username, r_user_image_url = NEW.image_url WHERE !NEW.is_deleted and user_id = NEW.id;
      UPDATE content SET r_username = NEW.username, r_user_image_url = NEW.image_url WHERE !NEW.is_deleted and user_id = NEW.id;
      UPDATE conversation JOIN user_conversation ON conversation.id = user_conversation.conversation_id
      SET r_users = REPLACE(r_users, CONCAT(OLD.username, '\\\\', OLD.image_url), CONCAT(NEW.username, '\\\\', NEW.image_url)) WHERE !NEW.is_deleted and user_id = NEW.id;
      UPDATE notification SET r_users = REPLACE(r_users, CONCAT(OLD.username, '/', OLD.id), CONCAT(NEW.username, '/', NEW.id));
    END IF;
  END
$$
#
DELIMITER $$
CREATE TRIGGER vote_AFTER_INSERT AFTER INSERT ON vote FOR EACH ROW
  BEGIN
    IF(NEW.choice = 'FIRST') THEN
      UPDATE poll SET r_first_amount = r_first_amount + 1 WHERE id = NEW.poll_id;
    ELSEIF (NEW.choice = 'SECOND') THEN
      UPDATE poll SET r_second_amount = r_second_amount + 1 WHERE id = NEW.poll_id;
    ELSEIF (NEW.choice = 'THIRD') THEN
      UPDATE poll SET r_third_amount = r_third_amount + 1 WHERE id = NEW.poll_id;
    END IF;
  END;
$$
#
DELIMITER $$
CREATE TRIGGER vote_AFTER_UPDATE AFTER UPDATE ON vote FOR EACH ROW
  BEGIN
    IF(OLD.choice = 'FIRST') THEN
      UPDATE poll SET r_first_amount = r_first_amount - 1 WHERE id = NEW.poll_id;
    ELSEIF (OLD.choice = 'SECOND') THEN
      UPDATE poll SET r_second_amount = r_second_amount - 1 WHERE id = NEW.poll_id;
    ELSEIF (OLD.choice = 'THIRD') THEN
      UPDATE poll SET r_third_amount = r_third_amount - 1 WHERE id = NEW.poll_id;
    END IF;
    IF(!NEW.is_deleted) THEN
      IF(NEW.choice = 'FIRST') THEN
        UPDATE poll SET r_first_amount = r_first_amount + 1 WHERE id = NEW.poll_id;
      ELSEIF (NEW.choice = 'SECOND') THEN
        UPDATE poll SET r_second_amount = r_second_amount + 1 WHERE id = NEW.poll_id;
      ELSEIF (NEW.choice = 'THIRD') THEN
        UPDATE poll SET r_third_amount = r_third_amount + 1 WHERE id = NEW.poll_id;
      END IF;
    END IF;
  END;
$$
DELIMITER ;