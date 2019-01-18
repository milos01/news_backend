package init.app.web;

import com.jcabi.aspects.Loggable;
import init.app.component.HttpCustomEntity;
import init.app.domain.model.enumeration.AdType;
import init.app.domain.repository.AdRepository;
import init.app.service.AdService;
import init.app.web.dto.request.AdRequestDto;
import init.app.web.dto.response.AdResponseDto;
import init.app.web.dto.shared.GenericResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import static init.app.exception.CustomExceptionHandler.handleException;
import static init.app.util.ConvertUtil.convertToAutocompleteResponse;
import static init.app.util.ConvertUtil.convertToPageResponse;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Loggable(trim = false, prepend = true)
@RestController
@RequestMapping("/ad")
public class AdApi {

    @Inject
    private HttpCustomEntity<GenericResponseDto> entity;
    @Inject
    private AdService adService;
    @Inject
    private AdRepository adRepository;

    @RequestMapping(value = "/", method = POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity create(@Valid @RequestBody AdRequestDto request) {
        try {
            GenericResponseDto responseDto = adService.create(request.getType(), request.getHref());
            return entity.response(responseDto, CREATED);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/{id}", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity getById(@PathVariable Long id) {
        try {
            return entity.response(convertToAutocompleteResponse(adRepository.getById(id).orElse(null)));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity delete(@PathVariable Long id) {
        try {
            adService.delete(id);
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getShareOfVoice(@RequestParam(value = "type") AdType type, @RequestParam("size") int size) {
        try {
            GenericResponseDto genericResponseDto = adService.getAll(type, size);
            return entity.response(genericResponseDto);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/all", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity getAll(@RequestParam(value = "type", required = false) AdType type, @RequestParam(value = "isDeleted") Boolean isDeleted, @RequestParam("page") int page, @RequestParam("size") int size) {
        try {
            Page<Object[]> response = adRepository.getAll(type, isDeleted, new PageRequest(page, size));
            return entity.response(convertToPageResponse(response, AdResponseDto.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

}
