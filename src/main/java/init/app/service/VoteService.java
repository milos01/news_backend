package init.app.service;

import com.jcabi.aspects.Loggable;
import init.app.domain.model.Content;
import init.app.domain.model.Poll;
import init.app.domain.model.User;
import init.app.domain.model.Vote;
import init.app.domain.model.enumeration.ChoiceType;
import init.app.domain.model.enumeration.NotificationType;
import init.app.domain.model.enumeration.PollType;
import init.app.domain.repository.ContentRepository;
import init.app.domain.repository.PollRepository;
import init.app.domain.repository.UserRepository;
import init.app.domain.repository.VoteRepository;
import init.app.exception.CustomException;
import init.app.web.dto.custom.UpdateContentNotificationDto;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

@Service
@Loggable(trim = false, prepend = true)
@Transactional(rollbackFor = Exception.class)
public class VoteService {

    @Inject
    @Lazy
    PollService pollService;

    @Inject
    PollRepository pollRepository;

    @Inject
    VoteRepository voteRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    UserService userService;

    @Inject
    ContentRepository contentRepository;

    @Inject
    NotificationService notificationService;

    @Inject
    private SimpMessagingTemplate simpMessagingTemplate;

    public void create(Long principalId, Long pollId, ChoiceType choiceType) throws CustomException {

        User user = principalId != null ? userService.getByRepoMethod(userRepository.findById(principalId)) : null;

        createVote(pollId, choiceType, Optional.ofNullable(user));
    }

    public void nominate(Long principalId, Long pollId, String nomination) throws CustomException {
        User user = principalId != null ? userService.getByRepoMethod(userRepository.findById(principalId)) : null;

        createNomination(pollId, nomination, Optional.ofNullable(user));
    }

    private void createNomination(Long pollId, String nomination, Optional<User> user) throws CustomException {

        Poll poll = pollService.getByRepoMethod(pollRepository.findById(pollId));

        if (poll.getType() != PollType.SPECIAL) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("NOMINATION_IS_SPECIAL_VOTE_ONLY"));
        }

        Vote vote;

        if (user.isPresent()) {
            List<Vote> userVotes = voteRepository.findByPollAndUserAndIsDeletedFalse(poll, user.get());


            if (userVotes.isEmpty()) {
                vote = new Vote();
                vote.setIsDeleted(false);
                vote.setPoll(poll);
                vote.setUser(user.get());
                vote.setCreateTime(ZonedDateTime.now());
            } else {
                vote = userVotes.get(0);

                if (StringUtils.hasText(vote.getSpecialPollNomination())) {
                    throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("ALREADY_NOMINATED"));
                }
            }
        } else {
            vote = new Vote();
            vote.setIsDeleted(false);
            vote.setPoll(poll);
            vote.setCreateTime(ZonedDateTime.now());
        }
        vote.setSpecialPollNomination(nomination);
        vote.setUpdateTime(ZonedDateTime.now());

        voteRepository.save(vote);
    }

    public void createVote(Long pollId, ChoiceType choiceType, Optional<User> user) throws CustomException {

        Poll poll = pollService.getByRepoMethod(pollRepository.findById(pollId));

        Vote vote;

        if (user.isPresent()) {
            List<Vote> userVotes = voteRepository.findByPollAndUserAndIsDeletedFalse(poll, user.get());

            if (!userVotes.isEmpty()) {
                vote = userVotes.get(0);
                if (vote.getChoice() != null) {
                    throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("ALREADY_VOTED_ON_POLL"));
                }
            } else {
                vote = new Vote();
            }
        } else {
            vote = new Vote();
        }

        poll.setUpdateTime(ZonedDateTime.now());

        switch (choiceType) {
            case FIRST:
                poll.setRFirstAmount(poll.getRFirstAmount() + 1);
                break;
            case SECOND:
                poll.setRSecondAmount(poll.getRSecondAmount() + 1);
                break;
            case THIRD:
                if (!StringUtils.hasText(poll.getThirdChoice())) {
                    throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("POLL_HAS_NO_THIRD_OPTION"));
                }
                poll.setRThirdAmount(poll.getRThirdAmount() + 1);
                break;
        }

        pollRepository.save(poll);

        vote.setChoice(choiceType);
        vote.setCreateTime(ZonedDateTime.now());
        vote.setUpdateTime(ZonedDateTime.now());
        vote.setIsDeleted(false);
        vote.setPoll(poll);
        vote.setUser(user.orElse(null));

        voteRepository.save(vote);

        Content content = contentRepository.findByPollId(poll.getId());
        if (content != null) {
            notificationService.createUpdateNotification(content.getUser(), user.orElse(null), NotificationType.VOTED, content, null, poll);

            UpdateContentNotificationDto updateContentNotificationDto = UpdateContentNotificationDto.builder().contentId(content.getId()).build();
            simpMessagingTemplate.convertAndSend("/content/update", updateContentNotificationDto);
        }
    }

    public void delete(Long principalId, Long pollId) throws CustomException {
        User user = userService.getByRepoMethod(userRepository.findById(principalId));

        Poll poll = pollService.getByRepoMethod(pollRepository.findById(pollId));

        List<Vote> userVotes = voteRepository.findByPollAndUserAndIsDeletedFalse(poll, user);

        if (userVotes.isEmpty()) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("NOT_VOTED_ON_POLL"));
        }

        Vote userVote = userVotes.get(0);

        switch (userVote.getChoice()) {
            case FIRST:
                poll.setRFirstAmount(poll.getRFirstAmount() - 1);
                break;
            case SECOND:
                poll.setRSecondAmount(poll.getRSecondAmount() - 1);
                break;
            case THIRD:
                poll.setRThirdAmount(poll.getRThirdAmount() - 1);
                break;
        }
        poll.setUpdateTime(ZonedDateTime.now());

        pollRepository.save(poll);

        Content content = contentRepository.findByPollId(poll.getId());

        if (content != null) {
            UpdateContentNotificationDto updateContentNotificationDto = UpdateContentNotificationDto.builder().contentId(content.getId()).build();
            simpMessagingTemplate.convertAndSend("/content/update", updateContentNotificationDto);
        }
        
        deleteVote(userVote);
    }

    public void deleteVote(Vote vote) {
        vote.setIsDeleted(true);
        vote.setUpdateTime(ZonedDateTime.now());

        voteRepository.save(vote);
    }

    public Vote getByRepoMethod(Vote vote) throws CustomException {

        if (vote == null) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("VOTE_DELETED"));
        } else if (vote.getIsDeleted()) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("VOTE_NOT_EXIST"));
        }

        return vote;
    }

}
