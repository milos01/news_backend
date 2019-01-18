package init.app.domain.repository;

import init.app.domain.model.Content;
import init.app.domain.model.EntityTag;
import init.app.domain.model.Tag;
import init.app.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntityTagRepository extends JpaRepository<EntityTag, Long> {

    List<EntityTag> findAllByTagAndIsDeletedFalse(Tag tag);

    List<EntityTag> findAllByUser(User user);

    List<EntityTag> findAllByContent(Content content);

    List<EntityTag> findAllByContentAndIsDeletedFalse(Content content);

    List<EntityTag> findAllByUserAndIsDeletedFalse(User user);

}
