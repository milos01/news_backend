DROP TRIGGER IF EXISTS `7bnew`.`comment_AFTER_INSERT`;

DELIMITER $$
USE `7bnew`$$
CREATE DEFINER=`7bnew`@`localhost` TRIGGER comment_AFTER_INSERT AFTER INSERT ON comment FOR EACH ROW
  BEGIN
    IF(NEW.parent_comment_id IS NULL) THEN
      UPDATE content SET r_comments = r_comments + 1 WHERE id = NEW.content_id;
    END IF;
  END$$
DELIMITER ;

DROP TRIGGER IF EXISTS `7bnew`.`comment_AFTER_UPDATE`;

DELIMITER $$
USE `7bnew`$$
CREATE DEFINER=`7bnew`@`localhost` TRIGGER comment_AFTER_UPDATE AFTER UPDATE ON comment FOR EACH ROW
  BEGIN
    IF(NEW.is_deleted) THEN
      IF(NEW.parent_comment_id IS NULL) THEN
        UPDATE content SET r_comments = r_comments - 1 WHERE id = NEW.content_id;
      END IF;
    END IF;
  END$$
DELIMITER ;


DROP TRIGGER IF EXISTS `7bnew`.`abuse_report_AFTER_UPDATE`;

DELIMITER $$
USE `7bnew`$$
CREATE DEFINER=`7bnew`@`localhost` TRIGGER abuse_report_AFTER_UPDATE AFTER UPDATE ON abuse_report FOR EACH ROW
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
        IF(@parentId IS NOT NULL) THEN
          UPDATE comment SET r_replies = r_replies - 1 WHERE id = @parentId;
        END IF;

        UPDATE notification SET is_deleted = 1 WHERE comment_id  = NEW.comment_id;
      END IF;
    END IF;
  END$$
DELIMITER ;
