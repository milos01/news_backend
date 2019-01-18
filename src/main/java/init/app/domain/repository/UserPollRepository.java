package init.app.domain.repository;

import init.app.domain.model.Poll;
import init.app.domain.model.User;
import init.app.domain.model.UserPoll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPollRepository extends JpaRepository<UserPoll, Long> {

    Boolean existsByUserAndPollAndIsDeletedFalse(User user, Poll poll);

    UserPoll findByUserAndPollAndIsDeletedFalse(User user, Poll poll);

    UserPoll findByUserAndPoll(User user, Poll poll);
}
