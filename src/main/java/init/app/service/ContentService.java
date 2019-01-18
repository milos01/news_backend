package init.app.service;

import com.jcabi.aspects.Loggable;
import init.app.domain.model.*;
import init.app.domain.model.enumeration.*;
import init.app.domain.repository.*;
import init.app.exception.CustomException;
import init.app.web.dto.custom.CreateContentNotificationDto;
import init.app.web.dto.custom.UpdateContentNotificationDto;
import init.app.web.dto.parent.IdDto;
import init.app.web.dto.request.AssignTagRequestDto;
import init.app.web.dto.shared.GenericResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.servlet.ServletException;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Loggable(trim = false, prepend = true)
@Transactional(rollbackFor = Exception.class)
public class ContentService {

    @Inject
    private CategoryRepository categoryRepository;
    @Inject
    private UserRepository userRepository;
    @Inject
    private ContentRepository contentRepository;
    @Inject
    private CategoryService categoryService;
    @Inject
    private UserService userService;
    @Inject
    private FileService fileService;
    @Inject
    private ContentLikeRepository contentLikeRepository;
    @Inject
    private ContentLikeService contentLikeService;
    @Inject
    private ContentMediaService contentMediaService;
    @Inject
    private EntityTagRepository entityTagRepository;
    @Inject
    private TagService tagService;
    @Inject
    private TagRepository tagRepository;
    @Inject
    private NotificationService notificationService;
    @Inject
    private SimpMessagingTemplate simpMessagingTemplate;
    @Inject
    private MentionService mentionService;

    public GenericResponseDto create(Long principalId, String headline, String text, Long categoryId, Boolean isPublished, ContentCreationType contentCreationType) throws CustomException {

        Category category = null;
        User user = userService.getByRepoMethod(userRepository.findById(principalId));

        ContentType type;
        switch (contentCreationType) {
            case ARTICLE:

                if (user.getRole() != Role.ADMIN) {
                    throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("ONLY_ADMIN_USERS_CAN_CREATE_ARTICLE"), HttpStatus.FORBIDDEN);
                }

                if (isPublished) {
                    category = categoryService.getByRepoMethod(categoryRepository.findById(categoryId));
                    type = ContentType.ARTICLE_REGULAR;
                } else {
                    category = categoryRepository.findById(categoryId);
                    type = ContentType.ARTICLE_DRAFT;
                }
                break;
            default:
                type = ContentType.POST;
        }

        Content content = createUpdateContent(null, user, type, headline, text, category);

