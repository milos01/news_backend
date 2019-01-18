package init.app.service;

import com.jcabi.aspects.Loggable;
import init.app.configuration.ConfigProperties;
import init.app.exception.CustomException;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.CharEncoding;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.mail.internet.MimeMessage;

@Service
@Loggable(trim = false, prepend = true)
@Transactional(rollbackFor = Exception.class)
@Log
public class MailService {

    @Inject
    private JavaMailSender mailSender;
    @Inject
    private ConfigProperties configProperties;

    @Async
    public void send(String to, String subject, String plainTextContent, String htmlContent) {
        final MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, CharEncoding.UTF_8);
            message.setTo(to);
            message.setFrom(configProperties.getMail().getFrom());
            message.setReplyTo(configProperties.getMail().getFrom());
            message.setSubject(subject);
            message.setText(plainTextContent, htmlContent);
            mailSender.send(mimeMessage);
        } catch (Throwable e) {
            log.info("Email sending failed with message: " + e.getMessage());
        }
        log.info("FINISH sendPlainTextAndHtmlEmail()");
    }

    @Async
    public void sendCSVAttachment(String to, String subject, String plainTextContent, String htmlContent, byte[] attachmentData, String attachmentName) {
        log.info("START sendCSVAttachment()");
        final MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, CharEncoding.UTF_8);
            message.setTo(to);
            message.setFrom(configProperties.getMail().getFrom());
            message.setReplyTo(configProperties.getMail().getFrom());
            message.setSubject(subject);
            message.setText(plainTextContent, htmlContent);
            if (attachmentData != null) {
                message.addAttachment(attachmentName + ".csv", new ByteArrayResource(attachmentData));
            }

            mailSender.send(mimeMessage);
        } catch (Throwable e) {
            log.info("Email sending failed with message: " + e.getMessage());
        }
        log.info("FINISH sendCSVAttachment()");
    }

}
