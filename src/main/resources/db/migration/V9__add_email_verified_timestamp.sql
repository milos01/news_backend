ALTER TABLE `user`
  ADD COLUMN `email_verified_time` DATETIME NULL DEFAULT NULL AFTER `update_time`