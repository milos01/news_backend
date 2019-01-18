package init.app.web.client;

import init.app.configuration.ConfigProperties;
import init.app.exception.CustomException;
import init.app.util.RequestBuilderUtil;
import init.app.web.dto.mailchimp.MailChimpSubscribeRequestDTO;
import init.app.web.dto.mailchimp.MailChimpSubscribeResponseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by bojan.stankovic@codetri.be on 7/3/18.
 */
@Component
public class MailchimpClient {

    @Inject
    private RestTemplateClient restTemplateClient;

    @Inject
    private ConfigProperties configProperties;

    public void subscribe(String listId, MailChimpSubscribeRequestDTO request) throws CustomException {
        HttpHeaders httpHeaders = RequestBuilderUtil.buildHeaderWithBasicAuthenticationAndJsonContentType(
                configProperties.getMailchimp().getUsername(),
                configProperties.getMailchimp().getApikey());

        try {
            restTemplateClient.exchange(
                    configProperties.getMailchimp().getUrl() + "/lists/" + listId + "/members",
                    HttpMethod.POST, Optional.ofNullable(request), httpHeaders, MailChimpSubscribeResponseDTO.class);
        } catch (CustomException e) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("ALREADY_SUBSCRIBED_TO_MAILCHIMP"), HttpStatus.CONFLICT);
        }
    }

}
