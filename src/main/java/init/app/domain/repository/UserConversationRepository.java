package init.app.domain.repository;

import init.app.domain.model.Conversation;
import init.app.domain.model.Tag;
import init.app.domain.model.User;
import init.app.domain.model.UserConversation;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserConversationRepository extends JpaRepository<UserConversation, Long> {

    UserConversation findByConversationAndUserAndIsDeletedFalse(Conversation conversation, User user);

    List<UserConversation> findAllByUserAndIsDeletedFalse(User user);

    List<UserConversation> findAllByConversationAndIsDeletedFalse(Conversation conversation);
}
