package init.app.service;

import com.jcabi.aspects.Loggable;
import init.app.configuration.ConfigProperties;
import init.app.configuration.security.JWTUtils;
import init.app.domain.model.*;
import init.app.domain.model.enumeration.Role;
import init.app.domain.model.enumeration.SocialType;
import init.app.domain.repository.*;
import init.app.exception.CustomException;
import init.app.web.dto.parent.IdDto;
import init.app.web.dto.request.AssignTagRequestDto;
import init.app.web.dto.response.ImageUrlResponseUrl;
import init.app.web.dto.response.SignInResponseDto;
import init.app.web.dto.response.SimpleTagResponseDto;
import init.app.web.dto.shared.GenericResponseDto;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.servlet.ServletException;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@Loggable(trim = false, prepend = true)
@Transactional(rollbackFor = Exception.class)
public class UserService {

    @Inject
    private UserRepository userRepository;
    @Inject
    private PasswordEncoder passwordEncoder;
    @Inject
    @Lazy
    private AuthenticationService authenticationService;
    @Inject
    private FileService fileService;
    @Inject
    private EntityTagRepository entityTagRepository;
    @Inject
    private TagService tagService;
    @Inject
    private TagRepository tagRepository;
    @Inject
    private FollowRepository followRepository;
    @Inject
    private FollowService followService;
    @Inject
    private MailService mailService;
    @Inject
    ConfigProperties configProperties;

    public GenericResponseDto createProfile(Long principalId, String username, String bio) throws CustomException {

        User user = update(principalId, username, bio);

        authenticationService.changeRole(user, Role.ACTIVE);

        if(StringUtils.hasText(user.getEmail())) {
            String subject = ResourceBundle.getBundle("i18n.mail", Locale.ENGLISH).getString("WELCOME_SUBJECT");
            String textPlain = ResourceBundle.getBundle("i18n.mail", Locale.ENGLISH).getString("WELCOME_TEXT_PLAIN").replace("WELCOME_VIDEO_URL", configProperties.getWelcomevideourl()).replace("BASE_URL", configProperties.getFrontendbaseurl());
            String textHtml = ResourceBundle.getBundle("i18n.mail", Locale.ENGLISH).getString("WELCOME_TEXT_HTML").replace("WELCOME_VIDEO_URL", configProperties.getWelcomevideourl()).replace("BASE_URL", configProperties.getFrontendbaseurl());
            mailService.send(user.getEmail(), subject, textPlain, textHtml);
        }

        return new GenericResponseDto(new SignInResponseDto(JWTUtils.generateToken(principalId, Role.ACTIVE), user.getUsername(), user.getRole(), user.getId(), user.getEmail(), user.getImageUrl(), getAllUserTags(user)));
    }

    public User update(Long principalId, String username, String bio) throws CustomException {
        User user = getByRepoMethod(userRepository.findById(principalId));

        user.setUsername(username);
        user.setBio(bio);
        user.setUpdateTime(ZonedDateTime.now());
        userRepository.save(user);

        return user;
    }

    @Async
    public void updatePassword(User user, String password) throws CustomException {

        user.setPassword(passwordEncoder.encode(password));
        user.setUpdateTime(ZonedDateTime.now());
        userRepository.save(user);
    }

    @Async
    public void updateImageUrl(Long principalId, String imageUrl) throws CustomException {

        User user = getByRepoMethod(userRepository.findById(principalId));
        user.setImageUrl(imageUrl);
        user.setUpdateTime(ZonedDateTime.now());
        userRepository.save(user);
    }

    public ImageUrlResponseUrl uploadImageUrl(Long userId, MultipartFile file) throws CustomException {
        String key;
        try {
            key = fileService.getMediaUrl(file);
        } catch (IOException | ServletException e) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("BUCKET_UPLOAD"));
        }
        updateImageUrl(userId, key);

        ImageUrlResponseUrl response = new ImageUrlResponseUrl();
        response.setImageUrl(key);

        return response;
    }

    protected User createUser(String email, String password) {

        User user = new User();

        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.UNCONFIRMED);

        user.setCreateTime(ZonedDateTime.now());
        user.setUpdateTime(ZonedDateTime.now());
        user.setIsDeleted(false);
        user.setRFollowers(0);
        user.setRFollowing(0);
        userRepository.save(user);

        return user;
    }

    protected User createUpdateUser(User user, String email, String username, SocialType type, String socialId, String imageUrl) {

        if (user == null) {

            if (email != null) {
                user = userRepository.findByEmail(email);
            }

            if (user == null) {

                user = new User();

                user.setCreateTime(ZonedDateTime.now());
                user.setIsDeleted(false);
                user.setRFollowers(0);
                user.setRFollowing(0);
                user.setRole(Role.CONFIRMED);
            } else if (user.getRole() == Role.UNCONFIRMED) {
                user.setRole(Role.CONFIRMED);
            }
        } else if (user.getRole() == Role.UNCONFIRMED) {
            user.setRole(Role.CONFIRMED);
        }

        user.setEmail(email);
        user.setUsername(username);
        user.setImageUrl(imageUrl);
        switch (type) {
            case GOOGLE:
                user.setGoogleId(socialId);
                break;
            case FACEBOOK:
                user.setFacebookId(socialId);
                break;
            case TWITTER:
                user.setTwitterId(socialId);
                break;
        }

        user.setUpdateTime(ZonedDateTime.now());
        userRepository.save(user);

        return user;
    }

    public User getByRepoMethod(User user) throws CustomException {

        if (user == null) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("USER_NOT_EXIST"));
        } else if (user.getIsDeleted()) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("USER_DELETED"));
        }

        return user;
    }

    public void updateUserTags(List<AssignTagRequestDto> tagList, Long principalId) throws CustomException {
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
                    tagIdList.add(((IdDto)tagCreationResponse.getContent()).getId());
                }
            }
        }

        User user = getByRepoMethod(userRepository.findById(principalId));

        HashSet<Long> uniqueTagList = new HashSet<>(tagIdList);

        for (Long tagId : uniqueTagList) {
            Tag tag = tagService.getByRepoMethod(tagRepository.findById(tagId));
            Follow follow = followRepository.findByUserAndFollowedTag(user, tag);

            if (follow == null || follow.getIsDeleted()) {
                followService.followTag(user, tagId);
            }
        }

        List<EntityTag> entityTagsForUser = entityTagRepository.findAllByUser(user);

        for (EntityTag entityTag : entityTagsForUser) {
            if (uniqueTagList.contains(entityTag.getTag().getId())) {
                uniqueTagList.remove(entityTag.getTag().getId());
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

        for (Long tagId : uniqueTagList) {
            Tag tag = tagService.getByRepoMethod(tagRepository.findById(tagId));
            EntityTag entityTag = new EntityTag();
            entityTag.setIsDeleted(false);
            entityTag.setUpdateTime(ZonedDateTime.now());
            entityTag.setCreateTime(ZonedDateTime.now());
            entityTag.setUser(user);
            entityTag.setTag(tag);

            entityTagRepository.save(entityTag);
        }

        userRepository.save(user);
    }

    public List<SimpleTagResponseDto> getAllUserTags(User user) {

        List<SimpleTagResponseDto> response = new ArrayList<>();

        List<EntityTag> userTags = entityTagRepository.findAllByUserAndIsDeletedFalse(user);

        for (EntityTag userTag : userTags) {
            SimpleTagResponseDto tagResponseDto = new SimpleTagResponseDto();
            tagResponseDto.setId(userTag.getTag().getId());
            tagResponseDto.setText(userTag.getTag().getText());

            response.add(tagResponseDto);
        }

        return response;
    }
}
