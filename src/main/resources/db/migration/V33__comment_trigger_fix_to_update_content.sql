DROP TRIGGER IF EXISTS `7bnew`.`comment_AFTER_INSERT`;

DELIMITER $$
USE `7bnew`$$
CREATE DEFINER=`7bnew`@`localhost` TRIGGER comment_AFTER_INSERT AFTER INSERT ON comment FOR EACH ROW
  BEGIN
    UPDATE content SET r_comments = r_comments + 1 WHERE id = NEW.content_id;
  END$$
DELIMITER ;

DROP TRIGGER IF EXISTS `7bnew`.`comment_AFTER_UPDATE`;

DELIMITER $$
USE `7bnew`$$
CREATE DEFINER=`7bnew`@`localhost` TRIGGER comment_AFTER_UPDATE AFTER UPDATE ON comment FOR EACH ROW
  BEGIN
    IF(NEW.is_deleted) THEN
      UPDATE content SET r_comments = r_comments - 1 WHERE id = NEW.content_id;
    END IF;
  END$$
DELIMITER ;