package init.app.web;

import com.jcabi.aspects.Loggable;
import init.app.component.HttpCustomEntity;
import init.app.domain.model.enumeration.FollowType;
import init.app.domain.repository.FollowRepository;
import init.app.service.FollowService;
import init.app.web.dto.parent.ExceptionDto;
import init.app.web.dto.response.TagResponseDto;
import init.app.web.dto.response.UserFollowResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;

import static init.app.exception.CustomExceptionHandler.handleException;
import static init.app.util.ConvertUtil.convertToPageResponse;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Loggable(trim = false, prepend = true)
@RestController
public class FollowApi {

    @Inject
    private HttpCustomEntity entity;
    @Inject
    private FollowService followService;
    @Inject
    private FollowRepository followRepository;

    @RequestMapping(value = "/{id}/follow", method = POST)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ACTIVE')")
    public ResponseEntity follow(@ApiIgnore @AuthenticationPrincipal Long principalId, @RequestParam("type") FollowType type, @PathVariable Long id) {
        try {
            followService.follow(principalId, type, id);
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/{id}/follow", method = DELETE)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ACTIVE')")
    public ResponseEntity unfollow(@ApiIgnore @AuthenticationPrincipal Long principalId, @RequestParam("type") FollowType type, @PathVariable Long id) {
        try {
            followService.unfollow(principalId, type, id);
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/followers", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ACTIVE')")
    public ResponseEntity getFollowers(@ApiIgnore @AuthenticationPrincipal Long principalId, @RequestParam("page") int page, @RequestParam("size") int size) {
        try {
            Page<Object[]> response = followRepository.getFollowers(principalId, FollowType.USER.name(), principalId, new PageRequest(page, size));
            return entity.response(convertToPageResponse(response, UserFollowResponseDto.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/user/{id}/followers", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getUserFollowers(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long id, @RequestParam("page") int page, @RequestParam("size") int size) {
        try {
            Page<Object[]> response = followRepository.getFollowers(id, FollowType.USER.name(), principalId, new PageRequest(page, size));
            return entity.response(convertToPageResponse(response, UserFollowResponseDto.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/tag/{id}/followers", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAll(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long id, @RequestParam("page") int page, @RequestParam("size") int size) {
        try {
            Page<Object[]> response = followRepository.getFollowers(id, FollowType.TAG.name(), principalId, new PageRequest(page, size));
            return entity.response(convertToPageResponse(response, UserFollowResponseDto.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/followed", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ACTIVE')")
    public ResponseEntity getFollowedUsers(@ApiIgnore @AuthenticationPrincipal Long principalId, @RequestParam("type") FollowType type, @RequestParam("page") int page, @RequestParam("size") int size) {
        try {
            switch (type) {
                case USER:

                    Page<Object[]> usersResponse = followRepository.getFollowedUsers(principalId, new PageRequest(page, size));
                    return entity.response(convertToPageResponse(usersResponse, UserFollowResponseDto.class));
                case TAG:
                    Page<Object[]> tagResponse = followRepository.getFollowedTags(principalId, new PageRequest(page, size));
                    return entity.response(convertToPageResponse(tagResponse, TagResponseDto.class));
            }

            return new ResponseEntity<>(new ExceptionDto("Oops, something went wrong!"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return handleException(e);
        }
    }
}
