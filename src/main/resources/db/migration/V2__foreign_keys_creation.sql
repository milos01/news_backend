ALTER TABLE `abuse_report`
ADD INDEX `fk_abuse_report_user_id_idx` (`user_id` ASC),
ADD INDEX `fk_abuse_report_comment_id_idx` (`comment_id` ASC),
ADD INDEX `fk_abuse_report_content_idx` (`content_id` ASC);
ALTER TABLE `abuse_report`
ADD CONSTRAINT `fk_abuse_report_user_id`
  FOREIGN KEY (`user_id`)
  REFERENCES `user` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_abuse_report_content_id`
  FOREIGN KEY (`content_id`)
  REFERENCES `content` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_abuse_report_comment_id`
  FOREIGN KEY (`comment_id`)
  REFERENCES `comment` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `content`
ADD INDEX `fk_content_user_id_idx` (`user_id` ASC),
ADD INDEX `fk_content_category_id_idx` (`category_id` ASC),
ADD INDEX `fk_content_poll_id_idx` (`poll_id` ASC),
ADD INDEX `fk_content_repost_id_idx` (`user_id` ASC);
ALTER TABLE `content`
ADD CONSTRAINT `fk_content_user_id`
  FOREIGN KEY (`user_id`)
  REFERENCES `user` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_content_category_id`
  FOREIGN KEY (`category_id`)
  REFERENCES `category` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_content_poll_id`
  FOREIGN KEY (`poll_id`)
  REFERENCES `poll` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_content_content_id`
  FOREIGN KEY (`repost_id`)
  REFERENCES `content` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `content_media`
ADD INDEX `fk_content_media_content_id_idx` (`content_id` ASC);
ALTER TABLE `content_media`
ADD CONSTRAINT `fk_content_media_content_id`
  FOREIGN KEY (`content_id`)
  REFERENCES `content` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `entity_tag`
ADD INDEX `fk_entity_tag_content_id_idx` (`content_id` ASC),
ADD INDEX `fk_entity_tag_user_id_idx` (`user_id` ASC),
ADD INDEX `fk_entity_tag_tag_id_idx` (`tag_id` ASC);
ALTER TABLE `entity_tag`
ADD CONSTRAINT `fk_entity_tag_content_id`
  FOREIGN KEY (`content_id`)
  REFERENCES `content` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_entity_tag_user_id`
  FOREIGN KEY (`user_id`)
  REFERENCES `user` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_entity_tag_tag_id`
  FOREIGN KEY (`tag_id`)
  REFERENCES `tag` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `authentication_link`
ADD INDEX `fk_authentication_link_user_id_idx` (`user_id` ASC);
ALTER TABLE `authentication_link`
ADD CONSTRAINT `fk_authentication_link_user_id`
  FOREIGN KEY (`user_id`)
  REFERENCES `user` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `comment`
ADD INDEX `fk_comment_user_id_idx` (`user_id` ASC),
ADD INDEX `fk_comment_content_id_idx` (`content_id` ASC),
ADD INDEX `fk_comment_comment_id_idx` (`parent_comment_id` ASC);
ALTER TABLE `comment`
ADD CONSTRAINT `fk_comment_user_id`
  FOREIGN KEY (`user_id`)
  REFERENCES `user` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_comment_content_id`
  FOREIGN KEY (`content_id`)
  REFERENCES `content` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_comment_comment_id`
  FOREIGN KEY (`parent_comment_id`)
  REFERENCES `comment` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `follow`
ADD INDEX `fk_follow_user_id_idx` (`user_id` ASC),
ADD INDEX `fk_follow_user_id2_idx` (`follow_user_id` ASC),
ADD INDEX `fk_follow_tag_id_idx` (`follow_tag_id` ASC);
ALTER TABLE `follow`
ADD CONSTRAINT `fk_follow_user_id`
  FOREIGN KEY (`user_id`)
  REFERENCES `user` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_follow_user_id2`
  FOREIGN KEY (`follow_user_id`)
  REFERENCES `user` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_follow_tag_id`
  FOREIGN KEY (`follow_tag_id`)
  REFERENCES `tag` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `footer_page`
ADD INDEX `fk_footer_page_content_id_idx` (`content_id` ASC);
ALTER TABLE `footer_page`
ADD CONSTRAINT `fk_footer_page_content_id`
  FOREIGN KEY (`content_id`)
  REFERENCES `content` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `message`
ADD INDEX `fk_message_user_id_idx` (`user_id` ASC),
ADD INDEX `fk_message_conversation_id_idx` (`conversation_id` ASC);
ALTER TABLE `message`
ADD CONSTRAINT `fk_message_user_id`
  FOREIGN KEY (`user_id`)
  REFERENCES `user` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_message_conversation_id`
  FOREIGN KEY (`conversation_id`)
  REFERENCES `conversation` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `notification`
ADD INDEX `fk_notification_user_id_idx` (`user_id` ASC),
ADD INDEX `fk_notification_content_id_idx` (`content_id` ASC),
ADD INDEX `fk_notification_comment_id_idx` (`comment_id` ASC),
ADD INDEX `fk_notification_poll_id_idx` (`poll_id` ASC);
ALTER TABLE `notification`
ADD CONSTRAINT `fk_notification_user_id`
  FOREIGN KEY (`user_id`)
  REFERENCES `user` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_notification_content_id`
  FOREIGN KEY (`content_id`)
  REFERENCES `content` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_notification_comment_id`
  FOREIGN KEY (`comment_id`)
  REFERENCES `comment` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_notification_poll_id`
  FOREIGN KEY (`poll_id`)
  REFERENCES `poll` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `stored_content`
ADD INDEX `fk_stored_content_user_id_idx` (`user_id` ASC),
ADD INDEX `fk_stored_content_content_id_idx` (`content_id` ASC);
ALTER TABLE `stored_content`
ADD CONSTRAINT `fk_stored_content_user_id`
  FOREIGN KEY (`user_id`)
  REFERENCES `user` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_stored_content_content_id`
  FOREIGN KEY (`content_id`)
  REFERENCES `content` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `user_conversation`
ADD INDEX `fk_user_conversation_user_id_idx` (`user_id` ASC),
ADD INDEX `fk_user_conversation_conversation_id_idx` (`conversation_id` ASC);
ALTER TABLE `user_conversation`
ADD CONSTRAINT `fk_user_conversation_user_id`
  FOREIGN KEY (`user_id`)
  REFERENCES `user` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_user_conversation_conversation_id`
  FOREIGN KEY (`conversation_id`)
  REFERENCES `conversation` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `vote`
ADD INDEX `fk_vote_user_id_idx` (`user_id` ASC),
ADD INDEX `fk_vote_poll_id_idx` (`poll_id` ASC);
ALTER TABLE `vote`
ADD CONSTRAINT `fk_vote_user_id`
  FOREIGN KEY (`user_id`)
  REFERENCES `user` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_vote_poll_id`
  FOREIGN KEY (`poll_id`)
  REFERENCES `poll` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `content_like`
ADD INDEX `fk_like_user_id_idx` (`user_id` ASC),
ADD INDEX `fk_like_content_id_idx` (`content_id` ASC);
ALTER TABLE `content_like`
ADD CONSTRAINT `fk_like_user_id`
  FOREIGN KEY (`user_id`)
  REFERENCES `user` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_like_content_id`
  FOREIGN KEY (`content_id`)
  REFERENCES `content` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;