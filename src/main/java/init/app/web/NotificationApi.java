package init.app.web;

import com.jcabi.aspects.Loggable;
import init.app.component.HttpCustomEntity;
import init.app.domain.model.User;
import init.app.domain.repository.NotificationRepository;
import init.app.domain.repository.UserRepository;
import init.app.service.NotificationService;
import init.app.service.UserService;
import init.app.util.ConvertUtil;
import init.app.web.dto.custom.NotificationCounterDto;
import init.app.web.dto.parent.NotificationResponseDto;
import init.app.web.dto.shared.AllResponseDto;
import init.app.web.dto.shared.PageResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static init.app.exception.CustomExceptionHandler.handleException;
import static init.app.util.ConvertUtil.convertToPageResponse;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Loggable(trim = false, prepend = true)
@RestController
@RequestMapping("/notification")
public class NotificationApi {

    @Inject
    private HttpCustomEntity entity;
    @Inject
    private NotificationService notificationService;
    @Inject
    private NotificationRepository notificationRepository;
    @Inject
    private UserService userService;
    @Inject
    private UserRepository userRepository;

    @RequestMapping(value = "/all", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ACTIVE')")
    public ResponseEntity getAll(@ApiIgnore @AuthenticationPrincipal Long principalId, @RequestParam("limit") int limit, @RequestParam("offset") int offset) {
        try {
            List<Object[]> response = notificationRepository.getAll(principalId, limit, offset);
            AllResponseDto parsedResponse = ConvertUtil.convertToAllResponse(response, NotificationResponseDto.class);

            notificationService.setUnreadNotificationsToRead((List<NotificationResponseDto>) parsedResponse.getContent(), principalId);

            return entity.response(parsedResponse);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/{id}", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ACTIVE')")
    public ResponseEntity getNotification(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long id) {
        try {
            NotificationResponseDto response = notificationRepository.getNotification(id);

            notificationService.setUnreadNotificationsToRead(Arrays.asList(response), principalId);

            return entity.response(response);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/unread-counter", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ACTIVE')")
    public ResponseEntity getUnreadCounter(@ApiIgnore @AuthenticationPrincipal Long principalId) {
        try {
            User user = userService.getByRepoMethod(userRepository.findById(principalId));

            NotificationCounterDto notificationCounterDto = new NotificationCounterDto();
            notificationCounterDto.setUnreadCounter(notificationRepository.countAllByUserAndIsReadFalseAndIsDeletedFalse(user));

            return entity.response(notificationCounterDto);
        } catch (Exception e) {
            return handleException(e);
        }
    }

}
