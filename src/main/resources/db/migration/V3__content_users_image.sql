ALTER TABLE `content`
  ADD COLUMN `r_user_image_url` VARCHAR(256) NULL DEFAULT NULL AFTER `r_username`;
