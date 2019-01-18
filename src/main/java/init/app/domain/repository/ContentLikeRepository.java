package init.app.domain.repository;

import init.app.domain.model.Content;
import init.app.domain.model.ContentLike;
import init.app.domain.model.User;
import init.app.web.dto.response.ContentLikesResponseDto;
import init.app.web.dto.response.UserLikesResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentLikeRepository extends JpaRepository<ContentLike, Long> {

    @Query(value = "SELECT new init.app.web.dto.response.ContentLikesResponseDto(cl.id, u.id, u.email, u.username, u.bio, u.imageUrl) FROM ContentLike cl INNER JOIN User u ON u.id = cl.user.id WHERE cl.content.id = :contentId AND cl.isDeleted = FALSE ORDER BY ?#{#pageable}",
            countQuery = "SELECT cl.id FROM ContentLike cl INNER JOIN User u ON u.id = cl.user.id WHERE cl.content.id = :contentId AND cl.isDeleted = FALSE ORDER BY ?#{#pageable}")
    Page<ContentLikesResponseDto> getAllForContent(@Param("contentId") Long contentId, Pageable pageable);

    @Query(value = "SELECT new init.app.web.dto.response.UserLikesResponseDto(cl.id, c.id, c.headline, c.rMediaUrl) FROM ContentLike cl INNER JOIN User u ON u.id = cl.user.id AND u.id = :userId INNER JOIN Content c ON cl.content.id = c.id  WHERE cl.user.id = :userId AND cl.isDeleted = FALSE ORDER BY ?#{#pageable}",
            countQuery = "SELECT cl.id FROM ContentLike cl INNER JOIN User u ON u.id = cl.user.id AND u.id = :userId INNER JOIN Content c ON cl.content.id = c.id  WHERE cl.user.id = :userId AND cl.isDeleted = FALSE ORDER BY ?#{#pageable}")
    Page<UserLikesResponseDto> getAllUserLiked(@Param("userId") Long userId, Pageable pageable);

    ContentLike findByUserAndContent(User user, Content content);

    List<ContentLike> findAllByContentAndIsDeletedFalse(Content content);
}
