package init.app.domain.repository;

import init.app.domain.model.Conversation;
import init.app.domain.model.Message;
import init.app.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query(value = "SELECT id FROM message ORDER BY ?#{#pageable}",
            countQuery = "SELECT * FROM message ORDER BY ?#{#pageable}",
            nativeQuery = true)
    Page<Object[]> getAll(Long userId, Long conversationId, Pageable pageable);

    @Query(value = "SELECT m.* FROM message m WHERE m.conversation_id = :conversationId AND m.is_deleted = false ORDER BY m.create_time DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Message> getAllMessagesForConversation(@Param("conversationId") Long conversationId, @Param("limit") Integer limit, @Param("offset") Integer offset);

    List<Message> findAllByConversationAndUserNot(Conversation conversation, User user);

    Message findById(Long messageId);

}
