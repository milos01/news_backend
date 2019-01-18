package init.app.domain.repository;

import init.app.domain.model.Follow;
import init.app.domain.model.FooterPage;
import init.app.domain.model.enumeration.FooterPageType;
import init.app.web.dto.response.FooterPageResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FooterPageRepository extends JpaRepository<FooterPage, Long> {

    @Query(value = "SELECT id FROM footer_page",
            nativeQuery = true)
    Object[] getById(Long id);

    @Query(value = "SELECT id FROM footer_page",
            nativeQuery = true)
    List<Object[]> getAll(FooterPageType type);

    FooterPage findById(Long id);

    @Query(value = "SELECT new init.app.web.dto.response.FooterPageResponseDto(f.id, f.type, f.content.id, f.content.headline, f.content.text, f.content.rMediaUrl, f.order, f.createTime) FROM FooterPage f WHERE f.id = :id")
    FooterPageResponseDto fetchFooterPageById(@Param("id") Long id);

    @Query(value = "SELECT new init.app.web.dto.response.FooterPageResponseDto(f.id, f.type, f.content.id, f.content.headline, f.content.text, f.content.rMediaUrl, f.order, f.createTime) FROM FooterPage f WHERE f.isDeleted = false and f.type = :typeParam ORDER BY f.order")
    List<FooterPageResponseDto> fetchAllFooterPages(@Param("typeParam") FooterPageType typeParam);
}
