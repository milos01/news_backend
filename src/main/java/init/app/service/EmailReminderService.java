package init.app.service;

import com.google.common.collect.Lists;
import init.app.configuration.ConfigProperties;
import init.app.domain.model.User;
import init.app.domain.model.enumeration.LinkType;
import init.app.domain.model.enumeration.Role;
import init.app.domain.repository.UserRepository;
import init.app.exception.CustomException;
import init.app.web.dto.custom.EmailReminderDto;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

/**
 * Created by bojan.stankovic@codetri.be on 7/5/18.
 */
@Service
public class EmailReminderService {

    @Inject
    UserRepository userRepository;

    @Inject
    AuthenticationService authenticationService;

    @Inject
    MailService mailService;

    @Inject
    TaskSchedulerService taskSchedulerService;

    @Inject
    ConfigProperties configProperties;

    public void sendReminderToAllUnconfirmedUser() throws InterruptedException, CustomException {

        List<User> uncofirmedUsers = userRepository.findAllByRole(Role.UNCONFIRMED);

        List<EmailReminderDto> emailReminderDtoList = new ArrayList<>();

        for (User uncofirmedUser : uncofirmedUsers) {
            EmailReminderDto emailReminderDto = new EmailReminderDto();
            emailReminderDto.setRecipientEmail(uncofirmedUser.getEmail());
            emailReminderDto.setUsername(uncofirmedUser.getUsername());
            emailReminderDto.setUrl(authenticationService.createLink(uncofirmedUser, LinkType.CREATE_PROFILE));

            String textPlain = ResourceBundle.getBundle("i18n.mail", Locale.ENGLISH).getString("EMAIL_REMINDER_TEXT").replace("INSERT_LINK_HERE", emailReminderDto.getUrl()).replace("BASE_URL", configProperties.getFrontendbaseurl());
            String textHtml = ResourceBundle.getBundle("i18n.mail", Locale.ENGLISH).getString("EMAIL_REMINDER_HTML").replace("INSERT_LINK_HERE", emailReminderDto.getUrl()).replace("BASE_URL", configProperties.getFrontendbaseurl());


            emailReminderDto.setHtmlContent(textHtml);
            emailReminderDto.setPlanTextContent(textPlain);

            emailReminderDtoList.add(emailReminderDto);
        }

        for (List<EmailReminderDto> emailReminderDtoListPartition : Lists.partition(emailReminderDtoList, 100)) {

            sendRemindersToPartition(emailReminderDtoListPartition);

            TimeUnit.MINUTES.sleep(10);
        }

    }

    public void sendRemindersToPartition(List<EmailReminderDto> reminderDtos) {

        String subject = ResourceBundle.getBundle("i18n.mail", Locale.ENGLISH).getString("EMAIL_REMINDER_SUBJECT");
        for (EmailReminderDto reminderDto : reminderDtos) {
            mailService.send(reminderDto.getRecipientEmail(), subject, reminderDto.getPlanTextContent(), reminderDto.getHtmlContent());
            //System.out.println("Email sent to: email - " + reminderDto.getRecipientEmail() + ", username - " + reminderDto.getUsername());
        }

    }

}
