package init.app.web;

import com.jcabi.aspects.Loggable;
import init.app.component.HttpCustomEntity;
import init.app.domain.model.enumeration.AbuseReportReason;
import init.app.domain.model.enumeration.AbuseReportStatus;
import init.app.domain.model.enumeration.AbuseReportType;
import init.app.domain.repository.AbuseReportRepository;
import init.app.service.AbuseReportService;
import init.app.web.dto.request.AbuseReportRequestDto;
import init.app.web.dto.response.AbuseReportResponseDto;
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

import static init.app.exception.CustomExceptionHandler.handleException;
import static init.app.util.ConvertUtil.convertToPageResponse;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Loggable(trim = false, prepend = true)
@RestController
@RequestMapping("/abuse-report")
public class AbuseReportApi {

    @Inject
    private HttpCustomEntity entity;
    @Inject
    private AbuseReportService abuseReportService;
    @Inject
    private AbuseReportRepository abuseReportRepository;

    @RequestMapping(value = "/", method = POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ACTIVE') or hasAuthority('ADMIN')")
    public ResponseEntity create(@ApiIgnore @AuthenticationPrincipal Long principalId, @Valid @RequestBody AbuseReportRequestDto request) {
        try {
            abuseReportService.create(request.getCommentId(), request.getContentId(), principalId, request.getType(), request.getAbuseReportReason(), request.getFeedback());
            return entity.responseCreated();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/{id}/confirm", method = POST)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity confirm(@PathVariable Long id) {
        try {
            abuseReportService.confirm(id);
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity deny(@PathVariable Long id) {
        try {
            abuseReportService.deny(id);
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/all", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity getAbuseReports(@RequestParam(value = "type", required = false) AbuseReportType type, @RequestParam(value = "status", required = false) AbuseReportStatus status, @RequestParam(value = "reason", required = false) AbuseReportReason reason, @RequestParam("page") int page, @RequestParam("size") int size) {
        try {
            Page<Object[]> response = abuseReportRepository.getAll(type == null ? null : type.name(), status == null ? null : status.name(), reason == null ? null : reason.name(), new PageRequest(page, size));
            return entity.response(convertToPageResponse(response, AbuseReportResponseDto.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }


}
