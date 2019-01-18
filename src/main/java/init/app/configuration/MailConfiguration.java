package init.app.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.inject.Inject;
import java.util.Properties;

@Configuration
public class MailConfiguration {

    @Inject
    private ConfigProperties configProperties;

    @Bean
    public JavaMailSender mailSender() {

        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(configProperties.getMail().getHost());
        mailSender.setPort(configProperties.getMail().getPort());
        mailSender.setUsername(configProperties.getMail().getUsername());
        mailSender.setPassword(configProperties.getMail().getPassword());

        final Properties properties = new Properties();
        properties.setProperty("mail.smtp.starttls.enable", configProperties.getMail().getStarttls());
        properties.setProperty("mail.smtp.auth", configProperties.getMail().getAuth());

        mailSender.setJavaMailProperties(properties);

        return mailSender;
    }

}