package init.app.web;

import com.jcabi.aspects.Loggable;
import init.app.service.MailchimpService;
import init.app.web.dto.mailchimp.EmailRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;

import static init.app.exception.CustomExceptionHandler.handleException;
import static org.springframework.http.HttpStatus.CONFLICT;

/**
 * Created by bojan.stankovic@codetri.be on 7/4/18.
 */
@Loggable(trim = false, prepend = true)
@RestController
@RequestMapping("/mail-chimp")
public class MailchimpApi {

    @Inject
    MailchimpService mailchimpService;

    @RequestMapping(value = "/subscribe", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity subscribe(@RequestBody @Valid EmailRequestDTO request) {
        try {
            mailchimpService.subscribe(request);
        } catch (Exception e) {
            return handleException(e);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


}
