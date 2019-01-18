package init.app.web;

import com.jcabi.aspects.Loggable;
import init.app.component.HttpCustomEntity;
import init.app.domain.repository.CategoryRepository;
import init.app.service.CategoryService;
import init.app.web.dto.request.CategoryRequestDto;
import init.app.web.dto.response.CategoryResponseDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;

import static init.app.exception.CustomExceptionHandler.handleException;
import static init.app.util.ConvertUtil.convertToAutocompleteResponse;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Loggable(trim = false, prepend = true)
@RestController
@RequestMapping("/category")
public class CategoryApi {

    @Inject
    private HttpCustomEntity entity;
    @Inject
    private CategoryService categoryService;
    @Inject
    private CategoryRepository categoryRepository;

    @RequestMapping(value = "/", method = POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity create(@Valid @RequestBody CategoryRequestDto request) {
        try {
            categoryService.create(request.getText());
            return entity.responseCreated();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity delete(@PathVariable Long id) {
        try {
            categoryService.delete(id);
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/all", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAll() {
        try {
            List<CategoryResponseDto> response = categoryRepository.getAll();
            return entity.response(convertToAutocompleteResponse(response));
        } catch (Exception e) {
            return handleException(e);
        }
    }
}
