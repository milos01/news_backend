package init.app.domain.repository;

import init.app.domain.model.Tag;
import init.app.web.dto.response.TagResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query(value = "SELECT new init.app.web.dto.response.TagResponseDto(t.id, t.text, t.type, t.rFollowers, false) FROM Tag t WHERE t.id = :id AND t.isDeleted = FALSE")
    Optional<TagResponseDto> getById(@Param("id") Long id);

    @Query(value = "SELECT new init.app.web.dto.response.TagResponseDto(t.id, t.text, t.type, t.rFollowers, false) FROM Tag t INNER JOIN EntityTag et ON et.user.id = :userId AND et.tag.id = t.id AND et.isDeleted = FALSE WHERE t.isDeleted = FALSE")
    List<TagResponseDto> getUserTags(@Param("userId") Long userId);

    Tag findById(Long id);

    @Query(value = "SELECT tag.id, text, type, r_followers, IF(follow.id IS NULL, FALSE, TRUE) FROM tag LEFT JOIN follow on follow.follow_tag_id = tag.id AND follow.user_id = :userId AND follow.is_deleted = FALSE WHERE tag.id = :id AND tag.is_deleted = FALSE",
            nativeQuery= true)
    Object[] getOneById(@Param("id") Long id, @Param("userId") Long userId);

    @Query(value = "SELECT tag.id, text, type, r_followers, IF(follow.id IS NULL, FALSE, TRUE) FROM tag LEFT JOIN follow ON follow.follow_tag_id = tag.id AND follow.user_id = :userId AND follow.is_deleted = FALSE WHERE IF(:type IS NOT NULL, type = :type, tag.id = tag.id) AND IF(:keyword IS NOT NULL, LOWER(text) LIKE LOWER(CONCAT('%',:keyword, '%')), tag.id = tag.id) AND IF(:isDeleted IS NOT NULL, tag.is_deleted= :isDeleted, tag.is_deleted = FALSE) ORDER BY tag.total_activity DESC, ?#{#pageable}",
            countQuery = "SELECT tag.id FROM tag LEFT JOIN follow ON follow.follow_tag_id = tag.id AND follow.user_id = :userId AND follow.is_deleted = FALSE WHERE IF(:type IS NOT NULL, type = :type, tag.id = tag.id) AND IF(:keyword IS NOT NULL, LOWER(text) LIKE LOWER(CONCAT('%',:keyword, '%')), tag.id = tag.id) AND IF(:isDeleted IS NOT NULL, tag.is_deleted= :isDeleted, tag.is_deleted = FALSE) ORDER BY tag.update_time DESC, ?#{#pageable}",
            nativeQuery = true)
    Page<Object[]> getAll(@Param("userId") Long userId, @Param("type") String type, @Param("keyword") String keyword, @Param("isDeleted") Boolean isDeleted, Pageable pageable);

    List<Tag> findAllByTextContainingAndIsDeletedFalse(String text);

    Tag findByText(String text);

    List<Tag> getAllByIsDeletedFalseOrderByTempActivityDesc();

    // TODO: 6/8/18 OBRISATI NAKON MIGRACIJA

    Tag findFirstByText(String text);
}
