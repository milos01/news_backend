ALTER TABLE `content`
  CHANGE COLUMN `r_likes` `r_likes` INT(11) NULL DEFAULT 0 ,
  CHANGE COLUMN `r_reposts` `r_reposts` INT(11) NULL DEFAULT 0 ,
  CHANGE COLUMN `r_shares` `r_shares` INT(11) NULL DEFAULT 0 ,
  CHANGE COLUMN `r_comments` `r_comments` INT(11) NULL DEFAULT 0 ,
  CHANGE COLUMN `r_has_video` `r_has_video` BIT(1) NULL DEFAULT b'0' ;

ALTER TABLE `conversation`
  CHANGE COLUMN `r_users` `r_users` LONGTEXT NULL ;

ALTER TABLE `poll`
  CHANGE COLUMN `r_first_amount` `r_first_amount` INT(11) NULL DEFAULT 0 ,
  CHANGE COLUMN `r_second_amount` `r_second_amount` INT(11) NULL DEFAULT 0 ;

ALTER TABLE `tag`
  CHANGE COLUMN `r_followers` `r_followers` INT(11) NULL DEFAULT 0 ;

ALTER TABLE `user`
  CHANGE COLUMN `r_followers` `r_followers` INT(11) NULL DEFAULT 0 ,
  CHANGE COLUMN `r_following` `r_following` INT(11) NULL DEFAULT 0 ;
