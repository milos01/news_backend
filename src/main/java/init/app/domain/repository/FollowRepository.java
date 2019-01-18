package init.app.domain.repository;

import init.app.domain.model.Follow;
import init.app.domain.model.Tag;
import init.app.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    @Query(value = "SELECT user.id, user.role, user.email, user.username, user.bio, user.image_url, user.r_followers, user.r_following, IF(user_follow.id IS NULL, FALSE, TRUE) " +
            "FROM follow AS main_follow " +
            "INNER JOIN user ON main_follow.user_id = user.id " +
            "LEFT JOIN follow AS user_follow ON user_follow.user_id = :loggedUserId AND user_follow.follow_user_id = user.id AND user_follow.is_deleted = FALSE " +
            "WHERE IF(:type = 'TAG', main_follow.follow_tag_id = :entityId, main_follow.follow_user_id = :entityId) AND main_follow.is_deleted = FALSE " +
            "ORDER BY main_follow.create_time DESC, ?#{#pageable}",
            countQuery =  "SELECT user.id " +
                    "FROM follow AS main_follow " +
                    "INNER JOIN user ON main_follow.user_id = user.id " +
                    "LEFT JOIN follow AS user_follow ON user_follow.user_id = :loggedUserId AND user_follow.follow_user_id = user.id AND user_follow.is_deleted = FALSE " +
                    "WHERE IF(:type = 'TAG', main_follow.follow_tag_id = :entityId, main_follow.follow_user_id = :entityId) AND main_follow.is_deleted = FALSE AND IF(:loggedUserId IS NOT NULL, user.id != :loggedUserId, user.id = user.id) " +
                    "ORDER BY main_follow.create_time DESC, ?#{#pageable}",
            nativeQuery = true)
    Page<Object[]> getFollowers(@Param("entityId") Long entityId,@Param("type") String type, @Param("loggedUserId") Long loggedUserId, Pageable pageable);

    @Query(value = "SELECT tag.id, tag.text, tag.type, tag.r_followers, TRUE " +
            "FROM follow INNER JOIN tag ON follow.follow_tag_id = tag.id WHERE follow.user_id = :userId AND follow.is_deleted = FALSE " +
            "ORDER BY follow.create_time DESC, ?#{#pageable}",
            countQuery = "SELECT tag.id " +
                    "FROM follow INNER JOIN tag ON follow.follow_tag_id = tag.id WHERE follow.user_id = :userId AND follow.is_deleted = FALSE " +
                    "ORDER BY follow.create_time DESC, ?#{#pageable}",
            nativeQuery = true)
    Page<Object[]> getFollowedTags(@Param("userId")Long userId, Pageable pageable);

    @Query(value = "SELECT user.id, user.role, user.email, user.username, user.bio, user.image_url, user.r_followers, user.r_following, TRUE " +
            "FROM follow " +
            "INNER JOIN user ON follow.follow_user_id = user.id " +
            "WHERE follow.user_id = :userId AND follow.is_deleted = FALSE " +
            "ORDER BY follow.create_time DESC, ?#{#pageable}",
            countQuery = "SELECT user.id FROM follow INNER JOIN user ON follow.follow_user_id = user.id WHERE follow.user_id = :userId AND follow.is_deleted = FALSE ORDER BY follow.create_time DESC, ?#{#pageable}",
            nativeQuery = true)
    Page<Object[]> getFollowedUsers(@Param("userId")Long userId, Pageable pageable);

    Follow findByUserAndFollowedUser(User user, User followedUser);

    Follow findByUserAndFollowedTag(User user, Tag followedTag);
}
