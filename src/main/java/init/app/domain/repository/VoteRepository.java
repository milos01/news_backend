package init.app.domain.repository;

import init.app.domain.model.Poll;
import init.app.domain.model.User;
import init.app.domain.model.UserConversation;
import init.app.domain.model.Vote;
import init.app.web.dto.response.SpecialPollVoteResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    @Query(value = "SELECT id FROM vote ORDER BY ?#{#pageable}",
            countQuery = "SELECT * FROM vote ORDER BY ?#{#pageable}",
            nativeQuery = true)
    Page<Object[]> getAll(Long principalId, Pageable pageable);

    List<Vote> findByPollAndUserAndIsDeletedFalse(Poll poll, User user);

    List<Vote> findAllByPollAndIsDeletedFalse(Poll poll);


    @Query(value = "SELECT new init.app.web.dto.response.SpecialPollVoteResponseDto(u.id, u.username, v.specialPollNomination) FROM Vote v LEFT JOIN User u ON u.id = v.user.id WHERE v.poll.id = :pollId AND v.isDeleted = false ORDER BY v.createTime")
    Page<SpecialPollVoteResponseDto> getAllVotesForSpecialPoll(@Param("pollId") Long pollId, Pageable pageable);
}
