DROP TRIGGER IF EXISTS `7bnew`.`abuse_report_AFTER_UPDATE`;

DELIMITER $$
USE `7bnew`$$
CREATE DEFINER=`7bnew`@`localhost` TRIGGER abuse_report_AFTER_UPDATE AFTER UPDATE ON abuse_report FOR EACH ROW
  BEGIN
    IF(NEW.type = 'CONTENT') THEN
      IF(NEW.abuse_report_status = 'COMPLETED') THEN

        SET @pollId = (SELECT poll_id FROM content where id = NEW.content_id);
        IF(@pollId IS NOT NULL) THEN
          UPDATE poll set is_deleted = 1 WHERE id = @pollId AND is_deleted = 0;
        END IF;

        SET @repostId = (SELECT repost_id FROM content where id = NEW.content_id AND content.is_deleted = 0);
        IF(@repostId IS NOT NULL) THEN
          UPDATE content set r_reposts = r_reposts - 1 WHERE id = @repostId AND is_deleted = 0;
        END IF;

        UPDATE notification SET is_deleted = 1 WHERE content_id  = NEW.content_id;

        UPDATE content SET is_deleted = 1 WHERE (id = NEW.content_id OR repost_id = NEW.content_id) AND is_deleted = 0;

      END IF;
    ELSEIF(NEW.type = 'COMMENT') THEN
      IF(NEW.abuse_report_status = 'COMPLETED') THEN
        UPDATE comment SET is_deleted = 1, r_replies = 0 WHERE (id = NEW.comment_id AND is_deleted = 0) OR (parent_comment_id = NEW.comment_id AND is_deleted = 0);

        SET @parentId = (SELECT parent_comment_id FROM comment where id = NEW.comment_id);
        IF(@parentId IS NOT NULL) THEN
          UPDATE comment SET r_replies = r_replies - 1 WHERE id = @parentId;
        END IF;

        UPDATE notification SET is_deleted = 1 WHERE comment_id  = NEW.comment_id;
      END IF;
    END IF;
  END$$
DELIMITER ;
