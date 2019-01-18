package init.app.web;

import com.jcabi.aspects.Loggable;
import init.app.component.HttpCustomEntity;
import init.app.domain.model.enumeration.ChoiceType;
import init.app.domain.repository.ContentRepository;
import init.app.domain.repository.VoteRepository;
import init.app.service.ContentService;
import init.app.service.TagService;
import init.app.service.VoteService;
import init.app.web.dto.request.SpecialVoteNominationRequestDto;
import init.app.web.dto.response.SpecialPollVoteResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;

import static init.app.exception.CustomExceptionHandler.handleException;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Loggable(trim = false, prepend = true)
@RestController
@RequestMapping("/poll")
public class VoteApi {

    @Inject
    private HttpCustomEntity entity;
    @Inject
    private VoteService voteService;
    @Inject
    private VoteRepository voteRepository;
    @Inject
    private TagService tagService;
    @Inject
    private ContentService contentService;
    @Inject
    private ContentRepository contentRepository;

    @RequestMapping(value = "{pollId}/vote", method = POST)
    public ResponseEntity create(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long pollId, @RequestParam("choice") ChoiceType choice) {
        try {
            voteService.create(principalId, pollId, choice);
            tagService.updateTagsTempActivity(contentService.getTagsForContent(contentRepository.findByPollId(pollId)!=null?contentRepository.findByPollId(pollId).getId():null));
            return entity.responseCreated();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "{pollId}/nominate", method = POST)
    public ResponseEntity nominate(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long pollId, @RequestBody SpecialVoteNominationRequestDto request) {
        try {
            voteService.nominate(principalId, pollId, request.getSpecialVoteNomination());
            return entity.responseCreated();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "{pollId}/vote", method = DELETE)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ACTIVE')")
    public ResponseEntity delete(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long pollId) {
        try {
            voteService.delete(principalId, pollId);
        } catch (Exception e) {
            return handleException(e);
        }
        return entity.responseCreated();
    }

    @RequestMapping(value = "{pollId}/vote/all", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity allForAdmin(@RequestParam("page") int page, @RequestParam("size") int size, @PathVariable Long pollId) {
        try {
            Page<SpecialPollVoteResponseDto> response = voteRepository.getAllVotesForSpecialPoll(pollId, new PageRequest(page, size));
            return entity.response(response);
        } catch (Exception e) {
            return handleException(e);
        }
    }

}