        return new GenericResponseDto(new IdDto(content.getId()));
    }

    public void update(Long contentId, Long principalId, String headline, String text, Long categoryId, Boolean isPublished) throws CustomException {

        User user = userService.getByRepoMethod(userRepository.findById(principalId));

        ContentType type;
        switch (user.getRole()) {
            case ADMIN:
                type = isPublished ? ContentType.ARTICLE_REGULAR : ContentType.ARTICLE_DRAFT;
                break;
            default:
                type = ContentType.POST;
        }

        createUpdateContent(getByRepoMethod(contentRepository.findById(contentId)), user, type, headline, text, categoryId == null ? null : categoryService.getByRepoMethod(categoryRepository.findById(categoryId)));
    }

    public void delete(Long principalId, Long contentId) throws CustomException {

        Content content = getByRepoMethod(contentRepository.findById(contentId));
        User user = userService.getByRepoMethod(userRepository.findById(principalId));

        if (user.getRole() != Role.ADMIN && content.getUser() != user) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("CONTENT_NOT_MINE"));
        }

        deleteContent(content);
    }

    public void changeType(Long contentId, ContentType contentType) throws CustomException {

        Content content = getByRepoMethod(contentRepository.findById(contentId));

        content.setUpdateTime(ZonedDateTime.now());
        content.setType(contentType);

        contentRepository.save(content);
    }

    public Content createUpdateContent(Content content, User user, ContentType type, String headline, String text, Category category) throws CustomException {

        if (content == null) {
            content = new Content();

            content.setUser(user);
            content.setCategory(category);

            content.setPoll(null);
            content.setRMediaUrl(null);

            content.setTotalActivity(0);
            content.setCreateTime(ZonedDateTime.now());
            content.setIsDeleted(false);

            content.setRUsername(user.getUsername());
            content.setRUserImageUrl(user.getImageUrl());
            content.setRUserRole(user.getRole());

            content.setRLikes(0);
            content.setRReposts(0);
            content.setRShares(0);
            content.setRComments(0);
            content.setRHasVideo(false);
        }
        if (content.getType() != ContentType.FOOTER) {
            content.setType(type);
        }
        content.setHeadline(headline);
        content.setText(text);
        content.setCategory(category);

        content.setRReactions(ReactionType.stream().map(ReactionType::name).collect(Collectors.joining("/0|", "", "/0")));


        content.setCreateTime(ZonedDateTime.now());
        content.setUpdateTime(ZonedDateTime.now());

        contentRepository.save(content);

        if (type == ContentType.POST && StringUtils.hasText(content.getText())) {
            mentionService.createMentionsForContent(content);
        }

        if (type == ContentType.POST) {
            CreateContentNotificationDto createContentNotificationDto = new CreateContentNotificationDto();
            createContentNotificationDto.setContentId(content.getId());
            createContentNotificationDto.setCreatedByUserId(user.getId());
            simpMessagingTemplate.convertAndSend("/content/create", createContentNotificationDto);
        }

        return content;
    }

    @Async
    public void deleteContent(Content content) throws CustomException {

        content.setUpdateTime(ZonedDateTime.now());
        content.setIsDeleted(true);

        contentRepository.save(content);

        if (content.getContent() != null) {
            Content originalContent = content.getContent();

            originalContent.setRReposts(originalContent.getRReposts() - 1);
            contentRepository.save(originalContent);
        }

        List<ContentLike> contentLikes = contentLikeRepository.findAllByContentAndIsDeletedFalse(content);

        for (ContentLike contentLike : contentLikes) {
            contentLikeService.deleteLike(contentLike);
        }

        List<Content> reposts = contentRepository.findAllByContentAndIsDeletedIsFalse(content);

        for (Content repost : reposts) {
            deleteContent(repost);
        }

        if (content.getType().equals(ContentType.POST)) {
            UpdateContentNotificationDto deleteContentDto = UpdateContentNotificationDto.builder().contentId(content.getId()).build();
            simpMessagingTemplate.convertAndSend("/content/delete", deleteContentDto);
        }
    }

    public Content getByRepoMethod(Content content) throws CustomException {

        if (content == null) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("CONTENT_NOT_EXIST"));
        } else if (content.getIsDeleted()) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("CONTENT_DELETED"));
        }

        return content;
    }

    public void uploadImage(Long principalId, Long contentId, MultipartFile file, Integer order) throws CustomException {

        Content content = checkIfUsersContent(contentId, principalId);

        contentMediaService.countImagesForContent(content);

        String key;
        try {
            key = fileService.getMediaUrl(file);
        } catch (IOException | ServletException s) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("BUCKET_UPLOAD"));
        } catch (Exception ex) {
            throw new CustomException(ex.getMessage());
        }

        contentMediaService.create(content, key, order, ContentMediaType.IMAGE);
    }

    public void addMediaUrl(Long principalId, String url, Long contentId, ContentMediaType mediaType, Integer order) throws CustomException {

        Content content = checkIfUsersContent(contentId, principalId);

        switch (mediaType) {
            case IMAGE:
                contentMediaService.countImagesForContent(content);
                break;
            case VIDEO:
                if (content.getRHasVideo()) {
                    throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("VIDEO_ALREADY_EXISTS"));
                }
                break;
        }

        contentMediaService.create(content, url, order == null ? 0 : order, mediaType);
    }

    private Content checkIfUsersContent(Long contentId, Long userId) throws CustomException {


        Content content = getByRepoMethod(contentRepository.findById(contentId));
        User user = userService.getByRepoMethod(userRepository.findById(userId));

        if (user.getRole() != Role.ADMIN && !content.getUser().getId().equals(userId)) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("CONTENT_NOT_MINE"));
        }

        return content;
    }

    private void deleteMediaUrl(Content content, String mediaUrl, ContentMediaType type) throws CustomException {

        List<String> mediaUrls = new ArrayList<>();
        mediaUrls.addAll(Arrays.asList(content.getRMediaUrl().split("\\|")));
        mediaUrls.remove(mediaUrl);

        String tempRMedia = org.apache.commons.lang3.StringUtils.join(mediaUrls.toArray(), "|");

        content.setRMediaUrl(StringUtils.hasText(tempRMedia) ? tempRMedia : null);
        content.setUpdateTime(ZonedDateTime.now());

        if (type == ContentMediaType.VIDEO) {
            content.setRHasVideo(false);
        }

        contentRepository.save(content);

        contentMediaService.delete(content.getId(), mediaUrl);
    }

    public void updateTags(List<AssignTagRequestDto> tagList, Long contentId, Long principalId) throws CustomException {

        List<Long> tagIdList = new ArrayList<>();

        for (AssignTagRequestDto assignTagRequestDto : tagList) {
            if (assignTagRequestDto.getId() != null) {
                tagIdList.add(assignTagRequestDto.getId());
            } else {
                Tag existingTag = tagRepository.findByText(assignTagRequestDto.getText());

                if (existingTag != null) {
                    tagIdList.add(existingTag.getId());
                } else {
                    GenericResponseDto tagCreationResponse = tagService.create(assignTagRequestDto.getText());
                    tagIdList.add(((IdDto) tagCreationResponse.getContent()).getId());
                }
            }
        }

        User user = userService.getByRepoMethod(userRepository.findById(principalId));

        Content content = contentRepository.findById(contentId);

        if (!content.getUser().getId().equals(user.getId())) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("CONTENT_NOT_MINE"));
        }

        List<EntityTag> entityTagsForContent = entityTagRepository.findAllByContent(content);

        for (EntityTag entityTag : entityTagsForContent) {
            if (tagIdList.contains(entityTag.getTag().getId())) {

                tagIdList.remove(tagIdList.indexOf(entityTag.getTag().getId()));
                if (entityTag.getIsDeleted()) {
                    entityTag.setIsDeleted(false);
                    entityTag.setUpdateTime(ZonedDateTime.now());

                    entityTagRepository.save(entityTag);
                }
            } else if (!entityTag.getIsDeleted()) {
                entityTag.setUpdateTime(ZonedDateTime.now());
                entityTag.setIsDeleted(true);

                entityTagRepository.save(entityTag);
            }
        }

        for (Long tagId : tagIdList) {
            Tag tag = tagService.getByRepoMethod(tagRepository.findById(tagId));
            EntityTag entityTag = new EntityTag();
            entityTag.setIsDeleted(false);
            entityTag.setUpdateTime(ZonedDateTime.now());
            entityTag.setCreateTime(ZonedDateTime.now());
            entityTag.setContent(content);
            entityTag.setTag(tag);

            entityTagRepository.save(entityTag);
        }

        entityTagRepository.flush();
    }

    public List<Tag> getTagsForContent(Long contentId) throws CustomException {
        if (contentId == null) {
            return new ArrayList<>();
        }
        List<EntityTag> entityTags = entityTagRepository.findAllByContentAndIsDeletedFalse(getByRepoMethod(contentRepository.findById(contentId)));
        List<Tag> tags = new ArrayList<>();
        if (entityTags.isEmpty()) {
            return tags;
        }
        return entityTags.stream().map(EntityTag::getTag).collect(Collectors.toList());
    }

    public GenericResponseDto repost(Long principalId, String text, Long contentId) throws CustomException {

        User author = userService.getByRepoMethod(userRepository.findById(principalId));

        Content content = getByRepoMethod(contentRepository.findById(contentId));

        return new GenericResponseDto(new IdDto(createRepost(author, text, content)));
    }

    public Long createRepost(User author, String text, Content content) throws CustomException {

        if (content.getContent() != null) {
            return createRepost(author, text, content.getContent());
        } else {
            content.setRReposts(content.getRReposts() + 1);

            contentRepository.save(content);
        }

        Content repost = new Content();
        repost.setRLikes(0);
        repost.setRComments(0);
        repost.setRHasVideo(false);
        repost.setRReposts(0);
        repost.setRShares(0);
        repost.setTotalActivity(0);
        repost.setRUserImageUrl(author.getImageUrl());
        repost.setRUserRole(author.getRole());
        repost.setCreateTime(ZonedDateTime.now());
        repost.setUpdateTime(ZonedDateTime.now());
        repost.setIsDeleted(false);
        repost.setUser(author);
        repost.setHeadline("");
        repost.setRUsername(author.getUsername());
        repost.setType(ContentType.POST);
        repost.setContent(content);
        repost.setText(text);

        contentRepository.save(repost);

        notificationService.createUpdateNotification(content.getUser(), author, NotificationType.REPOSTED, repost, null, null);

        mentionService.createMentionsForContent(repost);

        UpdateContentNotificationDto updateContentNotificationDto = UpdateContentNotificationDto.builder().contentId(content.getId()).build();
        simpMessagingTemplate.convertAndSend("/content/update", updateContentNotificationDto);

        CreateContentNotificationDto createContentNotificationDto = new CreateContentNotificationDto();
        createContentNotificationDto.setContentId(repost.getId());
        createContentNotificationDto.setCreatedByUserId(repost.getUser().getId());
        simpMessagingTemplate.convertAndSend("/content/create", createContentNotificationDto);

        return repost.getId();
    }

    public void deleteMedia(Long principalId, Long contentId, String url, ContentMediaType type) throws CustomException {

        User user = userService.getByRepoMethod(userRepository.findById(principalId));
        Content content = getByRepoMethod(contentRepository.findById(contentId));

        if (user.getRole() != Role.ADMIN && content.getUser().getId() != principalId) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("CONTENT_NOT_MINE"));
        }

        deleteMediaUrl(content, url, type);
    }
}
