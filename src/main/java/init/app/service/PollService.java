package init.app.service;

import com.jcabi.aspects.Loggable;
import init.app.domain.model.*;
import init.app.domain.model.enumeration.ContentType;
import init.app.domain.model.enumeration.PollType;
import init.app.domain.model.enumeration.Role;
import init.app.domain.repository.*;
import init.app.exception.CustomException;
import init.app.web.dto.parent.IdDto;
import init.app.web.dto.shared.GenericResponseDto;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Service
@Loggable(trim = false, prepend = true)
@Transactional(rollbackFor = Exception.class)
public class PollService {

    @Inject
    private UserRepository userRepository;
    @Inject
    private ContentRepository contentRepository;
    @Inject
    private PollRepository pollRepository;
    @Inject
    private UserPollRepository userPollRepository;
    @Inject
    private ContentService contentService;
    @Inject
    private UserService userService;
    @Inject
    private VoteRepository voteRepository;
    @Inject
    private VoteService voteService;

    public GenericResponseDto create(Long principalId, Long contentId, String question, String choice1, String choice2, String choice3) throws CustomException {

        User user = userService.getByRepoMethod(userRepository.findById(principalId));

        PollType type;
        switch (user.getRole()) {
            case ADMIN:
                type = contentId == null ? PollType.SPECIAL : PollType.ARTICLE;
                break;
            default:
                if(contentId == null) {
                    throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("POLL_HAS_TO_HAVE_CONTENT"));
                }
                type = PollType.POST;
        }

        Poll poll = createUpdatePoll(null, type, question, choice1, choice2, choice3);

        if (contentId != null) {
            Content content = contentService.getByRepoMethod(contentRepository.findById(contentId));
            if (user.getRole() != Role.ADMIN && content.getUser() != user) {
                throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("CONTENT_NOT_MINE"));
            }

