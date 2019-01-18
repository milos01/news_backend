ALTER TABLE `vote`
  ADD COLUMN `special_poll_nomination` VARCHAR(512) NULL DEFAULT NULL AFTER `choice`