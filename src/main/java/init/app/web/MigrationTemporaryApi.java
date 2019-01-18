package init.app.web;

import init.app.component.HttpCustomEntity;
import init.app.service.EmailReminderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

import static init.app.exception.CustomExceptionHandler.handleException;

@RestController
@RequestMapping("/migration")
public class MigrationTemporaryApi {

    @Inject
    private HttpCustomEntity entity;
    @Inject
    EmailReminderService emailReminderService;

    @RequestMapping(value = "/send-reminders", method = RequestMethod.POST)
    public ResponseEntity sendReminders() {
        try {
            emailReminderService.sendReminderToAllUnconfirmedUser();
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }
}