            contentRepository.updateContentPoll(poll.getId(), contentId);
        }

        return new GenericResponseDto(new IdDto(poll.getId()));
    }

    public void update(Long principalId, Long pollId, String question, String choice1, String choice2, String choice3) throws CustomException {

        User user = userService.getByRepoMethod(userRepository.findById(principalId));

        Poll poll = getByRepoMethod(pollRepository.findById(pollId));

        if(poll.getType() != PollType.SPECIAL){
            Content content = contentService.getByRepoMethod(contentRepository.findByPollId(pollId));
            if (user.getRole() != Role.ADMIN && content.getUser() != user) {
                throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("CONTENT_NOT_MINE"));
            }

            contentRepository.updateContentPoll(poll.getId(), content.getId());
        }

        createUpdatePoll(poll, poll.getType(), question, choice1, choice2, choice3);
    }

    public void delete(Long principalId, Long pollId) throws CustomException {

        User user = userService.getByRepoMethod(userRepository.findById(principalId));

        Content content = contentService.getByRepoMethod(contentRepository.findByPollId(pollId));
        if (user.getRole() != Role.ADMIN && content.getUser() != user) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("CONTENT_NOT_MINE"));
        }

        deletePoll(getByRepoMethod(pollRepository.findById(pollId)));

        contentRepository.updateContentPoll(null, content.getId());
    }

    public void changeType(Long pollId, PollType type) throws CustomException {

        if(type == PollType.OF_THE_DAY) {
            setPollOfTheDay(pollId);
        } else {

            Poll poll = getByRepoMethod(pollRepository.findById(pollId));

            poll.setUpdateTime(ZonedDateTime.now());
            poll.setType(type);

            pollRepository.save(poll);
        }
    }

    private Poll createUpdatePoll(Poll poll, PollType type, String question, String choice1, String choice2, String choice3) throws CustomException {

        if (poll == null) {

            if(type == PollType.SPECIAL) {
                List<Poll> oldSpecialPolls = pollRepository.findAllByTypeAndIsDeletedIsFalse(PollType.SPECIAL);

                for (Poll oldSpecialPoll : oldSpecialPolls) {
                    oldSpecialPoll.setIsDeleted(true);
                    oldSpecialPoll.setUpdateTime(ZonedDateTime.now());

                    pollRepository.save(oldSpecialPoll);
                }

            }

            poll = new Poll();

            poll.setType(type);
            poll.setCreateTime(ZonedDateTime.now());
            poll.setIsDeleted(false);

            poll.setRFirstAmount(0);
            poll.setRSecondAmount(0);
            poll.setRThirdAmount(0);
        }

        poll.setQuestion(question);
        poll.setFirstChoice(choice1);
        poll.setSecondChoice(choice2);
        poll.setThirdChoice(choice3);

        poll.setUpdateTime(ZonedDateTime.now());

        pollRepository.save(poll);

        return poll;
    }

    public void setPollOfTheDay(Long pollId) throws CustomException {

        Content content = contentService.getByRepoMethod(contentRepository.findByPollId(pollId));

        if(content.getType() == ContentType.POST || content.getType() == ContentType.FOOTER || content.getType() == ContentType.ARTICLE_DRAFT) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("POLL_OF_THE_DAY_HAS_TO_BE_FINISHED_REGULAR_ARTICLE"));
        }

        setPollOfTheDay(getByRepoMethod(pollRepository.findById(pollId)));
    }

    public void setPollOfTheDay(Poll poll) throws CustomException {

        List<Poll> oldPollsOfTheDay = pollRepository.findAllByTypeAndIsDeletedIsFalse(PollType.OF_THE_DAY);

        for (Poll pollOfTheDay : oldPollsOfTheDay) {
            pollOfTheDay.setType(PollType.ARTICLE);
            pollOfTheDay.setUpdateTime(ZonedDateTime.now());

            pollRepository.save(pollOfTheDay);
        }

        poll.setUpdateTime(ZonedDateTime.now());
        poll.setType(PollType.OF_THE_DAY);

        pollRepository.save(poll);
    }

    public Poll getByRepoMethod(Poll poll) throws CustomException {

        if (poll == null) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("POLL_NOT_EXIST"));
        } else if (poll.getIsDeleted()) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("POLL_DELETED"));
        }

        return poll;
    }

    public void followPoll(Long principalId, Long pollId) throws CustomException{

        User user = userService.getByRepoMethod(userRepository.findById(principalId));

        Poll poll = this.getByRepoMethod(pollRepository.findById(pollId));

        Boolean existingPollFollow = userPollRepository.existsByUserAndPollAndIsDeletedFalse(user, poll);

        if (existingPollFollow) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("ALREADY_FOLLOWED_POLL"));
        }

        UserPoll existingUserPoll = userPollRepository.findByUserAndPoll(user, poll);

        UserPoll userPoll;

        if (existingUserPoll == null) {
            userPoll = UserPoll.builder().user(user).poll(poll).isDeleted(false).createTime(ZonedDateTime.now()).build();
        }else{
            userPoll = existingUserPoll;
        }

        userPoll.setUpdateTime(ZonedDateTime.now());
        userPoll.setIsDeleted(false);
        userPollRepository.save(userPoll);
    }

    public void unfollowPoll(Long principalId, Long pollId) throws CustomException{

        User user = userService.getByRepoMethod(userRepository.findById(principalId));

        Poll poll = this.getByRepoMethod(pollRepository.findById(pollId));

        UserPoll existingPollFollow = userPollRepository.findByUserAndPollAndIsDeletedFalse(user, poll);

        if (existingPollFollow == null) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("POLL_NOT_FOLLOWED"));
        }

        existingPollFollow.setIsDeleted(true);
        userPollRepository.save(existingPollFollow);
    }

    @Async
    public void deletePoll(Poll poll) throws CustomException {

        poll.setUpdateTime(ZonedDateTime.now());
        poll.setIsDeleted(true);

        pollRepository.save(poll);

        List<Vote> pollVotes = voteRepository.findAllByPollAndIsDeletedFalse(poll);

        for (Vote vote : pollVotes) {
            voteService.deleteVote(vote);
        }
    }

}
