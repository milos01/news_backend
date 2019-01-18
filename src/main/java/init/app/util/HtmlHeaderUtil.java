package init.app.util;

import init.app.configuration.ConfigProperties;
import init.app.domain.model.Content;
import init.app.domain.model.enumeration.ContentType;
import org.jsoup.Jsoup;
import org.springframework.util.StringUtils;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public class HtmlHeaderUtil {
    // @formatter:off
    private static final String template =
            "<!DOCTYPE html>" +
                    "<html itemscope lang=\"en\" itemtype=\"http://schema.org/Article\"> " +
                    "<head> " +
                        "<meta itemprop=\"name\" content=\"CONTENT_HEADLINE\"> " +
                        "<meta itemprop=\"description\" content=\"CONTENT_TEXT\"> " +
                        "<meta itemprop=\"image\" content=\"MEDIA_URL\"> " +
                        "<meta name=\"twitter:card\" content=\"summary_large_image\"> " +
                        "<meta name=\"twitter:site\" content=\"@TWITTER_PROFILE\"> " +
                        "<meta name=\"twitter:title\" content=\"CONTENT_HEADLINE\"> " +
                        "<meta name=\"twitter:description\" content=\"CONTENT_TEXT\"> " +
                        "<meta name=\"twitter:creator\" content=\"@TWITTER_PROFILE\"> " +
                        "<meta name=\"twitter:image:src\" content=\"MEDIA_URL\"> " +
                        "<meta property=\"og:title\" content=\"CONTENT_HEADLINE\"/> " +
                        "<meta property=\"og:type\" content=\"article\"/> " +
                        "<meta property=\"og:url\" content=\"CONTENT_URL\"/> " +
                        "<meta property=\"og:image\" content=\"MEDIA_URL\"/> " +
                        "<meta property=\"og:description\" content=\"CONTENT_TEXT\"/> " +
                        "<meta property=\"og:site_name\" content=\"SevenBillionToday\"/> " +
                        "<meta property=\"article:published_time\" content=\"CONTENT_CREATE_TIME\"/> " +
                        "<meta property=\"article:modified_time\" content=\"CONTENT_UPDATE_TIME\"/> " +
                        "<meta property=\"article:tag\" content=\"CONTENT_TAGS\"/> " +
                        "<meta property=\"fb:admins\" content=\"FACEBOOK_APP_ID\"/> " +
                        "<meta property=\"og:image:width\" content=\"600\"/> " +
                        "<meta property=\"og:image:height\" content=\"315\"/> " +
                    "<title>SevenBillionToday</title> " +
                    "</head> " +
                    "</html>";
    // @formatter:on
    private static final String empty = "";

    private static final String headlineContainer = "CONTENT_HEADLINE";
    private static final String textContainer = "CONTENT_TEXT";
    private static final String mediaContainer = "MEDIA_URL";
    private static final String tagsContainer = "CONTENT_TAGS";
    private static final String createTimeContainer = "CONTENT_CREATE_TIME";
    private static final String updateTimeContainer = "CONTENT_UPDATE_TIME";
    private static final String urlContainer = "CONTENT_URL";

    private static final String twitterContainer = "TWITTER_PROFILE";
    private static final String facebookContainer = "FACEBOOK_APP_ID";

    public static String createUpdateHtmlHeader(ConfigProperties configProperties, Content content) {

        ContentType type = content.getType();
        Optional<Long> contentId = Optional.ofNullable(content.getId());

        Optional<String> headline = Optional.of("");
        Optional<String> text = Optional.of("");

        if (content.getType() == ContentType.POST) {
            if (content.getContent() != null) {

                Content repostedContent = content.getContent();

                if (StringUtils.hasText(content.getText())) {
                    headline = Optional.of(content.getText());

                    if (StringUtils.hasText(repostedContent.getHeadline()) && StringUtils.hasText(repostedContent.getText())) {
                        text = Optional.of(repostedContent.getHeadline() + ": " + repostedContent.getText());
                    } else if (StringUtils.hasText(repostedContent.getHeadline())) {
                        text = Optional.of(repostedContent.getHeadline());
                    } else if (StringUtils.hasText(repostedContent.getText())) {
                        text = Optional.of(repostedContent.getText());
                    }

                } else {
                    if (StringUtils.hasText(repostedContent.getHeadline()) && StringUtils.hasText(repostedContent.getText())) {
                        headline = Optional.of(repostedContent.getHeadline());
                        text = Optional.of(repostedContent.getText());
                    } else if (StringUtils.hasText(repostedContent.getHeadline())) {
                        headline = Optional.of(repostedContent.getHeadline());
                    } else if (StringUtils.hasText(repostedContent.getText())) {
                        headline = Optional.of(repostedContent.getText());
                    }
                }

                content = content.getContent();
            } else {
                headline = Optional.of(StringUtils.hasText(content.getHeadline()) ? content.getHeadline() : "");
                text = Optional.of(StringUtils.hasText(content.getText()) ? content.getText() : "");
            }
        } else {
            headline = Optional.ofNullable(content.getHeadline());
            text = Optional.ofNullable(content.getText());
        }

        Optional<String> mediaUrl = Optional.empty();

        if (StringUtils.hasText(content.getRMediaUrl())) {
            String[] mediaUrls = content.getRMediaUrl().split("\\|");
            if (mediaUrls.length == 1 && content.getRHasVideo()) {
                mediaUrl = Optional.of(configProperties.getVideoplaceholderimage());
            } else {
                for (String url : mediaUrls) {
                    if (url.contains(configProperties.getServedimagedomain())) {
                        mediaUrl = Optional.of(url);
                        break;
                    }
                }
            }
        } else {
            mediaUrl = Optional.of(configProperties.getPlaceholderimage());
        }

        Optional<String> tags = Optional.ofNullable(content.getRTags() != null ? content.getRTags().replace("|", " ") : null);
        Optional<ZonedDateTime> createTime = Optional.ofNullable(content.getCreateTime());
        Optional<ZonedDateTime> updateTime = Optional.ofNullable(content.getUpdateTime());
        String header = template;

        switch (type) {
            case FOOTER:
                header = header.replace(urlContainer, empty);
                break;
            case POST:
                header = header.replace(urlContainer, contentId.map(aLong -> (configProperties.getFrontendbaseurl() + "/post/" + aLong.toString())).orElse(empty));
                break;
            default:
                header = header.replace(urlContainer, contentId.isPresent() && headline.isPresent() ? (configProperties.getFrontendbaseurl() + "/article/" + contentId.get().toString() + "/" + headline.get().replaceAll("[\\. ,:-]+", "-")) : empty);
                break;
        }

        header = header.replace(headlineContainer, headline.orElse(empty));
        header = header.replace(textContainer, text.isPresent() ? Jsoup.parse(text.get()).text() : empty);
        header = header.replace(mediaContainer, mediaUrl.orElse(empty));
        header = header.replace(tagsContainer, tags.orElse(empty));
        //todo check format of datetime
        header = header.replace(createTimeContainer, createTime.map(zonedDateTime -> "" + zonedDateTime.withZoneSameInstant(ZoneOffset.of("Z"))).orElse(empty));
        header = header.replace(updateTimeContainer, updateTime.map(zonedDateTime -> "" + zonedDateTime.withZoneSameInstant(ZoneOffset.of("Z"))).orElse(empty));

        header = header.replace(twitterContainer, configProperties.getTwitter().getProfileName());
        header = header.replace(facebookContainer, configProperties.getFacebook().getAppId());

        return header;
    }
}
