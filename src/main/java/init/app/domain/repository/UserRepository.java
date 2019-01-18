package init.app.domain.repository;

import init.app.domain.model.User;
import init.app.domain.model.EntityTag;
import init.app.domain.model.enumeration.Role;
import init.app.web.dto.response.AdminApiUserResponseDto;
import init.app.web.dto.response.UserAutocompleteResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT user.id, email, username, role, bio, image_url, r_followers, r_following, IF(follow.id IS NULL, FALSE, TRUE) FROM user " +
            "LEFT JOIN follow ON follow.user_id = :principalId AND follow.follow_user_id = user.id AND follow.is_deleted = FALSE " +
            "where user.id = :userId " +
            "ORDER BY user.create_time DESC",
            countQuery = "SELECT user.id FROM user " +
                    "LEFT JOIN follow ON follow.user_id = :principalId AND follow.follow_user_id = user.id AND follow.is_deleted = FALSE " +
                    "where user.id = :userId " +
                    "ORDER BY user.create_time DESC",
            nativeQuery = true)
    Object[] customFindById(@Param("userId") Long userId, @Param("principalId") Long principalId);

    User findById(Long id);

    @Query(value = "SELECT u FROM User u where u.email = :email")
    Optional<User> findAllByEmail(@Param("email") String email);

    User findByTwitterId(String twitterId);

    User findByGoogleId(String googleId);

    User findByFacebookId(String facebookId);

    User findByEmail(String email);

    List<User> findAllByRole(Role role);

    @Query(value = "SELECT u FROM User u WHERE (u.role = init.app.domain.model.enumeration.Role.ACTIVE OR u.role = init.app.domain.model.enumeration.Role.ADMIN) AND u.isDeleted = FALSE")
    List<User> getAllActiveAndAdminUsers();

    @Query(value = "SELECT user.id, email, username, role, bio, image_url, r_followers, r_following, IF(follow.id IS NULL, FALSE, TRUE) FROM user " +
            "LEFT JOIN follow ON follow.user_id = :principalId AND follow.follow_user_id = user.id AND follow.is_deleted = FALSE " +
            "where IF(:role IS NOT NULL, role = :role, user.id = user.id) and IF(:principalId IS NOT NULL, user.id != :principalId, user.id = user.id) and IF(:keyword IS NOT NULL, (LOWER(username) LIKE LOWER(CONCAT('%',:keyword, '%'))), user.id = user.id) " +
            "ORDER BY user.create_time DESC, ?#{#pageable}",
            countQuery = "SELECT user.id FROM user " +
                    "LEFT JOIN follow ON follow.user_id = :principalId AND follow.follow_user_id = user.id AND follow.is_deleted = FALSE " +
                    "where IF(:role IS NOT NULL, role = :role, user.id = user.id) and IF(:principalId IS NOT NULL, user.id != :principalId, user.id = user.id) and IF(:keyword IS NOT NULL, (LOWER(username) LIKE LOWER(CONCAT('%',:keyword, '%'))), user.id = user.id) " +
                    "ORDER BY user.create_time DESC, ?#{#pageable}",
            nativeQuery = true)
    Page<Object[]> findAll(@Param("principalId") Long principalId, @Param("keyword") String keyword, @Param("role") String role, Pageable pageable);

    @Query(value = "SELECT new init.app.web.dto.response.UserAutocompleteResponseDto(u.id, u.username, u.imageUrl, u.role) FROM User u WHERE u.username LIKE :username AND u.isDeleted = FALSE AND u.role <> init.app.domain.model.enumeration.Role.DEACTIVATED")
    List<UserAutocompleteResponseDto> userAutocomplete(@Param("username") String username);

    @Query(value = "SELECT COUNT(id) FROM user WHERE user.is_deleted = false ",
            nativeQuery = true)
    Integer countAll();

    @Query(value = "SELECT new init.app.web.dto.response.AdminApiUserResponseDto(u.id, u.username, u.email, u.imageUrl, u.bio, u.rTags, u.twitterId, u.googleId, u.facebookId, u.role, u.createTime, u.emailVerifiedTime, u.isDeleted) FROM User u WHERE (u.username LIKE :keyword OR u.email LIKE :keyword) ORDER BY u.createTime")
    Page<AdminApiUserResponseDto> usersForAdmin(@Param("keyword") String keyword, Pageable pageable);
}
