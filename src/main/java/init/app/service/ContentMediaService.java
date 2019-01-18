package init.app.service;

import com.jcabi.aspects.Loggable;
import init.app.domain.model.Content;
import init.app.domain.model.ContentMedia;
import init.app.domain.model.enumeration.ContentMediaType;
import init.app.domain.repository.ContentMediaRepository;
import init.app.exception.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.ResourceBundle;

@Service
@Loggable(trim = false, prepend = true)
@Transactional(rollbackFor = Exception.class)
public class ContentMediaService {

    @Inject
    private ContentMediaRepository contentMediaRepository;

    public void countImagesForContent(Content content) throws CustomException{
        if(contentMediaRepository.countAllByContentAndIsDeletedAndType(content, false, ContentMediaType.IMAGE) == 3) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("NUMBER_IMAGES_ALLOWED"));
        }
    }

    public void create(Content content, String mediaUrl, Integer order, ContentMediaType type) throws CustomException{

        ContentMedia contentMedia = new ContentMedia();
        contentMedia.setContent(content);
        contentMedia.setUrl(mediaUrl);
        contentMedia.setType(type);
        contentMedia.setOrder(order);
        contentMedia.setCreateTime(ZonedDateTime.now());
        contentMedia.setUpdateTime(ZonedDateTime.now());
        contentMedia.setIsDeleted(false);

        contentMediaRepository.save(contentMedia);
    }

    public void delete(Long contentId, String mediaUrl) throws CustomException{

        ContentMedia contentMedia = contentMediaRepository.findByContentIdAndUrl(contentId, mediaUrl);
        if(contentMedia!=null && !contentMedia.getIsDeleted()){
            contentMedia.setIsDeleted(true);
            contentMediaRepository.save(contentMedia);
        }
    }
}
