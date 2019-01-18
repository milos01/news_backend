package init.app.web;

import com.jcabi.aspects.Loggable;
import init.app.component.HttpCustomEntity;
import init.app.service.AdService;
import init.app.service.CommentService;
import init.app.service.ContentService;
import init.app.service.UserService;
import init.app.web.dto.request.ContentMediaUrlTypeRequestDto;
import init.app.web.dto.request.UrlTypeRequestDto;
import init.app.web.dto.response.ImageUrlResponseUrl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static init.app.exception.CustomExceptionHandler.handleException;

@Loggable(trim = false, prepend = true)
@RestController
@RequestMapping("/file")
public class FileApi {

    @Inject
    private HttpCustomEntity entity;
    @Inject
    private UserService userService;
    @Inject
    private ContentService contentService;
    @Inject
    private CommentService commentService;
    @Inject
    private AdService adService;

    @RequestMapping(value = "/user/profile-image", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('UNCONFIRMED') or hasAuthority('CONFIRMED') or hasAuthority('ACTIVE') or hasAuthority('ADMIN')")
    public ResponseEntity changeMyProfileImage(@Valid @RequestParam("file") MultipartFile file, @ApiIgnore @AuthenticationPrincipal Long principalId) {
        try {
            ImageUrlResponseUrl response = userService.uploadImageUrl(principalId, file);
            return entity.response(response);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/user/{id}/profile-image", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('CONFIRMED') or hasAuthority('ACTIVE') or hasAuthority('ADMIN')")
    public ResponseEntity changeProfileImage(@PathVariable Long id, @Valid @RequestParam("file") MultipartFile file) {
        try {
            userService.uploadImageUrl(id, file);
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/content/{id}/image/{order}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('ACTIVE') or hasAuthority('ADMIN')")
    public ResponseEntity addImageToArticle(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long id, @PathVariable @Min(1) @Max(3) Integer order, @Valid @RequestParam("file") MultipartFile file) {
        try {
            contentService.uploadImage(principalId, id, file, order);
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/comment/{id}/image", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('UNCONFIRMED') or hasAuthority('CONFIRMED') or hasAuthority('ACTIVE') or hasAuthority('ADMIN')")
    public ResponseEntity addImageToComment(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long id, @Valid @RequestParam("file") MultipartFile file) {
        try {
            commentService.uploadImage(principalId, id, file);
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/content/{id}/media", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('ACTIVE') or hasAuthority('ADMIN')")
    public ResponseEntity addImageToContent(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long id, @Valid @RequestBody ContentMediaUrlTypeRequestDto request) {
        try {
            contentService.addMediaUrl(principalId, request.getUrl(), id, request.getMediaType(), request.getOrder());
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/content/{id}/media", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ACTIVE') or hasAuthority('ADMIN')")
    public ResponseEntity deleteImageFromContent(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long id, @Valid @RequestBody UrlTypeRequestDto urlTypeRequestDto) {
        try {
            contentService.deleteMedia(principalId, id, urlTypeRequestDto.getUrl(), urlTypeRequestDto.getMediaType());
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/ad/{id}/image", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('ACTIVE') or hasAuthority('ADMIN')")
    public ResponseEntity addImageToAd(@PathVariable Long id, @Valid @RequestParam("file") MultipartFile file) {
        try {
            adService.uploadImageForAd(id, file);
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }
}
