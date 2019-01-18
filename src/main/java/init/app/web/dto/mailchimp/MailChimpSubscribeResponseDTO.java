package init.app.web.dto.mailchimp;

import lombok.Data;

import java.util.List;

@Data
public class MailChimpSubscribeResponseDTO {

    private List<Link> _links;
    private String list_id;
    private Location location;
    private String email_client;
    private boolean vip;
    private String language;
    private String last_changed;
    private int member_rating;
    private String timestamp_opt;
    private String ip_opt;
    private String timestamp_signup;
    private String ip_signup;
    private Stats stats;
    private Merge_fields merge_fields;
    private String status;
    private String email_type;
    private String unique_email_id;
    private String email_address;
    private String id;

    @Data
    private static class Link {
        private String schema;
        private String targetSchema;
        private String method;
        private String href;
        private String rel;
    }

    @Data
    private static class Location {
        private String timezone;
        private String country_code;
        private int dstoff;
        private int gmtoff;
        private int longitude;
        private int latitude;
    }

    @Data
    private static class Stats {
        private int avg_click_rate;
        private int avg_open_rate;
    }

    @Data
    private static class Merge_fields {
        private String ADDRESS;
        private String COMPANY;
        private String LNAME;
        private String FNAME;
    }
}
