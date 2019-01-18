CREATE TABLE `abuse_report` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `type` VARCHAR(256) NOT NULL DEFAULT 'CONTENT',
  `user_id` BIGINT(20) NULL DEFAULT NULL,
  `content_id` BIGINT(20) NULL DEFAULT NULL,
  `comment_id` BIGINT(20) NULL DEFAULT NULL,
  `reason` VARCHAR(512) NULL DEFAULT NULL,
  `feedback` VARCHAR(512) NULL DEFAULT NULL,
  `create_time` DATETIME NOT NULL DEFAULT NOW(),
  `update_time` DATETIME NOT NULL DEFAULT NOW(),
  `is_deleted` BIT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`));

CREATE TABLE `ad` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `type` VARCHAR(256) NOT NULL DEFAULT 'REGULAR',
  `href` VARCHAR(512) NOT NULL DEFAULT '',
  `image_url` VARCHAR(512) NOT NULL DEFAULT '',
  `total_activity` INT NOT NULL DEFAULT 0,
  `temp_activity` INT NOT NULL DEFAULT 0,
  `create_time` DATETIME NOT NULL DEFAULT NOW(),
  `update_time` DATETIME NOT NULL DEFAULT NOW(),
  `is_deleted` BIT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`));

CREATE TABLE `content` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `type` VARCHAR(256) NOT NULL DEFAULT 'POST',
  `headline` VARCHAR(256) NULL DEFAULT NULL,
  `text` LONGTEXT NULL DEFAULT NULL,
  `user_id` BIGINT(20) NOT NULL,
  `category_id` BIGINT(20) NULL DEFAULT NULL,
  `poll_id` BIGINT(20) NULL DEFAULT NULL,
  `repost_id` BIGINT(20) NULL DEFAULT NULL,
  `total_activity` INT(11) NOT NULL DEFAULT 0,
  `create_time` DATETIME NOT NULL DEFAULT NOW(),
  `update_time` DATETIME NOT NULL DEFAULT NOW(),
  `is_deleted` BIT(1) NOT NULL DEFAULT 0,
  `r_likes` INT(11) NOT NULL DEFAULT 0,
  `r_reposts` INT(11) NOT NULL DEFAULT 0,
  `r_shares` INT(11) NOT NULL DEFAULT 0,
  `r_comments` INT(11) NOT NULL DEFAULT 0,
  `r_has_video` BIT(1) NOT NULL DEFAULT 0,
  `r_username` VARCHAR(256) NULL DEFAULT NULL,
  `r_media_url` VARCHAR(256) NULL DEFAULT NULL,
  `r_tags` VARCHAR(512) NULL DEFAULT NULL,
  `r_poll_question` VARCHAR(512) NULL DEFAULT NULL,
  `r_poll_answers` LONGTEXT NULL DEFAULT NULL,
  PRIMARY KEY (`id`));

CREATE TABLE `entity_tag` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `content_id` BIGINT(20) NULL DEFAULT NULL,
  `user_id` BIGINT(20) NULL DEFAULT NULL,
  `tag_id` BIGINT(20) NOT NULL,
  `create_time` DATETIME NOT NULL DEFAULT NOW(),
  `update_time` DATETIME NOT NULL DEFAULT NOW(),
  `is_deleted` BIT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`));

CREATE TABLE `content_media` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `type` VARCHAR(256) NOT NULL DEFAULT 'IMAGE',
  `url` VARCHAR(256) NOT NULL DEFAULT '',
  `order_number` INT NOT NULL DEFAULT 0,
  `content_id` BIGINT(20) NOT NULL,
  `create_time` DATETIME NOT NULL DEFAULT NOW(),
  `update_time` DATETIME NOT NULL DEFAULT NOW(),
  `is_deleted` BIT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`));

CREATE TABLE `authentication_link` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `type` VARCHAR(256) NOT NULL DEFAULT 'SET_PASS',
  `url` VARCHAR(256) NOT NULL DEFAULT '',
  `user_id` BIGINT(20) NOT NULL,
  `expiry_time` DATETIME NOT NULL DEFAULT NOW(),
  PRIMARY KEY (`id`));

