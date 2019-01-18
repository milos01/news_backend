ALTER TABLE `comment`
  ADD COLUMN `r_username` VARCHAR(256) NULL DEFAULT NULL AFTER `is_deleted`,
  ADD COLUMN `r_user_image_url` VARCHAR(256) NULL DEFAULT NULL AFTER `r_username`,
  ADD COLUMN `r_replies` INT(11) NULL DEFAULT NULL AFTER `r_user_image_url`;