package init.app.domain.repository;

import init.app.domain.model.Conversation;
import init.app.web.dto.request.ConversationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query(value = "SELECT new init.app.web.dto.request.ConversationResponseDto(c.id, c.rUsers, c.rUsersInfo, c.lastMessageTime) FROM Conversation c JOIN UserConversation uc ON uc.user.id = :userId AND uc.conversation.id = c.id WHERE c.isDeleted = false AND c.rUsersInfo LIKE :username ORDER BY c.lastMessageTime DESC")
    Page<ConversationResponseDto> getAll(@Param("userId") Long userId, @Param("username") String username, Pageable pageable);

    @Query(value = "SELECT new init.app.web.dto.request.ConversationResponseDto(c.id, c.rUsers, c.rUsersInfo, c.lastMessageTime) FROM Conversation c WHERE c.isDeleted = false AND c.id = :id")
    ConversationResponseDto getConversation(@Param("id") Long id);

    Conversation findByRUsersAndIsDeletedIsFalse(String rUsers);

    Conversation findById(Long id);

    @Query(value = "SELECT uc.conversation FROM UserConversation uc WHERE uc.user.id = :userId AND uc.isDeleted = false")
    Page<Conversation> getUserConversations(@Param("userId") Long userId, Pageable pageable);
}
