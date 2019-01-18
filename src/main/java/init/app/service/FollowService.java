package init.app.service;

import com.jcabi.aspects.Loggable;
import init.app.domain.model.Follow;
import init.app.domain.model.Tag;
import init.app.domain.model.User;
import init.app.domain.model.enumeration.FollowType;
import init.app.domain.model.enumeration.NotificationType;
import init.app.domain.model.enumeration.Role;
import init.app.domain.repository.FollowRepository;
import init.app.domain.repository.TagRepository;
import init.app.domain.repository.UserRepository;
import init.app.exception.CustomException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.ResourceBundle;

@Service
@Loggable(trim = false, prepend = true)
@Transactional(rollbackFor = Exception.class)
public class FollowService {

    @Inject
    FollowRepository followRepository;

    @Inject
    @Lazy
    UserService userService;

    @Inject
    UserRepository userRepository;

    @Inject
    TagService tagService;

    @Inject
    TagRepository tagRepository;

    @Inject
    NotificationService notificationService;

    public void follow(Long principalId, FollowType type, Long entityId) throws CustomException {

        User loggedInUser = userService.getByRepoMethod(userRepository.findById(principalId));

        switch (type) {
            case TAG:
                followTag(loggedInUser, entityId);
                break;
            case USER:
                followUser(loggedInUser, entityId);
                break;
        }

    }

    public void followUser(User loggedInUser, Long userToFollowId) throws CustomException {

        if (loggedInUser.getId().equals(userToFollowId)) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("CANNOT_FOLLOW_YOURSELF"));
        }

        User userToFollow = userService.getByRepoMethod(userRepository.findById(userToFollowId));

        if(userToFollow.getRole().equals(Role.BANNED) || userToFollow.getRole().equals(Role.DEACTIVATED)) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("CANNOT_FOLLOW_BANNED_DEACTIVATED_USER"));
        }

        Follow follow = followRepository.findByUserAndFollowedUser(loggedInUser, userToFollow);

        if (follow == null) {
            follow = new Follow();
            follow.setUser(loggedInUser);
            follow.setFollowedUser(userToFollow);
            follow.setCreateTime(ZonedDateTime.now());
            follow.setUpdateTime(ZonedDateTime.now());
            follow.setIsDeleted(false);
        } else {
            if (follow.getIsDeleted()) {
                follow.setIsDeleted(false);
                follow.setUpdateTime(ZonedDateTime.now());
            } else {
                throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("YOU_ALREADY_FOLLOW_USER"));
            }
        }

        followRepository.save(follow);

        notificationService.createUpdateNotification(userToFollow, loggedInUser, NotificationType.FOLLOWED, null, null, null);
    }

    public void followTag(User loggedInUser, Long tagToFollowId) throws CustomException {

        Tag tagToFollow = tagService.getByRepoMethod(tagRepository.findById(tagToFollowId));

        Follow follow = followRepository.findByUserAndFollowedTag(loggedInUser, tagToFollow);

        if (follow == null) {
            follow = new Follow();
            follow.setUser(loggedInUser);
            follow.setFollowedTag(tagToFollow);
            follow.setCreateTime(ZonedDateTime.now());
            follow.setUpdateTime(ZonedDateTime.now());
            follow.setIsDeleted(false);
        } else {
            if (follow.getIsDeleted()) {
                follow.setIsDeleted(false);
                follow.setUpdateTime(ZonedDateTime.now());
            } else {
                throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("YOU_ALREADY_FOLLOW_TAG"));
            }
        }

        followRepository.save(follow);
    }

    public void unfollow(Long principalId, FollowType type, Long entityId) throws CustomException {


        User loggedInUser = userService.getByRepoMethod(userRepository.findById(principalId));

        switch (type) {
            case TAG:
                unfollowTag(loggedInUser, entityId);
                break;
            case USER:
                unfollowUser(loggedInUser, entityId);
                break;
        }
    }

    public void unfollowUser(User loggedInUser, Long userToUnfollowId) throws CustomException {

        User userToUnfollow = userService.getByRepoMethod(userRepository.findById(userToUnfollowId));

        Follow follow = followRepository.findByUserAndFollowedUser(loggedInUser, userToUnfollow);

        if (follow == null) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("YOU_ARE_NOT_FOLLOWING_USER"));
        } else {
            if (follow.getIsDeleted()) {
                throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("YOU_ARE_NOT_FOLLOWING_USER"));
            } else {
                follow.setIsDeleted(true);
                follow.setUpdateTime(ZonedDateTime.now());

                followRepository.save(follow);
            }
        }
    }

    public void unfollowTag(User loggedInUser, Long tagToUnfollowId) throws CustomException {
        Tag tagToUnfollow = tagService.getByRepoMethod(tagRepository.findById(tagToUnfollowId));

        Follow follow = followRepository.findByUserAndFollowedTag(loggedInUser, tagToUnfollow);

        if (follow == null) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("YOU_ARE_NOT_FOLLOWING_TAG"));
        } else {
            if (follow.getIsDeleted()) {
                throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("YOU_ARE_NOT_FOLLOWING_TAG"));
            } else {
                follow.setIsDeleted(true);
                follow.setUpdateTime(ZonedDateTime.now());

                followRepository.save(follow);
            }
        }
    }
}
