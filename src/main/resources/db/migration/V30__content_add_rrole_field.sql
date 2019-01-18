ALTER TABLE `content`
  ADD COLUMN `r_user_role` VARCHAR(256) NULL DEFAULT NULL AFTER `r_username`;

UPDATE content c INNER JOIN user u on c.user_id = u.id SET c.r_user_role = u.role;