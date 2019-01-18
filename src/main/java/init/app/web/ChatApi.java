package init.app.web;

import com.jcabi.aspects.Loggable;
import init.app.component.HttpCustomEntity;
import init.app.domain.repository.ConversationRepository;
import init.app.service.ChatService;
import init.app.web.dto.custom.GetAllMessagesDto;
import init.app.web.dto.custom.MessageCreationResponseDto;
import init.app.web.dto.parent.MessageDto;
import init.app.web.dto.request.ConversationResponseDto;
import init.app.web.dto.request.MessageRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import javax.validation.Valid;

import static init.app.exception.CustomExceptionHandler.handleException;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Loggable(trim = false, prepend = true)
@RestController
@RequestMapping("/chat")
public class ChatApi {

    @Inject
    private HttpCustomEntity entity;
    @Inject
    private ChatService chatService;
    @Inject
    ConversationRepository conversationRepository;

    @RequestMapping(value = "/message", method = POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ACTIVE')")
    public ResponseEntity sendMessage(@ApiIgnore @AuthenticationPrincipal Long principalId, @Valid @RequestBody MessageRequestDto request) {
        try {
            MessageCreationResponseDto creationResponseDto = chatService.sendMessage(principalId, request.getConversationId(), request.getUsersToList(), request.getMessage());
            chatService.sendMessageNotifications(creationResponseDto.getUsersToList(), creationResponseDto.getNewConversationCreated(), creationResponseDto.getMessageNotification(), creationResponseDto.getConversationNotification());
            return entity.response(creationResponseDto.getApiResponse());
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/message-to-all", method = POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity sendToAll(@ApiIgnore @AuthenticationPrincipal Long principalId, @Valid @RequestBody MessageDto request) {
        try {
            chatService.sendToAll(principalId, request.getMessage());
            return entity.responseCreated();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/conversation/all", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ACTIVE')")
    public ResponseEntity getAllConversations(@ApiIgnore @AuthenticationPrincipal Long principalId, @RequestParam(value = "username", required = false) String username, @RequestParam("page") int page, @RequestParam("size") int size) {
        try {
            Page<ConversationResponseDto> response = chatService.getAllConversations(principalId, username, new PageRequest(page, size));
            return entity.response(response);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/conversation/{id}", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ACTIVE')")
    public ResponseEntity getConversation( @PathVariable Long id) {
        try {
            ConversationResponseDto response = conversationRepository.getConversation(id);
            return entity.response(response);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/conversation/count-unread", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ACTIVE')")
    public ResponseEntity getConversationUnreadCount(@ApiIgnore @AuthenticationPrincipal Long principalId) {
        try {
            return entity.response(chatService.getUnreadConversationsCounter(principalId));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/conversation/{id}/count-unread", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ACTIVE')")
    public ResponseEntity getConversationUnreadMessagesCount(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long id) {
        try {
            return entity.response(chatService.getUnreadMessageCounter(principalId, id));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/conversation/{id}/messages", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ACTIVE')")
    public ResponseEntity getAllMessages(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long id, @RequestParam("limit") int limit, @RequestParam("offset") int offset) {
        try {
            GetAllMessagesDto getAllMessagesDto = chatService.getAllMessages(principalId, id, limit, offset);

            if (getAllMessagesDto.getAnyMessageStatusChanged()) {
                chatService.sendResetCounterNotification(principalId, id);
            }

            return entity.response(getAllMessagesDto.getResponse());
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/conversation/{conversationId}/message/{messageId}/set-to-read", method = POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ACTIVE')")
    public ResponseEntity setMessageToRead(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long conversationId, @PathVariable Long messageId) {
        try {
            chatService.setMessageToRead(conversationId, messageId, principalId);
            chatService.sendResetCounterNotification(principalId, conversationId);
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }

}
