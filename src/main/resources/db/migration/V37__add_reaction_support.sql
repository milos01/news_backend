ALTER TABLE `content`
  ADD COLUMN `r_reactions` VARCHAR(512) AFTER `r_tags`;

ALTER TABLE `content_like`
  ADD COLUMN `type` ENUM('HEART', 'ANGRY', 'SAD', 'INTERESTING', 'LOL') AFTER `content_id`;

UPDATE `content` SET `r_reactions` = IF(r_likes IS NOT NULL, CONCAT('HEART/', r_likes, '|ANGRY/0|SAD/0|INTERESTING/0|LOL/0'), '');
UPDATE `content_like` SET `type` = 'HEART';

DROP TRIGGER IF EXISTS `7bnew`.`content_like_AFTER_INSERT`;

DELIMITER $$
CREATE TRIGGER content_like_AFTER_INSERT AFTER INSERT ON content_like FOR EACH ROW
    BEGIN
      SET @heart = (SELECT COUNT(id) FROM content_like WHERE type = 'HEART' AND is_deleted = FALSE AND `content_id` = NEW.content_id);
      SET @angry = (SELECT COUNT(id) FROM content_like WHERE type = 'ANGRY' AND is_deleted = FALSE AND `content_id` = NEW.content_id);
      SET @sad = (SELECT COUNT(id) FROM content_like WHERE type = 'SAD' AND is_deleted = FALSE AND `content_id` = NEW.content_id);
      SET @interesting = (SELECT COUNT(id) FROM content_like WHERE type = 'INTERESTING' AND is_deleted = FALSE AND `content_id` = NEW.content_id);
      SET @lol = (SELECT COUNT(id) FROM content_like WHERE type = 'LOL' AND is_deleted = FALSE AND `content_id` = NEW.content_id);
      UPDATE `content` SET
        `r_reactions` = CONCAT('HEART/', @heart, '|ANGRY/', @angry, '|SAD/', @sad, '|INTERESTING/', @interesting, '|LOL/', @lol),
        `r_likes` = `r_likes` + 1
      WHERE `id` = NEW.content_id;
    END
  $$
DELIMITER ;

DROP TRIGGER IF EXISTS `7bnew`.`content_like_AFTER_UPDATE`;

DELIMITER $$
  CREATE TRIGGER content_like_AFTER_UPDATE AFTER UPDATE ON content_like FOR EACH ROW
    BEGIN
      SET @heart = (SELECT COUNT(id) FROM content_like WHERE type = 'HEART' AND is_deleted = FALSE AND `content_id` = NEW.content_id);
      SET @angry = (SELECT COUNT(id) FROM content_like WHERE type = 'ANGRY' AND is_deleted = FALSE AND `content_id` = NEW.content_id);
      SET @sad = (SELECT COUNT(id) FROM content_like WHERE type = 'SAD' AND is_deleted = FALSE AND `content_id` = NEW.content_id);
      SET @interesting = (SELECT COUNT(id) FROM content_like WHERE type = 'INTERESTING' AND is_deleted = FALSE AND `content_id` = NEW.content_id);
      SET @lol = (SELECT COUNT(id) FROM content_like WHERE type = 'LOL' AND is_deleted = FALSE AND `content_id` = NEW.content_id);
      UPDATE `content` SET
        `r_reactions` = CONCAT('HEART/', @heart, '|ANGRY/', @angry, '|SAD/', @sad, '|INTERESTING/', @interesting, '|LOL/', @lol),
        `r_likes` = IF(!NEW.is_deleted, r_likes + 1, r_likes - 1)
      WHERE `id` = NEW.content_id;
    END
  $$
DELIMITER ;