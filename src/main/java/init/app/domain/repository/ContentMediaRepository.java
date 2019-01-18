package init.app.domain.repository;

import init.app.domain.model.Content;
import init.app.domain.model.ContentMedia;
import init.app.domain.model.enumeration.ContentMediaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentMediaRepository extends JpaRepository<ContentMedia, Long> {

    Long countAllByContentAndIsDeletedAndType(Content content, Boolean isDeleted, ContentMediaType type);

    ContentMedia findByContentIdAndUrl(Long contentId, String url);
}
