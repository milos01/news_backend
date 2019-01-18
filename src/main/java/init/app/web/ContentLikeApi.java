package init.app.web;

import com.jcabi.aspects.Loggable;
import init.app.component.HttpCustomEntity;
import init.app.domain.model.enumeration.ReactionType;
import init.app.domain.repository.ContentLikeRepository;
import init.app.service.ContentLikeService;
import init.app.service.ContentService;
import init.app.service.TagService;
import init.app.web.dto.response.ContentLikesResponseDto;
import init.app.web.dto.response.UserLikesResponseDto;
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
public class ContentLikeApi {

    @Inject
    private HttpCustomEntity entity;
    @Inject
    private ContentLikeService contentLikeService;
    @Inject
    private ContentLikeRepository likeRepository;
    @Inject
    private TagService tagService;
    @Inject
    private ContentService contentService;

    @RequestMapping(value = "/content/{id}/like", method = POST)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ACTIVE')")
    public ResponseEntity create(@ApiIgnore @AuthenticationPrincipal Long principalId, @RequestParam("type") ReactionType type, @PathVariable Long id) {
        try {
            contentLikeService.create(principalId, type, id);
            tagService.updateTagsTempActivity(contentService.getTagsForContent(id));
            return entity.responseCreated();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/content/{id}/like", method = DELETE)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ACTIVE')")
    public ResponseEntity delete(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long id) {
        try {
            contentLikeService.delete(principalId, id);
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/content/{id}/likes", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAllForContent(@PathVariable  Long id, @RequestParam("page") int page, @RequestParam("size") int size) {
        try {
            Page<ContentLikesResponseDto> response = likeRepository.getAllForContent(id, new PageRequest(page, size));
            return entity.response(response);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/user/{id}/likes", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity getAllByUser(@PathVariable  Long id, @RequestParam("page") int page, @RequestParam("size") int size) {
        try {
            Page<UserLikesResponseDto> response = likeRepository.getAllUserLiked(id, new PageRequest(page, size));
            return entity.response(response);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/user/likes", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ACTIVE')")
    public ResponseEntity getAllMy(@ApiIgnore @AuthenticationPrincipal Long principalId, @RequestParam("page") int page, @RequestParam("size") int size) {
        try {
            Page<UserLikesResponseDto> response = likeRepository.getAllUserLiked(principalId, new PageRequest(page, size));
            return entity.response(response);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping(value = "/content/{id}/react")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ACTIVE')")
    public ResponseEntity react(@RequestParam("type") ReactionType type, @PathVariable Long id){
        try {
//            contentLikeService.react(id);
            return entity.response(null);
        } catch (Exception e) {
            return handleException(e);
        }

    }

}