CREATE TABLE `category` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `text` VARCHAR(512) NOT NULL DEFAULT '',
  `create_time` DATETIME NOT NULL DEFAULT NOW(),
  `update_time` DATETIME NOT NULL DEFAULT NOW(),
  `is_deleted` BIT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`));

CREATE TABLE `comment` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `text` VARCHAR(512) NULL DEFAULT NULL,
  `url` VARCHAR(256) NULL DEFAULT NULL,
  `user_id` BIGINT(20) NOT NULL,
  `content_id` BIGINT(20) NOT NULL,
  `parent_comment_id` BIGINT(20) NULL DEFAULT NULL,
  `create_time` DATETIME NOT NULL DEFAULT NOW(),
  `update_time` DATETIME NOT NULL DEFAULT NOW(),
  `is_deleted` BIT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`));

CREATE TABLE `conversation` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `create_time` DATETIME NOT NULL DEFAULT NOW(),
  `update_time` DATETIME NOT NULL DEFAULT NOW(),
  `is_deleted` BIT(1) NOT NULL DEFAULT 0,
  `r_users` LONGTEXT NOT NULL,
  PRIMARY KEY (`id`));

CREATE TABLE `follow` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL,
  `follow_user_id` BIGINT(20) NULL DEFAULT NULL,
  `follow_tag_id` BIGINT(20) NULL DEFAULT NULL,
  `create_time` DATETIME NOT NULL DEFAULT NOW(),
  `update_time` DATETIME NOT NULL DEFAULT NOW(),
  `is_deleted` BIT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`));

CREATE TABLE `footer_page` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `type` VARCHAR(256) NOT NULL DEFAULT 'MENU',
  `order_number` INT NOT NULL DEFAULT 0,
  `content_id` BIGINT(20) NOT NULL,
  `create_time` DATETIME NOT NULL DEFAULT NOW(),
  `update_time` DATETIME NOT NULL DEFAULT NOW(),
  `is_deleted` BIT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`));

CREATE TABLE `message` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `text` LONGTEXT NOT NULL,
  `read_by` LONGTEXT NOT NULL,
  `user_id` BIGINT(20) NOT NULL,
  `conversation_id` BIGINT(20) NOT NULL,
  `create_time` DATETIME NOT NULL DEFAULT NOW(),
  `update_time` DATETIME NOT NULL DEFAULT NOW(),
  `is_deleted` BIT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`));

CREATE TABLE `notification` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `type` VARCHAR(256) NOT NULL DEFAULT 'FOLLOWED',
  `is_read` BIT(1) NOT NULL DEFAULT 0,
  `users` LONGTEXT NOT NULL,
  `user_id` BIGINT(20) NOT NULL,
  `content_id` BIGINT(20) NULL DEFAULT NULL,
  `comment_id` BIGINT(20) NULL DEFAULT NULL,
  `poll_id` BIGINT(20) NULL DEFAULT NULL,
  `create_time` DATETIME NOT NULL DEFAULT NOW(),
  `update_time` DATETIME NOT NULL DEFAULT NOW(),
  `is_deleted` BIT(1) NOT NULL DEFAULT 0,
  `r_username` VARCHAR(256) NULL DEFAULT NULL,
  `r_user_image_url` VARCHAR(256) NULL DEFAULT NULL,
  `r_content_headline` VARCHAR(256) NULL DEFAULT NULL,
  `r_poll_question` VARCHAR(256) NULL DEFAULT NULL,
  `r_comment` VARCHAR(256) NULL DEFAULT NULL,
  PRIMARY KEY (`id`));

CREATE TABLE `poll` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `type` VARCHAR(256) NOT NULL DEFAULT 'ARTICLE',
  `question` VARCHAR(512) NULL DEFAULT NULL,
  `first_choice` VARCHAR(512) NULL DEFAULT NULL,
  `second_choice` VARCHAR(512) NULL DEFAULT NULL,
  `third_choice` VARCHAR(512) NULL DEFAULT NULL,
  `create_time` DATETIME NOT NULL DEFAULT NOW(),
  `update_time` DATETIME NOT NULL DEFAULT NOW(),
  `is_deleted` BIT(1) NOT NULL DEFAULT 0,
  `r_first_amount` INT(11) NOT NULL DEFAULT 0,
  `r_second_amount` INT(11) NOT NULL DEFAULT 0,
  `r_third_amount` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`));

