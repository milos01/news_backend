package init.app.web;

import com.jcabi.aspects.Loggable;
import init.app.component.HttpCustomEntity;
import init.app.domain.model.enumeration.Role;
import init.app.domain.repository.TagRepository;
import init.app.domain.repository.UserRepository;
import init.app.service.CsvExportService;
import init.app.service.UserService;
import init.app.web.dto.request.TagListDto;
import init.app.web.dto.request.UserRequestDto;
import init.app.web.dto.response.AdminApiUserResponseDto;
import init.app.web.dto.response.CountResponseDto;
import init.app.web.dto.response.UserAutocompleteResponseDto;
import init.app.web.dto.response.UserPreviewResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

import static init.app.exception.CustomExceptionHandler.handleException;
import static init.app.util.ConvertUtil.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Loggable(trim = false, prepend = true)
@RestController
@RequestMapping("/user")
public class UserApi {

    @Inject
    private HttpCustomEntity entity;
    @Inject
    private UserService userService;
    @Inject
    private UserRepository userRepository;
    @Inject
    private TagRepository tagRepository;
    @Inject
    private CsvExportService csvExportService;

    @RequestMapping(value = "/", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('UNCONFIRMED') or hasAuthority('CONFIRMED') or hasAuthority('ACTIVE') or hasAuthority('ADMIN')")
    public ResponseEntity getMe(@ApiIgnore @AuthenticationPrincipal Long principalId) {
        try {
            return entity.response(convertQueryToSingleResponse(userRepository.customFindById(principalId, principalId), UserPreviewResponseDto.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/{id}", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity get(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long id) {
        try {
            return entity.response(convertQueryToSingleResponse(userRepository.customFindById(id, principalId), UserPreviewResponseDto.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/create-profile", method = PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('CONFIRMED')")
    public ResponseEntity createProfile(@ApiIgnore @AuthenticationPrincipal Long principalId, @Valid @RequestBody UserRequestDto request) {
        try {
            return entity.response(userService.createProfile(principalId, request.getUsername(), request.getBio()));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/", method = PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('CONFIRMED') or hasAuthority('ACTIVE') or hasAuthority('ADMIN')")
    public ResponseEntity updateMe(@ApiIgnore @AuthenticationPrincipal Long principalId, @Valid @RequestBody UserRequestDto request) {
        try {
            userService.update(principalId, request.getUsername(), request.getBio());
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/{id}", method = PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity update(@PathVariable Long id, @Valid @RequestBody UserRequestDto request) {
        try {
            userService.update(id, request.getUsername(), request.getBio());
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/all", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getUsers(@ApiIgnore @AuthenticationPrincipal Long principalId, @RequestParam(value = "keyword", required = false) @Size(min = 1, max = 600) String keyword, @RequestParam(value = "role", required = false) Role role, @RequestParam("page") int page, @RequestParam("size") int size) {
        try {
            Page<Object[]> response = userRepository.findAll(principalId, keyword, role == null ? null : role.name(), new PageRequest(page, size));
            return entity.response(convertToPageResponse(response, UserPreviewResponseDto.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/all/count", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity count() {
        try {
            CountResponseDto countResponseDto = new CountResponseDto();
            countResponseDto.setCount(userRepository.countAll());
            return entity.response(countResponseDto);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/tags", method = PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('CONFIRMED') or hasAuthority('ACTIVE') or hasAuthority('ADMIN')")
    public ResponseEntity updateTags(@ApiIgnore @AuthenticationPrincipal Long principalId, @Valid @RequestBody TagListDto request) {
        try {
            userService.updateUserTags(request.getTagList(), principalId);
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/tags", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('CONFIRMED') or hasAuthority('ACTIVE') or hasAuthority('ADMIN')")
    public ResponseEntity getMyTags(@ApiIgnore @AuthenticationPrincipal Long principalId) {
        try {
            return entity.response(convertToAutocompleteResponse(tagRepository.getUserTags(principalId)));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/{id}/tags", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getUserTags(@PathVariable Long id) {
        try {
            return entity.response(convertToAutocompleteResponse(tagRepository.getUserTags(id)));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/autocomplete", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity autocomplete(@RequestParam(value = "keyword") @Size(min = 1, max = 600) String keyword) {
        try {
            List<UserAutocompleteResponseDto> response = userRepository.userAutocomplete("%" + keyword + "%");
            return entity.response(response);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/export", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity exportToCsv(@ApiIgnore @AuthenticationPrincipal Long principalId) {
        try {
            csvExportService.exportUsersToCsv(principalId);
            return entity.response(HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/admin/all", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity allForAdmin(@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam(value = "keyword", required = false) @Size(min = 1, max = 600) String keyword) {
        try {
            Page<AdminApiUserResponseDto> response = userRepository.usersForAdmin(StringUtils.hasText(keyword) ? "%" + keyword + "%" : "%", new PageRequest(page, size));
            return entity.response(response);
        } catch (Exception e) {
            return handleException(e);
        }
    }
}
