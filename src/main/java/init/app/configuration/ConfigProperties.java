package init.app.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "custom", ignoreUnknownFields = false)
public class ConfigProperties {

    private Mail mail;
    private Twitter twitter;
    private String url;
    private String cookiedomain;
    private String storagefolder;
    private String imageHandlerUrl;
    private String baseurl;
    private String frontendbaseurl;
    private String servedimagedomain;
    private String frontendcreateprofile;
    private String frontendcreateprofilewithcode;
    private String frontendchangepasswordwithcode;
    private String frontendsignin;
    private String videoplaceholderimage;
    private String welcomevideourl;
    private String placeholderimage;
    private Bucket bucket;
    private Facebook facebook;
    private Mailchimp mailchimp;

    @Data
    public static class Twitter {
        private String key;
        private String secret;
        private String redirecturl;
        private String profileName;
    }

    @Data
    public static class Facebook {
        private String appId;
    }

    @Data
    public static class Mailchimp {
        private String url;
        private String listid;
        private String username;
        private String apikey;
    }

    @Data
    public static class Mail {

        private String from;
        private String host;
        private int port;
        private String username;
        private String password;
        private String starttls;
        private String auth;
    }

    @Data
    public static class Bucket {

        private String name;
        private String accessKey;
        private String secret;
    }
}
