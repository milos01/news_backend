package init.app.service;

import com.jcabi.aspects.Async;
import com.jcabi.aspects.Loggable;
import init.app.domain.model.Conversation;
import init.app.domain.model.Message;
import init.app.domain.model.User;
import init.app.domain.model.UserConversation;
import init.app.domain.model.enumeration.Role;
import init.app.domain.repository.ConversationRepository;
import init.app.domain.repository.MessageRepository;
import init.app.domain.repository.UserConversationRepository;
import init.app.domain.repository.UserRepository;
import init.app.exception.CustomException;
import init.app.web.dto.custom.ConversationCounterNotificationDto;
import init.app.web.dto.custom.GetAllMessagesDto;
import init.app.web.dto.custom.MessageCreationResponseDto;
import init.app.web.dto.custom.MessageNotificationDto;
import init.app.web.dto.parent.IdDto;
import init.app.web.dto.request.ConversationResponseDto;
import init.app.web.dto.request.UnreadConversationsCounterDto;
import init.app.web.dto.request.UnreadMessagesCounterDto;
import init.app.web.dto.response.MessageResponseDto;
import init.app.web.dto.shared.GenericResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@Loggable(trim = false, prepend = true)
@Transactional(rollbackFor = Exception.class)
public class ChatService {

    @Inject
    ConversationRepository conversationRepository;

    @Inject
    UserConversationRepository userConversationRepository;

    @Inject
    MessageRepository messageRepository;

    @Inject
    UserService userService;

    @Inject
    UserRepository userRepository;

    @Inject
    SimpMessagingTemplate simpMessagingTemplate;

    //================================================================================
    // Primary API Methods
    //================================================================================

    public MessageCreationResponseDto sendMessage(Long principalId, Long conversationId, List<Long> usersToList, String message) throws CustomException {

        User user = userService.getByRepoMethod(userRepository.findById(principalId));

        Conversation conversation;

        Boolean newConversation = false;

        if (conversationId == null) {
            if (usersToList == null || usersToList.isEmpty()) {
                throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("NO_USERS_TO_OR_CONVERSATION_SELECTED"));
            }

            conversation = getExistingConversation(principalId, usersToList);

            if (conversation == null) {
                newConversation = true;
                conversation = createConversation(principalId, usersToList);
            }
        } else {
            usersToList = new ArrayList<>();
            conversation = getByRepoMethod(conversationRepository.findById(conversationId), user);

            List<UserConversation> userConversationList = userConversationRepository.findAllByConversationAndIsDeletedFalse(conversation);

            for (UserConversation userConversation : userConversationList) {
                if (userConversation.getUser() == user) {
                    continue;
                }

                usersToList.add(userConversation.getUser().getId());
            }

        }

        Message newMessage = createMessage(conversation, message, principalId);

        MessageNotificationDto messageNotificationDto = new MessageNotificationDto();
        messageNotificationDto.setConversationId(conversation.getId());
        messageNotificationDto.setMessageId(newMessage.getId());
        messageNotificationDto.setMessageText(message);
        messageNotificationDto.setUserFromId(principalId);
        messageNotificationDto.setUserFromUsername(user.getUsername());
        messageNotificationDto.setUserFromImageUrl(user.getImageUrl());
        messageNotificationDto.setMessageTimestamp(ZonedDateTime.now());

        ConversationCounterNotificationDto conversationCounterNotificationDto = new ConversationCounterNotificationDto();
        conversationCounterNotificationDto.setConversationId(conversation.getId());

        MessageCreationResponseDto creationResponseDto = new MessageCreationResponseDto();
        creationResponseDto.setApiResponse(new GenericResponseDto(new IdDto(conversation.getId())));
        creationResponseDto.setMessageNotification(messageNotificationDto);
        creationResponseDto.setConversationNotification(conversationCounterNotificationDto);
        creationResponseDto.setNewConversationCreated(newConversation);
        creationResponseDto.setUsersToList(usersToList);

