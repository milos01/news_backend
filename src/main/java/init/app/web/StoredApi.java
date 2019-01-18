package init.app.web;

import com.jcabi.aspects.Loggable;
import init.app.component.HttpCustomEntity;
import init.app.service.StoredService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;

import static init.app.exception.CustomExceptionHandler.handleException;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Loggable(trim = false, prepend = true)
@RestController
@RequestMapping("/stored")
public class StoredApi {

    @Inject
    private HttpCustomEntity entity;
    @Inject
    private StoredService storedService;

    @RequestMapping(value = "/content/{id}", method = POST)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ACTIVE')")
    public ResponseEntity create(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long id) {
        try {
            storedService.storeContent(principalId, id);
            return entity.responseCreated();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/content/{id}", method = DELETE)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ACTIVE')")
    public ResponseEntity delete(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long id) {
        try {
            storedService.deleteContentStore(principalId, id);
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }

}
