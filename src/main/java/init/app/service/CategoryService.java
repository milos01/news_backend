package init.app.service;

import com.jcabi.aspects.Loggable;
import init.app.domain.model.Category;
import init.app.domain.repository.CategoryRepository;
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
public class CategoryService {

    @Inject
    private CategoryRepository categoryRepository;

    public void create(String text) throws CustomException {

        Category category = new Category();

        category.setText(text);
        category.setCreateTime(ZonedDateTime.now());
        category.setUpdateTime(ZonedDateTime.now());
        category.setIsDeleted(false);

        categoryRepository.save(category);
    }

    public void delete(Long categoryId) throws CustomException {

        Category category = getByRepoMethod(categoryRepository.findById(categoryId));

        category.setIsDeleted(true);
        categoryRepository.save(category);
    }

    protected Category getByRepoMethod(Category category) throws CustomException {

        if (category == null) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("CATEGORY_NOT_EXIST"));
        } else if (category.getIsDeleted()) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("CATEGORY_DELETED"));
        }

        return category;
    }

}
