ALTER TABLE `abuse_report`
  ADD COLUMN `abuse_report_status` VARCHAR(512) NULL DEFAULT NULL AFTER `reason`