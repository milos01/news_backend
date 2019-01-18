package init.app.domain.repository;

import init.app.domain.model.Content;
import init.app.domain.model.User;
import init.app.domain.model.enumeration.ContentType;
import init.app.web.dto.response.ContentPollResponseDto;
import init.app.web.dto.response.SingleContentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

    @Query(value = "SELECT content.id, headline, text, content.create_time, r_likes, r_reposts, r_shares, r_comments, r_has_video, r_username, r_user_role, r_user_image_url, r_media_url, content.r_tags, r_poll_question, r_poll_answers, content.user_id, repost_id, content.poll_id FROM content WHERE content.id = :id AND content.is_deleted = FALSE",
            nativeQuery = true)
    Object[] findShortById(@Param("id") Long id);

    @Query(value = "SELECT DISTINCT content.id, content.headline, content.text, content.create_time, content.r_likes, content.r_reposts, content.r_shares, content.r_comments, content.r_has_video, content.r_username, content.r_user_role, content.r_user_image_url, content.r_media_url, content.r_tags, content.r_poll_question, content.r_poll_answers, content.r_reactions, content.user_id, content.repost_id, content.poll_id, IF(user_poll.id IS NULL, FALSE, TRUE), IF(content_like.id IS NULL, FALSE, TRUE), IF(vote.id IS NULL, FALSE, TRUE), r_followers, r_following, IF(follow.id IS NULL, FALSE, TRUE), IF(stored_content.id IS NULL, FALSE, TRUE), IF(comment.id IS NULL, FALSE, TRUE), IF(repost.id IS NULL, FALSE, TRUE) " +
            "FROM content " +
            "LEFT JOIN comment on content.id = comment.content_id AND comment.user_id = :userId AND comment.is_deleted = FALSE " +
            "LEFT JOIN content repost ON content.id = repost.repost_id AND repost.user_id = :userId AND repost.is_deleted = FALSE " +
            "LEFT JOIN content_like ON content_like.content_id = content.id AND content_like.user_id = :userId AND content_like.is_deleted = FALSE " +
            "LEFT JOIN user_poll ON user_poll.poll_id = content.poll_id AND user_poll.user_id = :userId " +
            "LEFT JOIN vote ON vote.user_id = :userId AND vote.poll_id = content.poll_id AND vote.is_deleted = FALSE " +
            "LEFT JOIN follow ON follow.user_id = :userId AND follow.follow_user_id = content.user_id AND follow.is_deleted = FALSE " +
            "LEFT JOIN stored_content ON content.id = stored_content.content_id AND stored_content.user_id = :userId AND stored_content.is_deleted = false " +
            "JOIN user ON content.user_id = user.id " +
            "WHERE content.is_deleted = FALSE AND content.id = :id",
            nativeQuery = true)
    Object[] findByIdNative(@Param("id") Long id, @Param("userId") Long userId);

    Content findById(Long id);

    Content findByPollId(Long id);
    @Query(value = "SELECT DISTINCT content.id, content.headline, content.text, content.create_time, content.r_likes, content.r_reposts, content.r_shares, content.r_comments, content.r_has_video, content.r_username, content.r_user_role, content.r_user_image_url, content.r_media_url, content.r_tags, content.r_poll_question, content.r_poll_answers, content.r_reactions, content.user_id, content.repost_id, content.poll_id, IF(user_poll.id IS NULL, FALSE, TRUE), IF(content_like.id IS NULL, FALSE, TRUE), IF(vote.id IS NULL, FALSE, TRUE), r_followers, r_following, IF(follow.id IS NULL, FALSE, TRUE), IF(stored_content.id IS NULL, FALSE, TRUE), IF(comment.id IS NULL, FALSE, TRUE), IF(repost.id IS NULL, FALSE, TRUE) " +
            "FROM content " +
            "LEFT JOIN comment on content.id = comment.content_id AND comment.user_id = :userId AND comment.is_deleted = FALSE " +
            "LEFT JOIN content repost ON content.id = repost.repost_id AND repost.user_id = :userId AND repost.is_deleted = FALSE " +
            "LEFT JOIN content_like ON content_like.content_id = content.id AND content_like.user_id = :userId AND content_like.is_deleted = FALSE " +
            "LEFT JOIN user_poll ON user_poll.poll_id = content.poll_id AND user_poll.user_id = :userId " +
            "JOIN user ON content.user_id = user.id " +
            "LEFT JOIN follow ON follow.user_id = :userId AND follow.follow_user_id = content.user_id AND follow.is_deleted = FALSE " +
            "LEFT JOIN stored_content ON content.id = stored_content.content_id AND stored_content.user_id = :userId AND stored_content.is_deleted = false " +
            "LEFT JOIN vote ON vote.user_id = :userId AND vote.poll_id = content.poll_id AND vote.is_deleted = FALSE " +
            "WHERE user.id = :userId AND user.is_deleted = FALSE AND content.is_deleted = FALSE " +
            "ORDER BY content.create_time DESC " +
            "LIMIT :limitParam OFFSET :offsetParam",
            countQuery = "SELECT content.id " +
                    "FROM content " +
                    "LEFT JOIN content_like ON content_like.content_id = content.id AND content_like.user_id = :userId AND content_like.is_deleted = FALSE " +
                    "JOIN user ON content.user_id = user.id " +
                    "LEFT JOIN follow ON follow.user_id = :userId AND follow.follow_user_id = content.user_id AND follow.is_deleted = FALSE " +
                    "LEFT JOIN vote ON vote.user_id = :userId AND vote.poll_id = content.poll_id AND vote.is_deleted = FALSE " +
                    "WHERE user.id = :userId AND user.is_deleted = FALSE AND content.is_deleted = FALSE " +
                    "ORDER BY content.create_time DESC",
            nativeQuery = true)
    List<Object[]> getUserContent(@Param("userId") Long userId, @Param("limitParam") Integer limit, @Param("offsetParam") Integer offset);

    @Query(value = "SELECT DISTINCT content.id, content.headline, content.text, content.create_time, content.r_likes, content.r_reposts, content.r_shares, content.r_comments, content.r_has_video, content.r_username, content.r_user_role, content.r_user_image_url, content.r_media_url, content.r_tags, content.r_poll_question, content.r_poll_answers, content.r_reactions, content.user_id, content.repost_id, content.poll_id, IF(logged_in_user_like.id IS NULL, FALSE, TRUE), IF(logged_in_user_vote.id IS NULL, FALSE, TRUE), r_followers, r_following, IF(follow.id IS NULL, FALSE, TRUE), IF(stored_content.id IS NULL, FALSE, TRUE), IF(comment.id IS NULL, FALSE, TRUE), IF(repost.id IS NULL, FALSE, TRUE) \n" +
            "FROM content \n" +
            "LEFT JOIN comment on content.id = comment.content_id AND comment.user_id = :userId AND comment.is_deleted = FALSE " +
            "LEFT JOIN content repost ON content.id = repost.repost_id AND repost.user_id = :userId AND repost.is_deleted = FALSE " +
            "LEFT JOIN content_like logged_in_user_like ON logged_in_user_like.content_id = content.id AND logged_in_user_like.user_id = :loggedUserId AND logged_in_user_like.is_deleted = FALSE \n" +
            "LEFT JOIN vote logged_in_user_vote ON logged_in_user_vote.user_id = :loggedUserId AND logged_in_user_vote.poll_id = content.poll_id AND logged_in_user_vote.is_deleted = FALSE \n" +
            "JOIN user ON content.user_id = user.id \n" +
            "LEFT JOIN stored_content ON content.id = stored_content.content_id AND stored_content.user_id = :loggedUserId AND stored_content.is_deleted = false " +
            "INNER JOIN vote user_vote ON user_vote.poll_id = content.poll_id AND user_vote.user_id = :userId AND user_vote.is_deleted = FALSE \n" +
            "LEFT JOIN follow ON follow.user_id = :userId AND follow.follow_user_id = content.user_id AND follow.is_deleted = FALSE " +
            "WHERE content.is_deleted = FALSE AND content.type = 'ARTICLE_REGULAR' " +
            "ORDER BY content.create_time DESC " +
            "LIMIT :limitParam OFFSET :offsetParam",
            countQuery = "SELECT DISTINCT content.id \n" +
                    "FROM content \n" +
                    "LEFT JOIN content_like logged_in_user_like ON logged_in_user_like.content_id = content.id AND logged_in_user_like.user_id = :loggedUserId AND logged_in_user_like.is_deleted = FALSE \n" +
                    "LEFT JOIN vote logged_in_user_vote ON logged_in_user_vote.user_id = :loggedUserId AND logged_in_user_vote.poll_id = content.poll_id AND logged_in_user_vote.is_deleted = FALSE \n" +
                    "JOIN user ON content.user_id = user.id \n" +
                    "INNER JOIN vote user_vote ON user_vote.poll_id = content.poll_id AND user_vote.user_id = :userId AND user_vote.is_deleted = FALSE \n" +
                    "WHERE content.is_deleted = FALSE AND content.type = 'ARTICLE_REGULAR' ORDER BY content.create_time DESC",
            nativeQuery = true)
    List<Object[]> getUserContentVoted(@Param("userId") Long userId, @Param("loggedUserId") Long loggedUserId, @Param("limitParam") Integer limit, @Param("offsetParam") Integer offset);

    @Query(value = "SELECT DISTINCT content.id, content.headline, content.text, content.create_time, content.r_likes, content.r_reposts, content.r_shares, content.r_comments, content.r_has_video, content.r_username, content.r_user_role, content.r_user_image_url, content.r_media_url, content.r_tags, content.r_poll_question, content.r_poll_answers, content.r_reactions, content.user_id, content.repost_id, content.poll_id, IF(logged_in_user_like.id IS NULL, FALSE, TRUE), IF(logged_in_user_vote.id IS NULL, FALSE, TRUE), r_followers, r_following, IF(follow.id IS NULL, FALSE, TRUE), IF(stored_content.id IS NULL, FALSE, TRUE), IF(comment.id IS NULL, FALSE, TRUE), IF(repost.id IS NULL, FALSE, TRUE) \n" +
            "FROM content \n" +
            "LEFT JOIN comment on content.id = comment.content_id AND comment.user_id = :userId AND comment.is_deleted = FALSE " +
            "LEFT JOIN content repost ON content.id = repost.repost_id AND repost.user_id = :userId AND repost.is_deleted = FALSE " +
            "LEFT JOIN content_like logged_in_user_like ON logged_in_user_like.content_id = content.id AND logged_in_user_like.user_id = :userId AND logged_in_user_like.is_deleted = FALSE \n" +
            "LEFT JOIN vote logged_in_user_vote ON logged_in_user_vote.user_id = :userId AND logged_in_user_vote.poll_id = content.poll_id AND logged_in_user_vote.is_deleted = FALSE \n" +
            "JOIN user ON content.user_id = user.id \n" +
            "LEFT JOIN stored_content ON content.id = stored_content.content_id AND stored_content.user_id = :userId AND stored_content.is_deleted = false " +
            "LEFT JOIN follow ON follow.user_id = :userId AND follow.follow_user_id = content.user_id AND follow.is_deleted = FALSE " +
            "INNER JOIN vote user_vote ON user_vote.poll_id = content.poll_id AND user_vote.user_id = :userId AND user_vote.is_deleted = FALSE \n" +
            "WHERE content.is_deleted = FALSE " +
            "ORDER BY content.create_time DESC " +
            "LIMIT :limitParam OFFSET :offsetParam",
            countQuery = "SELECT DISTINCT content.id \n" +
                    "FROM content \n" +
                    "INNER JOIN content_like logged_in_user_like ON logged_in_user_like.content_id = content.id AND logged_in_user_like.user_id = :userId AND logged_in_user_like.is_deleted = FALSE \n" +
                    "LEFT JOIN vote logged_in_user_vote ON logged_in_user_vote.user_id = :userId AND logged_in_user_vote.poll_id = content.poll_id AND logged_in_user_vote.is_deleted = FALSE \n" +
                    "JOIN user ON content.user_id = user.id \n" +
                    "WHERE content.is_deleted = FALSE ORDER BY content.create_time DESC",
            nativeQuery = true)
    List<Object[]> getUserContentLiked(@Param("userId") Long userId, @Param("limitParam") Integer limit, @Param("offsetParam") Integer offset);

    @Query(value = "SELECT DISTINCT content.id, content.headline, content.text, content.create_time, content.r_likes, content.r_reposts, content.r_shares, content.r_comments, content.r_has_video, content.r_username, content.r_user_role, content.r_user_image_url, content.r_media_url, content.r_tags, content.r_poll_question, content.r_poll_answers, content.r_reactions, content.user_id, content.repost_id, content.poll_id, IF(content_like.id IS NULL, FALSE, TRUE), IF(vote.id IS NULL, FALSE, TRUE), r_followers, r_following, IF(follow.id IS NULL, FALSE, TRUE), IF(stored_content.id IS NULL, FALSE, TRUE), IF(comment.id IS NULL, FALSE, TRUE), IF(repost.id IS NULL, FALSE, TRUE) " +
            "FROM content " +
            "LEFT JOIN comment on content.id = comment.content_id AND comment.user_id = :userId AND comment.is_deleted = FALSE " +
            "LEFT JOIN content repost ON content.id = repost.repost_id AND repost.user_id = :userId AND repost.is_deleted = FALSE " +
            "LEFT JOIN content_like ON content_like.content_id = content.id AND content_like.user_id = :loggedUserId AND content_like.is_deleted = FALSE " +
            "JOIN user ON content.user_id = user.id " +
            "LEFT JOIN stored_content ON stored_content.content_id = content.id AND stored_content.user_id = :loggedUserId AND stored_content.is_deleted = FALSE " +
            "INNER JOIN notification n ON content.id = n.content_id AND (n.r_users LIKE CONCAT('%/', :userId, '|%') OR n.r_users LIKE CONCAT('%/', :userId)) AND n.type = 'SHARED' " +
            "LEFT JOIN follow ON follow.user_id = :loggedUserId AND follow.follow_user_id = content.user_id AND follow.is_deleted = FALSE " +
            "LEFT JOIN vote ON vote.user_id = :loggedUserId AND vote.poll_id = content.poll_id AND vote.is_deleted = FALSE " +
            "WHERE content.is_deleted = FALSE AND content.type = 'ARTICLE_REGULAR' " +
            "ORDER BY content.create_time DESC " +
            "LIMIT :limitParam OFFSET :offsetParam",
            countQuery ="SELECT content.id " +
                    "FROM content " +
                    "LEFT JOIN content_like ON content_like.content_id = content.id AND content_like.user_id = :loggedUserId AND content_like.is_deleted = FALSE " +
                    "JOIN user ON content.user_id = user.id " +
                    "INNER JOIN notification n ON content.id = n.content_id AND (n.r_users LIKE CONCAT('%/', :userId, '|%') OR n.r_users LIKE CONCAT('%/', :userId)) AND n.type = 'SHARED' " +
                    "LEFT JOIN follow ON follow.user_id = :loggedUserId AND follow.follow_user_id = content.user_id AND follow.is_deleted = FALSE " +
                    "LEFT JOIN vote ON vote.user_id = :loggedUserId AND vote.poll_id = content.poll_id AND vote.is_deleted = FALSE " +
                    "WHERE content.is_deleted = FALSE AND content.type = 'ARTICLE_REGULAR' " +
                    "ORDER BY content.create_time DESC",
            nativeQuery = true)
    List<Object[]> getUserContentShared(@Param("loggedUserId") Long loggedUserId, @Param("userId") String userId, @Param("limitParam") Integer limit, @Param("offsetParam") Integer offset);

    @Query(value = "SELECT DISTINCT content.id, content.headline, content.text, content.create_time, content.r_likes, content.r_reposts, content.r_shares, content.r_comments, content.r_has_video, content.r_username, content.r_user_role, content.r_user_image_url, content.r_media_url, content.r_tags, content.r_poll_question, content.r_poll_answers, content.r_reactions, content.user_id, content.repost_id, content.poll_id, IF(content_like.id IS NULL, FALSE, TRUE), IF(vote.id IS NULL, FALSE, TRUE), r_followers, r_following, IF(follow.id IS NULL, FALSE, TRUE), IF(stored_content.id IS NULL, FALSE, TRUE) " +
            "FROM content " +
            "LEFT JOIN comment on content.id = comment.content_id AND comment.user_id = :userId AND comment.is_deleted = FALSE " +
            "LEFT JOIN content repost ON content.id = repost.repost_id AND repost.user_id = :userId AND repost.is_deleted = FALSE " +
            "LEFT JOIN content_like ON content_like.content_id = content.id AND content_like.user_id = :userId AND content_like.is_deleted = FALSE " +
            "JOIN user ON content.user_id = user.id " +
            "LEFT JOIN vote ON vote.user_id = :userId AND vote.poll_id = content.poll_id AND vote.is_deleted = FALSE " +
            "LEFT JOIN follow ON follow.user_id = :userId AND follow.follow_user_id = content.user_id AND follow.is_deleted = FALSE " +
            "INNER JOIN stored_content ON content.id = stored_content.content_id AND stored_content.user_id = :userId AND stored_content.is_deleted = false " +
            "WHERE content.is_deleted = FALSE AND content.type = 'ARTICLE_REGULAR' " +
            "ORDER BY content.create_time DESC " +
            "LIMIT :limitParam OFFSET :offsetParam",
            countQuery = "SELECT content.id " +
                    "FROM content " +
                    "LEFT JOIN content_like ON content_like.content_id = content.id AND content_like.user_id = :userId AND content_like.is_deleted = FALSE " +
                    "JOIN user ON content.user_id = user.id " +
                    "LEFT JOIN vote ON vote.user_id = :userId AND vote.poll_id = content.poll_id AND vote.is_deleted = FALSE " +
                    "LEFT JOIN follow ON follow.user_id = :userId AND follow.follow_user_id = content.user_id AND follow.is_deleted = FALSE " +
                    "INNER JOIN stored_content ON content.id = stored_content.content_id AND stored_content.user_id = :userId AND stored_content.is_deleted = false " +
                    "WHERE content.is_deleted = FALSE AND content.type = 'ARTICLE_REGULAR'",
            nativeQuery = true)
    List<Object[]> getUserContentStored(@Param("userId") Long userId, @Param("limitParam") Integer limit, @Param("offsetParam") Integer offset);

    //Stream of ARTICLES having the tags I follow
    @Query(value = "SELECT DISTINCT content.id, content.headline, content.text, content.create_time, content.r_likes, content.r_reposts, content.r_shares, content.r_comments, content.r_has_video, content.r_username, content.r_user_role, content.r_user_image_url, content.r_media_url, content.r_tags, content.r_poll_question, content.r_poll_answers, content.r_reactions, content.user_id, content.repost_id, content.poll_id, IF(user_poll.id IS NULL, FALSE, TRUE), IF(content_like.id IS NULL, FALSE, TRUE), IF(vote.id IS NULL, FALSE, TRUE), r_followers, r_following, IF(follow.id IS NULL, FALSE, TRUE), IF(stored_content.id IS NULL, FALSE, TRUE), IF(comment.id IS NULL, FALSE, TRUE), IF(repost.id IS NULL, FALSE, TRUE) " +
            "FROM content " +
            "LEFT JOIN content repost ON content.id = repost.repost_id AND repost.user_id = :userId AND repost.is_deleted = FALSE " +
            "LEFT JOIN comment on content.id = comment.content_id AND comment.user_id = :userId AND comment.is_deleted = FALSE " +
            "LEFT JOIN content_like ON content_like.content_id = content.id AND content_like.user_id = :userId AND content_like.is_deleted = FALSE " +
            "LEFT JOIN user_poll ON user_poll.poll_id = content.poll_id AND user_poll.user_id = :userId " +
            "JOIN user ON content.user_id = user.id LEFT JOIN entity_tag ON entity_tag.content_id = content.id AND entity_tag.is_deleted = FALSE " +
            "LEFT JOIN stored_content ON content.id = stored_content.content_id AND stored_content.user_id = :userId AND stored_content.is_deleted = false " +
            "INNER JOIN follow ON (follow.follow_tag_id = entity_tag.tag_id OR follow.follow_user_id = content.user_id) AND follow.is_deleted = FALSE AND follow.user_id = :userId " +
            "LEFT JOIN vote ON vote.user_id = :userId AND vote.poll_id = content.poll_id AND vote.is_deleted = FALSE WHERE content.is_deleted = FALSE AND content.type = 'POST' " +
            "ORDER BY content.create_time DESC " +
            "LIMIT :limitParam OFFSET :offsetParam",
            countQuery = "SELECT DISTINCT content.* FROM content LEFT JOIN content_like ON content_like.content_id = content.id AND content_like.user_id = :userId AND content_like.is_deleted = FALSE JOIN user ON content.user_id = user.id LEFT JOIN entity_tag ON entity_tag.content_id = content.id AND entity_tag.is_deleted = FALSE INNER JOIN follow ON (follow.follow_tag_id = entity_tag.tag_id OR follow.follow_user_id = content.user_id) AND follow.is_deleted = FALSE AND follow.user_id = :userId WHERE content.is_deleted = FALSE",
            nativeQuery = true)
    List<Object[]> getCommunity(@Param("userId") Long userId, @Param("limitParam") Integer limit, @Param("offsetParam") Integer offset);

    //Stream of POSTS from the people I follow
    @Query(value = "SELECT DISTINCT content.id, content.headline, content.text, content.create_time, content.r_likes, content.r_reposts, content.r_shares, content.r_comments, content.r_has_video, content.r_username, content.r_user_role, content.r_user_image_url, content.r_media_url, content.r_tags, content.r_poll_question, content.r_poll_answers, content.r_reactions, content.user_id, content.repost_id, content.poll_id, IF(user_poll.id IS NULL, FALSE, TRUE), IF(content_like.id IS NULL, FALSE, TRUE), IF(vote.id IS NULL, FALSE, TRUE), r_followers, r_following, IF(follow.id IS NULL, FALSE, TRUE), IF(stored_content.id IS NULL, FALSE, TRUE), IF(comment.id IS NULL, FALSE, TRUE), IF(repost.id IS NULL, FALSE, TRUE) " +
            "FROM content " +
            "LEFT JOIN comment on content.id = comment.content_id AND comment.user_id = :userId AND comment.is_deleted = FALSE " +
            "LEFT JOIN content repost ON content.id = repost.repost_id AND repost.user_id = :userId AND repost.is_deleted = FALSE " +
            "LEFT JOIN content_like ON content_like.content_id = content.id AND content_like.user_id = :userId AND content_like.is_deleted = FALSE " +
            "LEFT JOIN user_poll ON user_poll.poll_id = content.poll_id AND user_poll.user_id = :userId " +
            "LEFT JOIN user_poll ON user_poll.poll_id = content.poll_id AND user_poll.user_id = :userId " +
            "LEFT JOIN vote ON vote.user_id = :userId AND vote.poll_id = content.poll_id AND vote.is_deleted = FALSE " +
            "LEFT JOIN follow ON follow.user_id = :userId AND follow.follow_user_id = content.user_id AND follow.is_deleted = FALSE " +
            "JOIN user ON content.user_id = user.id " +
            "LEFT JOIN stored_content ON content.id = stored_content.content_id AND stored_content.user_id = :userId AND stored_content.is_deleted = false " +
            "WHERE content.is_deleted = FALSE AND content.type = :type " +
            "ORDER BY content.create_time DESC " +
            "LIMIT :limitParam OFFSET :offsetParam",
            countQuery =  "SELECT content.id " +
                    "FROM content " +
                    "LEFT JOIN content_like ON content_like.content_id = content.id AND content_like.user_id = :userId AND content_like.is_deleted = FALSE " +
                    "LEFT JOIN vote ON vote.user_id = :userId AND vote.poll_id = content.poll_id AND vote.is_deleted = FALSE " +
                    "LEFT JOIN follow ON follow.user_id = :userId AND follow.follow_user_id = content.user_id AND follow.is_deleted = FALSE " +
                    "JOIN user ON content.user_id = user.id " +
                    "WHERE content.is_deleted = FALSE AND content.type = :type " +
                    "ORDER BY content.create_time DESC",
            nativeQuery = true)
    List<Object[]> getStream(@Param("userId") Long userId, @Param("type") String type, @Param("limitParam") Integer limit, @Param("offsetParam") Integer offset);

    @Query(value = "SELECT content.id, headline, text, content.create_time, r_likes, r_reposts, r_shares, r_comments, r_has_video, r_username, r_user_image_url, r_media_url, content.r_tags, r_poll_question, r_poll_answers, r_reactions, content.user_id, repost_id, content.poll_id, IF(content_like.id IS NULL, FALSE, TRUE), IF(vote.id IS NULL, FALSE, TRUE), r_followers, r_following, IF(follow.id IS NULL, FALSE, TRUE), IF(stored_content.id IS NULL, FALSE, TRUE), content.category_id " +
            "FROM content " +
            "LEFT JOIN content_like ON content_like.content_id = content.id AND content_like.user_id = :userId AND content_like.is_deleted = FALSE " +
            "LEFT JOIN vote ON vote.user_id = :userId AND vote.poll_id = content.poll_id AND vote.is_deleted = FALSE " +
            "LEFT JOIN follow ON follow.user_id = :userId AND follow.follow_user_id = content.user_id AND follow.is_deleted = FALSE " +
            "JOIN user ON content.user_id = user.id " +
            "JOIN poll ON content.poll_id = poll.id AND poll.type = 'OF_THE_DAY' " +
            "LEFT JOIN stored_content ON content.id = stored_content.content_id AND stored_content.user_id = :userId AND stored_content.is_deleted = false " +
            "WHERE content.is_deleted = FALSE " +
            "LIMIT 1",
            nativeQuery = true)
    Object[] getPollOfTheDay(@Param("userId") Long userId);

    @Query(value = "SELECT content.id, headline, text, content.create_time, r_likes, r_reposts, r_shares, r_comments, r_has_video, r_username, r_user_role, r_user_image_url, r_media_url, content.r_tags, r_poll_question, r_poll_answers, r_reactions, content.user_id, repost_id, content.poll_id, IF(user_poll.id IS NULL, FALSE, TRUE), IF(content_like.id IS NULL, FALSE, TRUE), IF(vote.id IS NULL, FALSE, TRUE), r_followers, r_following, IF(follow.id IS NULL, FALSE, TRUE), IF(stored_content.id IS NULL, FALSE, TRUE) " +
            "FROM content " +
            "LEFT JOIN content_like ON content_like.content_id = content.id AND content_like.user_id = :userId AND content_like.is_deleted = FALSE " +
            "LEFT JOIN user_poll ON user_poll.poll_id = content.poll_id AND user_poll.user_id = :userId " +
            "LEFT JOIN vote ON vote.user_id = :userId AND vote.poll_id = content.poll_id AND vote.is_deleted = FALSE " +
            "LEFT JOIN follow ON follow.user_id = :userId AND follow.follow_user_id = content.user_id AND follow.is_deleted = FALSE " +
            "JOIN user ON content.user_id = user.id " +
            "LEFT JOIN stored_content ON content.id = stored_content.content_id AND stored_content.user_id = :userId AND stored_content.is_deleted = false " +
            "LEFT JOIN poll ON poll.id = content.poll_id " +
            "WHERE IF(poll.id IS NOT NULL, poll.type != 'OF_THE_DAY', content.id = content.id) AND IF(:type IS NOT NULL, content.type = :type, content.type = 'ARTICLE_REGULAR') AND IF(:categoryId IS NOT NULL, category_id = :categoryId, content.id = content.id) AND IF(:keyword IS NOT NULL, (LOWER(headline) LIKE LOWER(CONCAT('%',:keyword, '%')) OR LOWER(text) LIKE LOWER(CONCAT('%',:keyword, '%')) OR LOWER(content.r_tags) LIKE LOWER(CONCAT('%',:keyword, '%'))), content.id = content.id) AND IF(:isDeleted IS NOT NULL, content.is_deleted= :isDeleted, content.is_deleted = FALSE) AND IF(:hasVideo IS NOT NULL, content.r_has_video= :hasVideo, content.id = content.id) " +
            "ORDER BY content.create_time DESC, ?#{#pageable}",
            countQuery = "SELECT content.id FROM content " +
                    "LEFT JOIN content_like ON content_like.content_id = content.id AND content_like.user_id = :userId AND content_like.is_deleted = FALSE " +
                    "LEFT JOIN vote ON vote.user_id = :userId AND vote.poll_id = content.poll_id AND vote.is_deleted = FALSE " +
                    "LEFT JOIN follow ON follow.user_id = :userId AND follow.follow_user_id = content.user_id AND follow.is_deleted = FALSE " +
                    "JOIN user ON content.user_id = user.id " +
                    "LEFT JOIN poll ON poll.id = content.poll_id " +
                    "WHERE IF(poll.id IS NOT NULL, poll.type != 'OF_THE_DAY', content.id = content.id) AND IF(:type IS NOT NULL, content.type = :type, content.type = 'ARTICLE_REGULAR') AND IF(:categoryId IS NOT NULL, category_id = :categoryId, content.id = content.id) AND IF(:keyword IS NOT NULL, (LOWER(headline) LIKE LOWER(CONCAT('%',:keyword, '%')) OR LOWER(text) LIKE LOWER(CONCAT('%',:keyword, '%')) OR LOWER(content.r_tags) LIKE LOWER(CONCAT('%',:keyword, '%'))), content.id = content.id) AND IF(:isDeleted IS NOT NULL, content.is_deleted= :isDeleted, content.is_deleted = FALSE) AND IF(:hasVideo IS NOT NULL, content.r_has_video= :hasVideo, content.id = content.id) " +
                    "ORDER BY content.create_time DESC, ?#{#pageable}",
            nativeQuery = true)
    Page<Object[]> getAll(@Param("type") String type, @Param("keyword") String keyword, @Param("isDeleted") Boolean isDeleted, @Param("userId") Long userId, @Param("categoryId") Long categoryId, @Param("hasVideo") Boolean hasVideo, Pageable pageable);

    @Query(value = "SELECT COUNT(c.id) FROM content c WHERE c.is_deleted = FALSE and c.type = :typeParam ",
            nativeQuery = true)
    Integer countAll(@Param("typeParam") String typeParam);

    @Query(value = "SELECT content.id, headline, text, content.create_time, r_likes, r_reposts, r_shares, r_comments, r_has_video, r_username, r_user_role, r_user_image_url, r_media_url, content.r_tags, r_poll_question, r_poll_answers, r_reactions, content.user_id, repost_id, content.poll_id, IF(user_poll.id IS NULL, FALSE, TRUE), IF(content_like.id IS NULL, FALSE, TRUE), IF(vote.id IS NULL, FALSE, TRUE), r_followers, r_following, IF(follow.id IS NULL, FALSE, TRUE), IF(stored_content.id IS NULL, FALSE, TRUE), COUNT(content.id) " +
            "FROM content " +
            "LEFT JOIN content_like ON content_like.content_id = content.id AND content_like.user_id = :userId AND content_like.is_deleted = FALSE " +
            "LEFT JOIN user_poll ON user_poll.poll_id = content.poll_id AND user_poll.user_id = :userId " +
            "LEFT JOIN vote ON vote.user_id = :userId AND vote.poll_id = content.poll_id AND vote.is_deleted = FALSE " +
            "LEFT JOIN follow ON follow.user_id = :userId AND follow.follow_user_id = content.user_id AND follow.is_deleted = FALSE " +
            "JOIN user ON content.user_id = user.id " +
            "LEFT JOIN stored_content ON content.id = stored_content.content_id AND stored_content.user_id = :userId AND stored_content.is_deleted = false " +
            "INNER JOIN entity_tag ON content.id = entity_tag.content_id AND (IF(:firstTagId IS NOT NULL, entity_tag.tag_id = :firstTagId, content.id = null) OR IF(:secondTagId IS NOT NULL, entity_tag.tag_id = :secondTagId, content.id = null) OR IF(:thirdTagId IS NOT NULL, entity_tag.tag_id = :thirdTagId, content.id = null)) " +
            "WHERE content.is_deleted = FALSE AND content.type = 'ARTICLE_REGULAR' AND content.id != :contentId " +
            "GROUP BY content.id " +
            "ORDER BY content.create_time DESC, ?#{#pageable}",
            countQuery = "SELECT content.id " +
                    "FROM content " +
                    "LEFT JOIN content_like ON content_like.content_id = content.id AND content_like.user_id = :userId AND content_like.is_deleted = FALSE " +
                    "LEFT JOIN vote ON vote.user_id = :userId AND vote.poll_id = content.poll_id AND vote.is_deleted = FALSE " +
                    "LEFT JOIN follow ON follow.user_id = :userId AND follow.follow_user_id = content.user_id AND follow.is_deleted = FALSE " +
                    "JOIN user ON content.user_id = user.id " +
                    "INNER JOIN entity_tag ON content.id = entity_tag.content_id AND (IF(:firstTagId IS NOT NULL, entity_tag.tag_id = :firstTagId, content.id = null) OR IF(:secondTagId IS NOT NULL, entity_tag.tag_id = :secondTagId, content.id = null) OR IF(:thirdTagId IS NOT NULL, entity_tag.tag_id = :thirdTagId, content.id = null)) " +
                    "WHERE content.is_deleted = FALSE AND content.type = 'ARTICLE_REGULAR' AND content.id != :contentId " +
                    "GROUP BY content.id " +
                    "ORDER BY ?#{#pageable}",
            nativeQuery = true)
    Page<Object[]> getRecommended(@Param("userId") Long userId, @Param("contentId") Long contentId, @Param("firstTagId") Long firstTagId, @Param("secondTagId") Long secondTagId, @Param("thirdTagId") Long thirdTagId, Pageable pageable);

    @Query(value = "SELECT DISTINCT content.id, content.headline, content.text, content.update_time, content.r_likes, content.r_reposts, content.r_shares, content.r_comments, content.r_has_video, content.r_username, content.r_user_role, content.r_user_image_url, content.r_media_url, content.r_tags, content.r_poll_question, content.r_poll_answers, content.r_reactions, content.user_id, content.repost_id, content.poll_id, IF(content_like.id IS NULL, FALSE, TRUE), IF(vote.id IS NULL, FALSE, TRUE), r_followers, r_following, IF(follow.id IS NULL, FALSE, TRUE), IF(stored_content.id IS NULL, FALSE, TRUE), IF(comment.id IS NULL, FALSE, TRUE), IF(repost.id IS NULL, FALSE, TRUE) " +
            "FROM content " +
            "LEFT JOIN comment on content.id = comment.content_id AND comment.user_id = :userId AND comment.is_deleted = FALSE " +
            "LEFT JOIN content repost ON content.id = repost.repost_id AND repost.user_id = :userId AND repost.is_deleted = FALSE " +
            "LEFT JOIN content_like ON content_like.content_id = content.id AND content_like.user_id = :userId AND content_like.is_deleted = FALSE " +
            "LEFT JOIN vote ON vote.user_id = :userId AND vote.poll_id = content.poll_id AND vote.is_deleted = FALSE " +
            "LEFT JOIN follow ON follow.user_id = :userId AND follow.follow_user_id = content.user_id AND follow.is_deleted = FALSE " +
            "JOIN user ON content.user_id = user.id " +
            "LEFT JOIN stored_content ON content.id = stored_content.content_id AND stored_content.user_id = :userId AND stored_content.is_deleted = false " +
            "WHERE IF(:type IS NOT NULL, type = :type, type = 'ARTICLE_REGULAR') AND content.r_tags LIKE :tagName " +
            "ORDER BY content.create_time DESC, ?#{#pageable}",
            countQuery = "SELECT content.id " +
                    "FROM content " +
                    "LEFT JOIN content_like ON content_like.content_id = content.id AND content_like.user_id = :userId AND content_like.is_deleted = FALSE " +
                    "LEFT JOIN vote ON vote.user_id = :userId AND vote.poll_id = content.poll_id AND vote.is_deleted = FALSE " +
                    "LEFT JOIN follow ON follow.user_id = :userId AND follow.follow_user_id = content.user_id AND follow.is_deleted = FALSE " +
                    "JOIN user ON content.user_id = user.id " +
                    "WHERE IF(:type IS NOT NULL, type = :type, type = 'ARTICLE_REGULAR') AND content.r_tags LIKE :tagName " +
                    "ORDER BY ?#{#pageable}",
            nativeQuery = true)
    Page<Object[]> getAllByTag(@Param("tagName") String tagName, @Param("type") String type, @Param("userId") Long userId, Pageable pageable);

    @Query(value = "SELECT DISTINCT content.id, content.headline, content.text, content.update_time, content.r_likes, content.r_reposts, content.r_shares, content.r_comments, content.r_has_video, content.r_username, content.r_user_role, content.r_user_image_url, content.r_media_url, content.r_tags, content.r_poll_question, content.r_poll_answers, content.r_reactions, content.user_id, content.repost_id, content.poll_id, IF(user_poll.id IS NULL, FALSE, TRUE), IF(content_like.id IS NULL, FALSE, TRUE), IF(vote.id IS NULL, FALSE, TRUE), user.r_followers, r_following, IF(follow.id IS NULL, FALSE, TRUE), IF(stored_content.id IS NULL, FALSE, TRUE), IF(comment.id IS NULL, FALSE, TRUE), IF(repost.id IS NULL, FALSE, TRUE) " +
            "FROM content " +
            "LEFT JOIN comment on content.id = comment.content_id AND comment.user_id = :userId AND comment.is_deleted = FALSE " +
            "LEFT JOIN content repost ON content.id = repost.repost_id AND repost.user_id = :userId AND repost.is_deleted = FALSE " +
            "LEFT JOIN content_like ON content_like.content_id = content.id AND content_like.user_id = :userId AND content_like.is_deleted = FALSE " +
            "LEFT JOIN user_poll ON user_poll.poll_id = content.poll_id AND user_poll.user_id = :userId " +
            "LEFT JOIN vote ON vote.user_id = :userId AND vote.poll_id = content.poll_id AND vote.is_deleted = FALSE " +
            "LEFT JOIN follow ON follow.user_id = :userId AND follow.follow_user_id = content.user_id AND follow.is_deleted = FALSE " +
            "JOIN user ON content.user_id = user.id " +
            "LEFT JOIN stored_content ON content.id = stored_content.content_id AND stored_content.user_id = :userId AND stored_content.is_deleted = false " +
            "INNER JOIN entity_tag ON entity_tag.content_id = content.id AND entity_tag.is_deleted = FALSE AND entity_tag.tag_id = :tagId " +
            "WHERE IF(:type IS NOT NULL, content.type = :type, content.type = 'ARTICLE_REGULAR') AND content.is_deleted = FALSE " +
            "ORDER BY content.create_time DESC, ?#{#pageable}",
            countQuery = "SELECT content.id " +
                    "FROM content " +
                    "LEFT JOIN content_like ON content_like.content_id = content.id AND content_like.user_id = :userId AND content_like.is_deleted = FALSE " +
                    "LEFT JOIN vote ON vote.user_id = :userId AND vote.poll_id = content.poll_id AND vote.is_deleted = FALSE " +
                    "LEFT JOIN follow ON follow.user_id = :userId AND follow.follow_user_id = content.user_id AND follow.is_deleted = FALSE " +
                    "JOIN user ON content.user_id = user.id " +
                    "INNER JOIN entity_tag ON entity_tag.content_id = content.id AND entity_tag.is_deleted = FALSE AND entity_tag.tag_id = :tagId " +
                    "WHERE IF(:type IS NOT NULL, content.type = :type, content.type = 'ARTICLE_REGULAR') " +
                    "ORDER BY ?#{#pageable}",
            nativeQuery = true)
    Page<Object[]> getAllByTag(@Param("tagId") Long tagId, @Param("type") String type, @Param("userId") Long userId, Pageable pageable);

    List<Content> findAllByUserAndIsDeletedFalse(User user);

    List<Content> findAllByPollIsNotNullAndTypeIs(ContentType type);

    List<Content> findAllByContent(Content content);

    List<Content> findAllByContentAndIsDeletedIsFalse(Content content);

    @Query(value = "SELECT new init.app.web.dto.response.ContentPollResponseDto(c.user.id, c.user.username, c.user.email, c.id, c.headline, c.text, c.poll.id, c.poll.type) FROM Content c WHERE c.type = 'ARTICLE_REGULAR' and c.isDeleted = false and c.poll is not null AND (c.headline LIKE :keyword OR c.text LIKE :keyword) ORDER BY c.createTime")
    Page<ContentPollResponseDto> fetchAllContentWithPoll(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = "SELECT content.id, content.headline, content.text, content.r_media_url, content.create_time, content.category_id, content.poll_id,  IF(user_poll.id IS NULL, FALSE, TRUE), content.r_poll_question, content.r_poll_answers, content.r_tags " +
            "FROM content " +
            "LEFT JOIN user_poll ON user_poll.poll_id = content.poll_id AND user_poll.user_id = :principalId " +
            "WHERE content.id = :id", nativeQuery = true)
    Object[] getSingleResponse(@Param("principalId") Long principalId, @Param("id") Long id);


    // TODO: 6/7/18 DELETE AFTER MIGRATIONS
    Content findFirstByHeadlineAndIsDeletedFalseAndTypeIn(String headline, List<ContentType> contentTypes);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE content SET poll_id = :pollId WHERE id = :contentId", nativeQuery = true)
    void updateContentPoll(@Param("pollId") Long pollId, @Param("contentId") Long contentId);
}
