package init.app.service;

import com.jcabi.aspects.Loggable;
import init.app.domain.model.Content;
import init.app.domain.model.FooterPage;
import init.app.domain.model.User;
import init.app.domain.model.enumeration.ContentType;
import init.app.domain.model.enumeration.FooterPageType;
import init.app.domain.repository.ContentRepository;
import init.app.domain.repository.FooterPageRepository;
import init.app.domain.repository.UserRepository;
import init.app.exception.CustomException;
import init.app.web.dto.request.ContentRequestDto;
import init.app.web.dto.response.FooterPageCreateResponseDto;
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
public class FooterPageService {

    @Inject
    ContentService contentService;
    @Inject
    ContentRepository contentRepository;
    @Inject
    UserService userService;
    @Inject
    UserRepository userRepository;
    @Inject
    FooterPageRepository footerPageRepository;

    public FooterPageCreateResponseDto create(Long principalId, FooterPageType type, Integer orderNumber, ContentRequestDto content) throws CustomException{

        User user = userService.getByRepoMethod(userRepository.findById(principalId));

        Content contentSaved = contentService.createUpdateContent(null, user, ContentType.FOOTER, content.getHeadline(), content.getText(), null);

        FooterPage footerPage = new FooterPage();
        footerPage.setContent(contentSaved);
        footerPage.setOrder(orderNumber);
        footerPage.setType(type);
        footerPage.setCreateTime(ZonedDateTime.now());
        footerPage.setUpdateTime(ZonedDateTime.now());
        footerPage.setIsDeleted(false);

        footerPageRepository.save(footerPage);

        FooterPageCreateResponseDto footerPageCreateResponseDto = new FooterPageCreateResponseDto();
        footerPageCreateResponseDto.setContentId(contentSaved.getId());
        footerPageCreateResponseDto.setFooterPageId(footerPage.getId());

        return footerPageCreateResponseDto;
    }

    public void update(Long principalId, Long footerPageId, FooterPageType type, Integer orderNumber, ContentRequestDto content) throws CustomException {
        FooterPage footerPage = getByRepoMethod(footerPageRepository.findById(footerPageId));

        footerPage.setOrder(orderNumber);
        footerPage.setType(type);
        footerPage.setUpdateTime(ZonedDateTime.now());

        footerPageRepository.save(footerPage);

        contentService.update(footerPage.getContent().getId(), principalId, content.getHeadline(), content.getText(), null, true);
    }

    public void delete(Long footerPageId) throws CustomException{

        FooterPage footerPage = getByRepoMethod(footerPageRepository.findById(footerPageId));

        footerPage.setIsDeleted(true);
        footerPage.setUpdateTime(ZonedDateTime.now());

        footerPageRepository.save(footerPage);

        Content content = footerPage.getContent();
        content.setIsDeleted(true);
        content.setUpdateTime(ZonedDateTime.now());

        contentRepository.save(content);
    }

    public FooterPage getByRepoMethod(FooterPage footerPage) throws CustomException {
        if (footerPage == null) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("FOOTER_PAGE_NOT_EXIST"));
        } else if (footerPage.getIsDeleted()) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("FOOTER_PAGE_DELETED"));
        }

        return footerPage;
    }

    public void order(List<Long> idList) throws CustomException {

        Integer orderNumber = 1;

        for (Long footerPageId : idList) {
            FooterPage footerPage = getByRepoMethod(footerPageRepository.findById(footerPageId));
            footerPage.setOrder(orderNumber++);
            footerPage.setUpdateTime(ZonedDateTime.now());

            footerPageRepository.save(footerPage);
        }

    }
}
