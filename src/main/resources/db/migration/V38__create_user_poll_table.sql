CREATE TABLE `user_poll` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL,
  `poll_id` BIGINT(20) NOT NULL,
  `is_deleted` BIT(1) NOT NULL DEFAULT 0,
  `create_time` DATETIME NOT NULL DEFAULT NOW(),
  `update_time` DATETIME NOT NULL DEFAULT NOW(),
  PRIMARY KEY (`id`),
  INDEX `fk_userpoll_user_id_idx` (`user_id` ASC),
  INDEX `fk_userpoll_poll_id_idx` (`poll_id` ASC),
  CONSTRAINT `fk_userpoll_user_id` FOREIGN KEY (`user_id`) REFERENCES `User`(`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_userpoll_poll_id` FOREIGN KEY (`poll_id`) REFERENCES `Poll`(`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
 );