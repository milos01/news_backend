DROP TRIGGER IF EXISTS `7bnew`.`comment_AFTER_UPDATE`;

DELIMITER $$
USE `7bnew`$$
CREATE DEFINER=`7bnew`@`localhost` TRIGGER comment_AFTER_UPDATE AFTER UPDATE ON comment FOR EACH ROW
  BEGIN
    IF(!OLD.is_deleted AND NEW.is_deleted) THEN
      UPDATE content SET r_comments = r_comments - 1 WHERE id = NEW.content_id;
    END IF;
  END$$
DELIMITER ;