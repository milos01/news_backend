package init.app.service;

import init.app.configuration.ConfigProperties;
import init.app.exception.CustomException;
import init.app.web.client.MailchimpClient;
import init.app.web.dto.mailchimp.EmailRequestDTO;
import init.app.web.dto.mailchimp.MailChimpStatus;
import init.app.web.dto.mailchimp.MailChimpSubscribeRequestDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * Created by bojan.stankovic@codetri.be on 7/3/18.
 */
@Service
public class MailchimpService {

    @Inject
    private MailchimpClient mailchimpClient;

    @Inject
    private ConfigProperties configProperties;

    public void subscribe(EmailRequestDTO request) throws CustomException {
        MailChimpSubscribeRequestDTO payload = buildMailChimpSubscribeRequestDTO(request.getEmail());
        mailchimpClient.subscribe(configProperties.getMailchimp().getListid(), payload);
    }

    private MailChimpSubscribeRequestDTO buildMailChimpSubscribeRequestDTO(String email) {
        return MailChimpSubscribeRequestDTO.builder()
                .emailAddress(email)
                .status(MailChimpStatus.SUBSCRIBED.value())
                .build();
    }
}
