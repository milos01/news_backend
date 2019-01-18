package init.app.service;

import com.jcabi.aspects.Loggable;
import init.app.domain.model.EntityTag;
import init.app.domain.model.Tag;
import init.app.domain.model.enumeration.TagType;
import init.app.domain.repository.EntityTagRepository;
import init.app.domain.repository.TagRepository;
import init.app.exception.CustomException;
import init.app.web.dto.parent.IdDto;
import init.app.web.dto.shared.GenericResponseDto;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Service
@Loggable(trim = false, prepend = true)
@Transactional(rollbackFor = Exception.class)
public class TagService {

    @Inject
    private TagRepository tagRepository;
    @Inject
    private EntityTagRepository entityTagRepository;

    public GenericResponseDto create(String tagText) throws CustomException{

        Tag existingTag = tagRepository.findByText(tagText);

        if(existingTag != null) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("TAG_ALREADY_EXISTS"));
        }

        Tag tag = new Tag();
        tag.setText(tagText);
        tag.setIsDeleted(false);
        tag.setRFollowers(0);
        tag.setTempActivity(0);
        tag.setTotalActivity(0);
        tag.setType(TagType.REGULAR);
        tag.setUpdateTime(ZonedDateTime.now());
        tag.setCreateTime(ZonedDateTime.now());

        tagRepository.save(tag);

        return new GenericResponseDto(new IdDto(tag.getId()));
    }

    public void delete(Long tagId) throws CustomException{

        Tag tag = getByRepoMethod(tagRepository.findById(tagId));

        deleteTag(tag);
    }

    public Tag getByRepoMethod(Tag tag) throws CustomException {

        if (tag == null) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("TAG_NOT_EXIST"));
        } else if (tag.getIsDeleted()) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("TAG_DELETED"));
        }

        return tag;
    }

    public void deleteTag(Tag tag) {
        List<EntityTag> entityTagList = entityTagRepository.findAllByTagAndIsDeletedFalse(tag);

        for (EntityTag entityTag : entityTagList) {
            entityTag.setIsDeleted(true);
            entityTag.setUpdateTime(ZonedDateTime.now());

            entityTagRepository.save(entityTag);
        }

        tag.setIsDeleted(true);
        tag.setUpdateTime(ZonedDateTime.now());

        tagRepository.save(tag);
    }

    public void updateTagTempActivity(Tag tag) throws CustomException{
        tag.setTempActivity(tag.getTempActivity()+1);
        tagRepository.save(tag);
    }

    @Async
    public void updateTagsTempActivity(List<Tag> tags) throws CustomException{
        for (Tag tag : tags) {
            updateTagTempActivity(tag);
        }
    }

    void updateTagsTotalActivity() throws CustomException{
        List<Tag> tags = tagRepository.getAllByIsDeletedFalseOrderByTempActivityDesc();

        Integer numOfTrendingTags = 0;
        for (Tag tag : tags) {
            updateTagTotalActivity(tag, numOfTrendingTags<=50);
            numOfTrendingTags++;
        }
    }

    void updateTagTotalActivity(Tag tag, Boolean ifTrending) throws CustomException{
        tag.setTotalActivity(tag.getTotalActivity()+tag.getTempActivity());
        tag.setTempActivity(0);
        tag.setType(ifTrending?TagType.TRENDING:TagType.REGULAR);
        tag.setUpdateTime(ZonedDateTime.now());
        tagRepository.save(tag);
    }
}
