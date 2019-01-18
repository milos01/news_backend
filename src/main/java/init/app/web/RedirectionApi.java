package init.app.web;

import com.jcabi.aspects.Loggable;
import init.app.component.HttpCustomEntity;
import init.app.service.RedirectionService;
import init.app.web.dto.parent.UrlDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;

import static init.app.exception.CustomExceptionHandler.handleException;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Loggable(trim = false, prepend = true)
@RestController
@RequestMapping("/redirect")
public class RedirectionApi {

    @Inject
    private HttpCustomEntity entity;
    @Inject
    private RedirectionService redirectionService;

    @RequestMapping(method = POST)
    public ResponseEntity create(@RequestBody @Valid UrlDto request) {
        try {
            return entity.response(redirectionService.getNewUrl(request.getUrl()));
        } catch (Exception e) {
            return handleException(e);
        }
    }
}