        return creationResponseDto;
    }

    @Async
    public void sendMessageNotifications(List<Long> usersToList, Boolean newConversation, MessageNotificationDto messageNotificationDto, ConversationCounterNotificationDto conversationCounterNotificationDto) {
        for (Long userId : usersToList) {
            simpMessagingTemplate.convertAndSend("/notification/user/" + userId + "/message/new", messageNotificationDto);
            if (newConversation) {
                simpMessagingTemplate.convertAndSend("/notification/user/" + userId + "/conversation/new", conversationCounterNotificationDto);
            } else {
                simpMessagingTemplate.convertAndSend("/notification/user/" + userId + "/message/reset-counter", conversationCounterNotificationDto);
            }
        }
    }

    public GetAllMessagesDto getAllMessages(Long principalId, Long conversationId, Integer limit, Integer offset) throws CustomException {

        User user = userService.getByRepoMethod(userRepository.findById(principalId));

        Conversation conversation = getByRepoMethod(conversationRepository.findById(conversationId), user);

        List<Message> pagedMessages = messageRepository.getAllMessagesForConversation(conversationId, limit, offset);

        List<MessageResponseDto> messageResponseDtos = new ArrayList<>();

        Boolean messageStatusChanged = false;

        for (Message message : pagedMessages) {
            if (message.getUser() != user) {
                if (setUnreadMessageToReadAndCheckIfStatusChanged(message, user)) {
                    messageStatusChanged = true;
                }
            }

            MessageResponseDto messageResponseDto = new MessageResponseDto();
            messageResponseDto.setMessageId(message.getId());
            messageResponseDto.setMessageText(message.getText());
            messageResponseDto.setMessageTimestamp(message.getCreateTime());
            messageResponseDto.setUserFromId(message.getUser().getId());
            messageResponseDto.setUserFromUsername(message.getUser().getUsername());
            messageResponseDto.setUserFromImageUrl(message.getUser().getImageUrl());

            messageResponseDtos.add(messageResponseDto);
        }

        GetAllMessagesDto getAllMessagesDto = new GetAllMessagesDto();
        getAllMessagesDto.setResponse(messageResponseDtos);
        getAllMessagesDto.setAnyMessageStatusChanged(messageStatusChanged);

        return getAllMessagesDto;
    }

    public Page<ConversationResponseDto> getAllConversations(Long principalId, String username, Pageable pageable) throws CustomException {

        Page<ConversationResponseDto> response = conversationRepository.getAll(principalId, StringUtils.hasText(username) ? "%" + username + "%" : "%", pageable);

        for (ConversationResponseDto conversationResponseDto : response) {
            conversationResponseDto.setUnreadMessages(getUnreadMessageCounter(principalId, conversationResponseDto.getConversationId()).getUnreadCounter());
        }

        return response;
    }

    public UnreadMessagesCounterDto getUnreadMessageCounter(Long principalId, Long conversationId) throws CustomException {
        User user = userService.getByRepoMethod(userRepository.findById(principalId));

        Conversation conversation = getByRepoMethod(conversationRepository.findById(conversationId), user);

        List<Message> messages = messageRepository.findAllByConversationAndUserNot(conversation, user);

        Integer unreadMessagesCounter = 0;

        for (Message message : messages) {
            if (!checkIfMessageIsRead(message, user)) {
                unreadMessagesCounter++;
            }
        }

        UnreadMessagesCounterDto unreadCounterDto = new UnreadMessagesCounterDto();
        unreadCounterDto.setUnreadCounter(unreadMessagesCounter);
        unreadCounterDto.setLastMessageTimestamp(conversation.getLastMessageTime());

        return unreadCounterDto;
    }

    public UnreadConversationsCounterDto getUnreadConversationsCounter(Long principalId) throws CustomException {

        User user = userService.getByRepoMethod(userRepository.findById(principalId));

        List<UserConversation> userConversations = userConversationRepository.findAllByUserAndIsDeletedFalse(user);

        Integer unreadConversationsCounter = 0;

        for (UserConversation userConversation : userConversations) {
            if (!checkIfConversationIsRead(userConversation.getConversation(), user)) {
                unreadConversationsCounter++;
            }
        }

        UnreadConversationsCounterDto unreadCounterDto = new UnreadConversationsCounterDto();
        unreadCounterDto.setUnreadCounter(unreadConversationsCounter);

        return unreadCounterDto;
    }

    public void setMessageToRead(Long conversationId, Long messageId, Long principalId) throws CustomException {

        User user = userService.getByRepoMethod(userRepository.findById(principalId));

        Conversation conversation = getByRepoMethod(conversationRepository.findById(conversationId), user);

        Message message = messageRepository.findById(messageId);

        if (message.getUser() == user) {
            return;
        }

        if (message.getConversation() != conversation) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("CONVERSATION_NOT_YOURS"));
        }

        setUnreadMessageToReadAndCheckIfStatusChanged(message, user);
    }

    @Async
    public void sendToAll(Long principalId, String message) throws CustomException {
        List<User> userList = userRepository.getAllActiveAndAdminUsers();
        User loggedInUser = userService.getByRepoMethod(userRepository.findById(principalId));

        List<MessageCreationResponseDto> creationResponses = new ArrayList<>();

        for (User user : userList) {
            if (user == loggedInUser) {
                continue;
            }

            creationResponses.add(sendMessageToUser(principalId, user.getId(), message));
        }

        for (MessageCreationResponseDto creationResponse : creationResponses) {
            sendMessageNotifications(creationResponse.getUsersToList(), creationResponse.getNewConversationCreated(), creationResponse.getMessageNotification(), creationResponse.getConversationNotification());
        }

    }

    //================================================================================
    // Helper API Methods
    //================================================================================

    public void sendResetCounterNotification(Long userId, Long conversationId) {
        ConversationCounterNotificationDto conversationCounterNotificationDto = new ConversationCounterNotificationDto();
        conversationCounterNotificationDto.setConversationId(conversationId);

        simpMessagingTemplate.convertAndSend("/notification/user/" + userId + "/message/reset-counter", conversationCounterNotificationDto);
    }

    //================================================================================
    // Private Helper Methods
    //================================================================================

    private Conversation createConversation(Long principalId, List<Long> usersToList) throws CustomException {

        List<Long> usersForConversation = new ArrayList<>(usersToList);
        usersForConversation.add(principalId);
        Collections.sort(usersForConversation, Collections.reverseOrder());

        String usersRedundantField = "";
        String usersInfoRedundantField = "";

        for (Long userId : usersForConversation) {

            User user = userService.getByRepoMethod(userRepository.findById(userId));

            if (user.getRole().equals(Role.DEACTIVATED) || user.getRole().equals(Role.BANNED)) {
                throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("CANNOT_START_A_CONVERSATION_BANNED_DEACTIVATED"));
            }

            usersRedundantField += userId + "|";

            usersInfoRedundantField += user.getUsername() + "\\" + user.getImageUrl() + "|";
        }
        Conversation conversation = new Conversation();
        conversation.setRUsers(usersRedundantField.substring(0, usersRedundantField.length() - 1));
        conversation.setRUsersInfo(usersInfoRedundantField.substring(0, usersInfoRedundantField.length() - 1));
        conversation.setCreateTime(ZonedDateTime.now());
        conversation.setUpdateTime(ZonedDateTime.now());
        conversation.setIsDeleted(false);

        conversationRepository.save(conversation);

        for (Long userId : usersForConversation) {
            User user = userService.getByRepoMethod(userRepository.findById(userId));

            UserConversation userConversation = new UserConversation();
            userConversation.setConversation(conversation);
            userConversation.setUser(user);
            userConversation.setCreateTime(ZonedDateTime.now());
            userConversation.setUpdateTime(ZonedDateTime.now());
            userConversation.setIsDeleted(false);

            userConversationRepository.save(userConversation);
        }

        return conversation;
    }

    private Conversation getExistingConversation(Long principalId, List<Long> usersToList) throws CustomException {
        String usersRedundantField = createRedundantUsersField(principalId, usersToList);

        return conversationRepository.findByRUsersAndIsDeletedIsFalse(usersRedundantField);
    }

    private Conversation getByRepoMethod(Conversation conversation, User user) throws CustomException {

        if (conversation == null) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("CONVERSATION_NOT_EXIST"));
        } else if (conversation.getIsDeleted()) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("CONVERSATION_DELETED"));
        }

        UserConversation userConversation = userConversationRepository.findByConversationAndUserAndIsDeletedFalse(conversation, user);

        if (userConversation == null) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("CONVERSATION_NOT_YOURS"));
        }

        return conversation;
    }

    private String createRedundantUsersField(Long principalId, List<Long> usersToList) {
        List<Long> usersForConversation = new ArrayList<>(usersToList);
        usersForConversation.add(principalId);
        Collections.sort(usersForConversation, Collections.reverseOrder());

        String usersRedundantField = "";

        for (Long userId : usersForConversation) {
            usersRedundantField += userId + "|";
        }

        return usersRedundantField.substring(0, usersRedundantField.length() - 1);
    }

    private Message createMessage(Conversation conversation, String messageText, Long principalId) throws CustomException {

        User user = userService.getByRepoMethod(userRepository.findById(principalId));

        conversation.setLastMessageTime(ZonedDateTime.now());

        conversationRepository.save(conversation);

        Message message = new Message();
        message.setConversation(conversation);
        message.setUser(user);
        message.setText(messageText);
        message.setReadBy("");
        message.setCreateTime(ZonedDateTime.now());
        message.setUpdateTime(ZonedDateTime.now());
        message.setIsDeleted(false);

        messageRepository.save(message);

        return message;
    }

    private Boolean checkIfConversationIsRead(Conversation conversation, User user) {
        List<Message> messages = messageRepository.findAllByConversationAndUserNot(conversation, user);

        for (Message message : messages) {
            if (!checkIfMessageIsRead(message, user)) {
                return false;
            }
        }

        return true;
    }

    private Boolean checkIfMessageIsRead(Message message, User user) {

        if (message.getUser() != user && StringUtils.hasText(message.getReadBy()) && (message.getReadBy().startsWith(user.getId() + "|") || message.getReadBy().endsWith("|" + user.getId()) || message.getReadBy().contains("|" + user.getId() + "|") || message.getReadBy().equals(user.getId().toString()))) {
            return true;
        }

        return false;
    }

    private Boolean setUnreadMessageToReadAndCheckIfStatusChanged(Message message, User user) {

        if (checkIfMessageIsRead(message, user)) {
            return false;
        }

        if (!StringUtils.hasText(message.getReadBy())) {
            message.setReadBy(user.getId().toString());
        } else {
            List<String> readByUserIds = new ArrayList<>();
            readByUserIds.addAll(Arrays.asList(message.getReadBy().split("\\|")));
            readByUserIds.add(user.getId().toString());

            Collections.sort(readByUserIds, Collections.reverseOrder());

            String readByUsers = "";

            for (String readByUserId : readByUserIds) {
                readByUsers += readByUserId + "|";
            }

            message.setReadBy(readByUsers.substring(0, readByUsers.length() - 1));
        }

        messageRepository.save(message);

        return true;
    }

    private MessageCreationResponseDto sendMessageToUser(Long userFromId, Long userToSendToId, String message) throws CustomException {
        MessageCreationResponseDto responseDto = sendMessage(userFromId, null, Arrays.asList(userToSendToId), message);

        return responseDto;
    }

}
