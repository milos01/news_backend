package init.app.web;

import com.jcabi.aspects.Loggable;
import init.app.component.HttpCustomEntity;
import init.app.domain.model.enumeration.FooterPageType;
import init.app.domain.repository.FooterPageRepository;
import init.app.service.FooterPageService;
import init.app.web.dto.parent.ListIdsDto;
import init.app.web.dto.request.FooterPageRequestDto;
import init.app.web.dto.response.FooterPageCreateResponseDto;
import init.app.web.dto.response.FooterPageResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;

import static init.app.exception.CustomExceptionHandler.handleException;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Loggable(trim = false, prepend = true)
@RestController
@RequestMapping("/footer-page")
public class FooterPageApi {

    @Inject
    private HttpCustomEntity entity;
    @Inject
    private FooterPageService footerPageService;
    @Inject
    private FooterPageRepository footerPageRepository;

    @RequestMapping(value = "/", method = POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity create(@ApiIgnore @AuthenticationPrincipal Long principalId, @Valid @RequestBody FooterPageRequestDto request) {
        try {
            FooterPageCreateResponseDto responseDto = footerPageService.create(principalId, request.getType(), request.getOrder(), request.getContent());
            return entity.response(responseDto, CREATED);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/{id}", method = PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity update(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long id, @Valid @RequestBody FooterPageRequestDto request) {
        try {
            footerPageService.update(principalId, id, request.getType(), request.getOrder(), request.getContent());
            return entity.response(HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/{id}", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity get(@PathVariable Long id) {
        try {
            FooterPageResponseDto response = footerPageRepository.fetchFooterPageById(id);
            return entity.response(response);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity delete(@PathVariable Long id) {
        try {
            footerPageService.delete(id);
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/all", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAll(@RequestParam(value = "type") FooterPageType type) {
        try {
            List<FooterPageResponseDto> response = footerPageRepository.fetchAllFooterPages(type);
            return entity.response(response);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/order", method = POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity create(@Valid @RequestBody ListIdsDto request) {
        try {
            footerPageService.order(request.getIds());
            entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
        return entity.responseCreated();
    }

}