CREATE TABLE `stored_content` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL,
  `content_id` BIGINT(20) NOT NULL,
  `create_time` DATETIME NOT NULL DEFAULT NOW(),
  `update_time` DATETIME NOT NULL DEFAULT NOW(),
  `is_deleted` BIT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`));

CREATE TABLE `tag` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `type` VARCHAR(256) NOT NULL DEFAULT 'REGULAR',
  `text` VARCHAR(512) NOT NULL,
  `total_activity` INT(11) NOT NULL DEFAULT 0,
  `temp_activity` INT(11) NOT NULL DEFAULT 0,
  `create_time` DATETIME NOT NULL DEFAULT NOW(),
  `update_time` DATETIME NOT NULL DEFAULT NOW(),
  `is_deleted` BIT(1) NOT NULL DEFAULT 0,
  `r_followers` INT(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`));

CREATE TABLE `user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `role` VARCHAR(256) NOT NULL DEFAULT 'UNCONFIRMED',
  `email` VARCHAR(256) NULL UNIQUE DEFAULT NULL,
  `password` VARCHAR(128) NULL DEFAULT NULL,
  `username` VARCHAR(512) NULL DEFAULT NULL,
  `bio` VARCHAR(512) NULL DEFAULT NULL,
  `image_url` VARCHAR(256) NULL DEFAULT NULL,
  `facebook_id` VARCHAR(128) NULL DEFAULT NULL,
  `twitter_id` VARCHAR(128) NULL DEFAULT NULL,
  `google_id` VARCHAR(128) NULL DEFAULT NULL,
  `create_time` DATETIME NOT NULL DEFAULT NOW(),
  `update_time` DATETIME NOT NULL DEFAULT NOW(),
  `is_deleted` BIT(1) NOT NULL DEFAULT 0,
  `r_followers` INT(11) NOT NULL DEFAULT 0,
  `r_following` INT(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`));

CREATE TABLE `user_conversation` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL,
  `conversation_id` BIGINT(20) NOT NULL,
  `create_time` DATETIME NOT NULL DEFAULT NOW(),
  `update_time` DATETIME NOT NULL DEFAULT NOW(),
  `is_deleted` BIT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`));

CREATE TABLE `vote` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `choice` VARCHAR(256) NOT NULL DEFAULT 'FIRST',
  `user_id` BIGINT(20) NOT NULL,
  `poll_id` BIGINT(20) NOT NULL,
  `create_time` DATETIME NOT NULL DEFAULT NOW(),
  `update_time` DATETIME NOT NULL DEFAULT NOW(),
  `is_deleted` BIT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`));

CREATE TABLE `content_like` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL,
  `content_id` BIGINT(20) NOT NULL,
  `create_time` DATETIME NOT NULL DEFAULT NOW(),
  `update_time` DATETIME NOT NULL DEFAULT NOW(),
  `is_deleted` BIT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`));

/* INSERTING OF DUMMY DATA */

/* USERS WITH Codetribe123 passwords */
INSERT INTO `user` (`role`, `email`, `password`, `create_time`, `update_time`, `is_deleted`) VALUES ('ADMIN', 'admin@codetri.be', '$2a$10$XGAodLo1wR5U/qerrqPMOu8RV8evZQoSCvfHosWXTu90lOdmes0ae', '2018-01-01 01:01:01', '2018-01-01 01:01:01', 0);
INSERT INTO `user` (`role`, `email`, `password`, `create_time`, `update_time`, `is_deleted`) VALUES ('ACTIVE', 'user@codetri.be', '$2a$10$XGAodLo1wR5U/qerrqPMOu8RV8evZQoSCvfHosWXTu90lOdmes0ae', '2018-01-01 01:01:01', '2018-01-01 01:01:01', 0);
