package init.app.web.dto.mailchimp;

public enum MailChimpStatus {
    SUBSCRIBED("subscribed"),
    UNSUBSCRIBED("unsubscribed"),
    CLEANED("cleaned"),
    PENDING("pending");

    private final String name;

    MailChimpStatus(String value) {
        this.name = value;
    }

    public String value() {
        return this.name;
    }

    @Override
    public String toString() {
        return name;
    }
}
