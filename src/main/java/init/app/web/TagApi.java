package init.app.web;

import com.jcabi.aspects.Loggable;
import init.app.component.HttpCustomEntity;
import init.app.domain.model.Tag;
import init.app.domain.model.enumeration.TagType;
import init.app.domain.repository.TagRepository;
import init.app.service.TagService;
import init.app.service.TaskSchedulerService;
import init.app.web.dto.request.TagRequestDto;
import init.app.web.dto.response.TagResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

import static init.app.exception.CustomExceptionHandler.handleException;
import static init.app.util.ConvertUtil.convertQueryToSingleResponse;
import static init.app.util.ConvertUtil.convertToAutocompleteResponse;
import static init.app.util.ConvertUtil.convertToPageResponse;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Loggable(trim = false, prepend = true)
@RestController
@RequestMapping("/tag")
public class TagApi {

    @Inject
    private HttpCustomEntity entity;
    @Inject
    private TagService tagService;
    @Inject
    private TagRepository tagRepository;
    @Inject
    private TaskSchedulerService taskSchedulerService;

    @RequestMapping(value = "/", method = POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ACTIVE') or hasAuthority('ADMIN') or hasAuthority('CONFIRMED')")
    public ResponseEntity create(@Valid @RequestBody TagRequestDto request) {
        try {
            return entity.response(tagService.create(request.getText()), CREATED);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/{id}", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity get(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long id) {
        try {
            return entity.response(convertQueryToSingleResponse(tagRepository.getOneById(id, principalId), TagResponseDto.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity delete(@PathVariable Long id) {
        try {
            tagService.delete(id);
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/all", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getPaged(@ApiIgnore @AuthenticationPrincipal Long principalId, @RequestParam(value = "type", required = false) TagType type, @RequestParam(value = "keyword", required = false) @Size(min = 1, max = 600) String keyword, @RequestParam("page") int page, @RequestParam("size") int size) {
        try {
            Page<Object[]> response = tagRepository.getAll(principalId, type!=null?type.name():null, keyword, false, new PageRequest(page, size));
            return entity.response(convertToPageResponse(response, TagResponseDto.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/autocomplete", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAll(@RequestParam(value = "keyword") @Size(min = 1, max = 600) String keyword) {
        try {
            List<Tag> response = tagRepository.findAllByTextContainingAndIsDeletedFalse(keyword);
            return entity.response(convertToAutocompleteResponse(new ArrayList<>(response), TagResponseDto.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

}
