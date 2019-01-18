package init.app.web;

import com.jcabi.aspects.Loggable;
import init.app.component.HttpCustomEntity;
import init.app.domain.model.enumeration.ContentType;
import init.app.domain.model.enumeration.PollType;
import init.app.domain.repository.PollRepository;
import init.app.service.CsvExportService;
import init.app.service.PollService;
import init.app.util.ConvertUtil;
import init.app.web.dto.request.PollRequestDto;
import init.app.web.dto.response.CountResponseDto;
import init.app.web.dto.response.FollowPollResponseDto;
import init.app.web.dto.response.PollResponseDto;
import init.app.web.dto.response.SinglePollResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Size;

import java.util.List;

import static init.app.exception.CustomExceptionHandler.handleException;
import static init.app.util.ConvertUtil.convertQueryToSingleResponse;
import static init.app.util.ConvertUtil.convertToPageResponse;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Loggable(trim = false, prepend = true)
@RestController
@RequestMapping("/poll")
public class PollApi {

    @Inject
    private HttpCustomEntity entity;
    @Inject
    private PollService pollService;
    @Inject
    private PollRepository pollRepository;
    @Inject
    CsvExportService csvExportService;

    @RequestMapping(value = "/", method = POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ACTIVE')")
    public ResponseEntity create(@ApiIgnore @AuthenticationPrincipal Long principalId, @RequestParam(value = "content-id", required = false) Long contentId, @Valid @RequestBody PollRequestDto request) {
        try {
            return entity.response(pollService.create(principalId, contentId, request.getQuestion(), request.getFirstChoice(), request.getSecondChoice(), request.getThirdChoice()));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/{id}", method = PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ACTIVE')")
    public ResponseEntity update(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long id, @Valid @RequestBody PollRequestDto request) {
        try {
            pollService.update(principalId, id, request.getQuestion(), request.getFirstChoice(), request.getSecondChoice(), request.getThirdChoice());
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ACTIVE')")
    public ResponseEntity delete(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long id) {
        try {
            pollService.delete(principalId, id);
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getByType(@ApiIgnore @AuthenticationPrincipal Long principalId, @RequestParam("type") PollType type) {
        try {
            return entity.response(convertQueryToSingleResponse(pollRepository.findByType(principalId, type != null ? type.name() : null), SinglePollResponseDto.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/{id}/type", method = PUT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity changeType(@PathVariable Long id, @RequestParam("type") PollType type) {
        try {
            pollService.changeType(id, type);
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/all", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity getAll(@RequestParam(value = "type", required = false) PollType type, @RequestParam(value = "keyword", required = false) @Size(min = 1, max = 600) String keyword, @RequestParam("page") int page, @RequestParam("size") int size) {
        try {
            Page<Object[]> response = pollRepository.getAll(type == null ? null : type.name(), keyword, new PageRequest(page, size));
            return entity.response(convertToPageResponse(response, PollResponseDto.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/all/count", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity getAllCount(@RequestParam(value = "type", required = false) PollType type) {
        try {
            CountResponseDto countResponseDto = new CountResponseDto();
            countResponseDto.setCount(pollRepository.countAll(type == null ? null : type.name()));
            return entity.response(countResponseDto);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/export", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity export(@ApiIgnore @AuthenticationPrincipal Long principalId, @RequestParam(value = "type") ContentType type) {
        try {
            if(type == ContentType.ARTICLE_REGULAR) {
                csvExportService.exportArticlePollsToCsv(principalId);
            } else {
                csvExportService.exportPostPollsToCsv(principalId);
            }
            return entity.response(HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping(value = "/follow", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ACTIVE')")
    public ResponseEntity getFollowedPolls (@ApiIgnore @AuthenticationPrincipal Long principalId, @RequestParam("limit") int limit, @RequestParam("offset") int offset) {
        try {
            List<Object[]> response = pollRepository.getFollowedPolls(principalId, limit, offset);
            return entity.response(ConvertUtil.convertToAllResponse(response, FollowPollResponseDto.class));
        }catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping(value = "/follow", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ACTIVE')")
    public ResponseEntity followPoll (@ApiIgnore @AuthenticationPrincipal Long principalId,  @RequestParam("poll_id") Long pollId) {
        try {
            pollService.followPoll(principalId, pollId);
            return entity.responseCreated();
        }catch (Exception e) {
            return handleException(e);
        }
    }

    @DeleteMapping(value = "/follow", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ACTIVE')")
    public ResponseEntity unfollowPoll (@ApiIgnore @AuthenticationPrincipal Long principalId,  @RequestParam("poll_id") Long pollId) {
        try {
            pollService.unfollowPoll(principalId, pollId);
            return entity.responseCreated();
        }catch (Exception e) {
            return handleException(e);
        }
    }
}
