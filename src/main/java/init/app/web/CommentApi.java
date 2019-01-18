package init.app.web;

import com.jcabi.aspects.Loggable;
import init.app.component.HttpCustomEntity;
import init.app.domain.repository.CommentRepository;
import init.app.service.CommentService;
import init.app.service.ContentService;
import init.app.service.TagService;
import init.app.util.ConvertUtil;
import init.app.web.dto.request.CommentRequestDto;
import init.app.web.dto.response.CommentResponseDto;
import init.app.web.dto.shared.GenericResponseDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static init.app.exception.CustomExceptionHandler.handleException;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Loggable(trim = false, prepend = true)
@RestController
public class CommentApi {

    @Inject
    private HttpCustomEntity entity;
    @Inject
    private CommentService commentService;
    @Inject
    private CommentRepository commentRepository;
    @Inject
    private TagService tagService;
    @Inject
    private ContentService contentService;

    @RequestMapping(value = "/content/{contentId}/comment/", method = POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long contentId, @Valid @RequestBody CommentRequestDto request) {
        try {
            GenericResponseDto genericResponseDto = commentService.create(request.getText(), principalId, contentId, Optional.empty());
            tagService.updateTagsTempActivity(contentService.getTagsForContent(contentId));
            return entity.response(genericResponseDto, CREATED);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/content/{contentId}/comment/{id}/", method = POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity reply(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long contentId, @PathVariable Long id, @Valid @RequestBody CommentRequestDto request) {
        try {
            GenericResponseDto genericResponseDto = commentService.create(request.getText(), principalId, contentId, Optional.of(id));
            tagService.updateTagsTempActivity(contentService.getTagsForContent(contentId));
            return entity.response(genericResponseDto, CREATED);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/content/{contentId}/comment/all", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAllComments(@PathVariable Long contentId, @RequestParam("limit") int limit, @RequestParam("offset") int offset) {
        try {
            List<Object[]> response = commentRepository.getAll(contentId, null, limit, offset);
            return entity.response(ConvertUtil.convertToAllResponse(response, CommentResponseDto.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/content/{contentId}/comment/{id}/all", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAllReplies(@PathVariable Long contentId, @PathVariable Long id, @RequestParam("limit") int limit, @RequestParam("offset") int offset) {
        try {
            List<Object[]> response = commentRepository.getAll(contentId, id, limit, offset);
            return entity.response(ConvertUtil.convertToAllResponse(response, CommentResponseDto.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/comment/{id}", method = DELETE)
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('ACTIVE')")
    public ResponseEntity delete(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long id) {
        try {
            commentService.delete(principalId, id);
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/comment/{id}", method = GET)
    public ResponseEntity getComment(@PathVariable Long id) {
        try {
            CommentResponseDto commentResponseDto = commentRepository.getComment(id);
            return entity.response(commentResponseDto);
        } catch (Exception e) {
            return handleException(e);
        }
    }
}
