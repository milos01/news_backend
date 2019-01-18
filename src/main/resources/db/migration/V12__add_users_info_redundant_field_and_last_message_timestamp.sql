ALTER TABLE `conversation`
  ADD COLUMN `r_users_info` VARCHAR(10240) NULL DEFAULT NULL AFTER `r_users`,
  ADD COLUMN `last_message_time` DATETIME NULL DEFAULT NULL AFTER `r_users`