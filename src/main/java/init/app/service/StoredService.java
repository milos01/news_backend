package init.app.service;

import com.jcabi.aspects.Loggable;
import init.app.domain.model.Content;
import init.app.domain.model.Stored;
import init.app.domain.model.User;
import init.app.domain.repository.ContentRepository;
import init.app.domain.repository.StoredRepository;
import init.app.domain.repository.UserRepository;
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
public class StoredService {

    @Inject
    StoredRepository storedRepository;

    @Inject
    private ContentService contentService;

    @Inject
    private ContentRepository contentRepository;

    @Inject
    private UserService userService;

    @Inject
    private UserRepository userRepository;


    public void storeContent(Long principalId, Long contentId) throws CustomException {

        User user = userService.getByRepoMethod(userRepository.findById(principalId));

        Content content = contentService.getByRepoMethod(contentRepository.findById(contentId));

        store(content, user);
    }

    public void store(Content content, User user) throws CustomException {

        Stored stored = storedRepository.findByContentAndUser(content, user);

        if (stored == null) {
            stored = new Stored();
            stored.setContent(content);
            stored.setUser(user);
            stored.setIsDeleted(false);
            stored.setCreateTime(ZonedDateTime.now());
            stored.setUpdateTime(ZonedDateTime.now());

            storedRepository.save(stored);
        } else {
            if (stored.getIsDeleted() == false) {
                throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("CONTENT_ALREADY_STORED"));
            }

            stored.setIsDeleted(false);
            stored.setUpdateTime(ZonedDateTime.now());

            storedRepository.save(stored);
        }

    }

    public void deleteContentStore(Long principalId, Long contentId) throws CustomException {

        User user = userService.getByRepoMethod(userRepository.findById(principalId));

        Content content = contentService.getByRepoMethod(contentRepository.findById(contentId));

        delete(content, user);
    }

    public void delete(Content content, User user) throws CustomException {

        Stored stored = storedRepository.findByContentAndUser(content, user);

        if (stored == null || stored.getIsDeleted() == true) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("CONTENT_NOT_STORED"));
        } else {
            stored.setIsDeleted(true);
            stored.setUpdateTime(ZonedDateTime.now());

            storedRepository.save(stored);
        }

    }
}
