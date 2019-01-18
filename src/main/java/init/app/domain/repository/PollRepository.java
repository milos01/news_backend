package init.app.domain.repository;

import init.app.domain.model.Poll;
import init.app.domain.model.User;
import init.app.domain.model.enumeration.PollType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {

    Poll findById(Long id);

    @Query(value = "SELECT id, question, first_choice, second_choice, third_choice, r_first_amount, r_second_amount, r_third_amount, create_time, is_deleted FROM poll WHERE IF(:type IS NOT NULL, poll.type = :type, id = id) AND IF(:keyword IS NOT NULL, (LOWER(question) LIKE LOWER(CONCAT('%',:keyword, '%')) OR LOWER(first_choice) LIKE LOWER(CONCAT('%',:keyword, '%')) OR LOWER(second_choice) LIKE LOWER(CONCAT('%',:keyword, '%')) OR LOWER(third_choice) LIKE LOWER(CONCAT('%',:keyword, '%'))), id = id) ORDER BY create_time DESC, ?#{#pageable}",
            countQuery = "SELECT * FROM poll WHERE IF(:type IS NOT NULL, poll.type = :type, id = id) AND IF(:keyword IS NOT NULL, (LOWER(question) LIKE LOWER(CONCAT('%',:keyword, '%')) OR LOWER(first_choice) LIKE LOWER(CONCAT('%',:keyword, '%')) OR LOWER(second_choice) LIKE LOWER(CONCAT('%',:keyword, '%')) OR LOWER(third_choice) LIKE LOWER(CONCAT('%',:keyword, '%'))), id = id) ORDER BY ?#{#pageable}",
            nativeQuery = true)
    Page<Object[]> getAll(@Param("type") String type, @Param("keyword") String keyword, Pageable pageable);

    @Query(value = "SELECT content.id, content.type, r_poll_question, r_poll_answers " +
            "FROM poll " +
            "LEFT JOIN content ON content.poll_id = poll.id " +
            "LEFT JOIN user_poll ON user_poll.poll_id = content.poll_id " +
            "WHERE user_poll.user_id = :principalId " +
            "ORDER BY user_poll.create_time ASC LIMIT :limit OFFSET :offset",
            nativeQuery = true)
    List<Object[]> getFollowedPolls(@Param("principalId") Long principalId,  @Param("limit") Integer limit, @Param("offset") Integer offset);

    @Query(value = "SELECT COUNT(id) FROM poll WHERE is_deleted = FALSE AND IF(:type IS NOT NULL, poll.type = :type, id = id)",
            nativeQuery = true)
    Integer countAll(@Param("type") String type);

    @Query(value = "SELECT poll.id, question, first_choice, second_choice, third_choice, r_first_amount, r_second_amount, r_third_amount, poll.create_time, poll.is_deleted, IF(vote.id IS NULL OR vote.choice IS NULL, FALSE, TRUE) as 'voted', IF(vote.id IS NULL, NULL, vote.special_poll_nomination)" +
        "FROM poll " +
        "LEFT JOIN vote ON vote.user_id = :userId AND vote.poll_id = poll.id AND vote.is_deleted = FALSE "+
        "WHERE IF(:type IS NOT NULL, poll.type = :type, poll.is_deleted = FALSE) and poll.is_deleted = FALSE " +
        "LIMIT 1",
        nativeQuery = true)
    Object[] findByType(@Param("userId") Long userId, @Param("type") String type);

    List<Poll> findAllByTypeAndIsDeletedIsFalse(PollType pollType);

    // TODO: 6/7/18 DELETE AFTER MIGRATIONS
    Poll findFirstByCreateTimeAndIsDeletedFalse(ZonedDateTime createTime);
}
