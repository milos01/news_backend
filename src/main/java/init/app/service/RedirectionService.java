package init.app.service;

import com.jcabi.aspects.Loggable;
import init.app.configuration.ConfigProperties;
import init.app.domain.model.Content;
import init.app.domain.model.Tag;
import init.app.domain.model.enumeration.ContentType;
import init.app.domain.old_mapper.OldArticleMapper;
import init.app.domain.old_mapper.OldTagMapper;
import init.app.domain.old_model.OldArticle;
import init.app.domain.old_model.OldTag;
import init.app.domain.repository.ContentRepository;
import init.app.domain.repository.TagRepository;
import init.app.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

@Service
@Loggable(trim = false, prepend = true)
@Transactional(rollbackFor = Exception.class)
public class RedirectionService {

    @Autowired
    @Qualifier("oldJdbcTemplate")
    private JdbcTemplate mysqlTemplate;
    @Inject
    private ConfigProperties configProperties;
    @Inject
    private TagRepository tagRepository;
    @Inject
    private ContentRepository contentRepository;

    //https://sevenbilliontoday.com/#tag/61e54172-7283-4ab9-8320-47d1c87fe0b0 OLD TAG PAGE
    //https://sevenbilliontoday.com/#!article/fa6a7e7c-62b2-4f8a-828f-cc2b0b6fc78c OLD ARTICLE
    //https://sevenbilliontoday.com/#registration/signup OLD REGISTRATION
    //https://sevenbilliontoday.com/#search/human%20rights OLD SEARCH PAGE

    public String getNewUrl(String oldUrl) throws CustomException {

        String redirectionPart = oldUrl.replace(configProperties.getFrontendbaseurl().concat("#"), "");

        String newUrl = configProperties.getFrontendbaseurl();

        if (redirectionPart.startsWith("tag")) {

            redirectionPart = redirectionPart.substring(4);
            Tag tag = getTagFromUUID(redirectionPart);
            newUrl = newUrl.concat("tag/").concat(tag != null ? tag.getId().toString() : "").concat("?active=news");

        } else if (redirectionPart.startsWith("!article")) {

            redirectionPart = redirectionPart.substring(9);
            Content content = getContentFromUUID(redirectionPart);
            newUrl = newUrl.concat("article/").concat(content != null ? (content.getId().toString().concat("/").concat(content.getHeadline().replace(' ', '-'))) : "");

        } else if (redirectionPart.startsWith("registration")) {

            newUrl = configProperties.getFrontendbaseurl().concat("signup");

        } else if (redirectionPart.startsWith("search")) {

            newUrl = configProperties.getFrontendbaseurl().concat("search/") + redirectionPart.substring(7);
        }

        return newUrl;
    }

    private Tag getTagFromUUID(String uuid) {

        String sql = "SELECT * FROM sbdb.Tag WHERE TagID = UNHEX('" + uuid.replace("-", "") + "')";
        List<OldTag> oldTags = mysqlTemplate.query(sql, new OldTagMapper());
        if (oldTags.isEmpty()) {
            return null;
        }
        OldTag tag = oldTags.get(0);
        return tagRepository.findFirstByText(tag.getText());
    }

    private Content getContentFromUUID(String uuid) {

        String sql = "SELECT * FROM sbdb.Article WHERE ArticleID = UNHEX('" + uuid.replace("-", "") + "')";
        List<OldArticle> oldArticles = mysqlTemplate.query(sql, new OldArticleMapper());
        if (oldArticles.isEmpty()) {
            return null;
        }
        return contentRepository.findFirstByHeadlineAndIsDeletedFalseAndTypeIn(oldArticles.get(0).getHeadline(), Arrays.asList(ContentType.ARTICLE_REGULAR, ContentType.ARTICLE_SPONSORED));
    }

}
