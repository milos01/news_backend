package init.app.web;

import com.jcabi.aspects.Loggable;
import init.app.component.HttpCustomEntity;
import init.app.service.ContentService;
import init.app.service.ShareService;
import init.app.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;

import static init.app.exception.CustomExceptionHandler.handleException;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Loggable(trim = false, prepend = true)
@RestController
public class ShareApi {

    @Inject
    private HttpCustomEntity entity;
    @Inject
    private ShareService shareService;
    @Inject
    private TagService tagService;
    @Inject
    private ContentService contentService;

    @RequestMapping(value = "/share/content/{id}", method = POST)
    public ResponseEntity create(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long id) {
        try {
            shareService.create(principalId, id);
            tagService.updateTagsTempActivity(contentService.getTagsForContent(id));
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }
}
