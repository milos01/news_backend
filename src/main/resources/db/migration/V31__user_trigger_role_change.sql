DROP TRIGGER IF EXISTS `7bnew`.`user_AFTER_UPDATE`;

DELIMITER $$
USE `7bnew`$$
CREATE DEFINER=`7bnew`@`localhost` TRIGGER user_AFTER_UPDATE AFTER UPDATE ON user FOR EACH ROW
  BEGIN
    IF(NEW.username != OLD.username OR NEW.image_url != OLD.image_url OR OLD.image_url is null AND NEW.image_url is not null OR OLD.image_url is not null AND NEW.image_url is null) THEN
      UPDATE comment SET r_username = NEW.username, r_user_image_url = NEW.image_url WHERE !NEW.is_deleted and user_id = NEW.id;
      UPDATE content SET r_username = NEW.username, r_user_image_url = NEW.image_url WHERE !NEW.is_deleted and user_id = NEW.id;
      UPDATE conversation JOIN user_conversation ON conversation.id = user_conversation.conversation_id
      SET r_users_info = REPLACE(r_users_info, CONCAT(OLD.username, '\\', if(OLD.image_url is null, 'null', OLD.image_url)), CONCAT(NEW.username, '\\', if(NEW.image_url is null, 'null', NEW.image_url))) WHERE !NEW.is_deleted and user_id = NEW.id;
      UPDATE notification SET r_users = REPLACE(r_users, CONCAT(OLD.username, '/', OLD.id), CONCAT(NEW.username, '/', NEW.id));
      UPDATE content SET text = REPLACE(text, CONCAT('<a href="/user/', OLD.id, '" userid="', OLD.id, '" target="_blank">', OLD.username, '</a>'), CONCAT('<a href="/user/', NEW.id, '" userid="', NEW.id, '" target="_blank">', NEW.username, '</a>')) where text like CONCAT('%', '<a href="/user/', OLD.id, '" userid="', OLD.id, '" target="_blank">', OLD.username, '</a>', '%') AND !NEW.is_deleted AND !content.is_deleted;
      UPDATE comment SET text = REPLACE(text, CONCAT('<a href="/user/', OLD.id, '" userid="', OLD.id, '" target="_blank">', OLD.username, '</a>'), CONCAT('<a href="/user/', NEW.id, '" userid="', NEW.id, '" target="_blank">', NEW.username, '</a>')) where text like CONCAT('%', '<a href="/user/', OLD.id, '" userid="', OLD.id, '" target="_blank">', OLD.username, '</a>', '%') AND !NEW.is_deleted AND !comment.is_deleted;
    END IF;
    IF(NEW.role != OLD.role) THEN
      UPDATE content SET r_user_role = NEW.role WHERE !NEW.is_deleted and user_id = NEW.id;
    END IF;
  END$$
DELIMITER ;