package init.app.domain.repository;

import init.app.domain.model.Ad;
import init.app.domain.model.enumeration.AdType;
import init.app.web.dto.parent.IdDto;
import init.app.web.dto.response.AdResponseDto;
import init.app.web.dto.response.CategoryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdRepository extends JpaRepository<Ad, Long> {

    @Query(value = "SELECT new init.app.web.dto.response.AdResponseDto(ad.id, ad.href, ad.imageUrl, ad.type, ad.createTime) FROM Ad ad WHERE ad.id = :id")
    Optional<AdResponseDto> getById(@Param("id") Long id);

    @Query(value = "SELECT id, href, image_url, type, create_time FROM ad WHERE IF(:adType IS NOT NULL, type = :adType, id = id) AND IF(:isDeleted IS NOT NULL, is_deleted = :isDeleted, id = id) ORDER BY create_time, ?#{#pageable}",
            countQuery = "SELECT * FROM ad a WHERE IF(:adType IS NOT NULL, a.type = :adType, a.id = a.id) AND IF(:isDeleted IS NOT NULL, a.is_deleted = :isDeleted, a.id = a.id) ORDER BY ?#{#pageable}"
    , nativeQuery = true)
    Page<Object[]> getAll(@Param("adType") AdType type, @Param("isDeleted") Boolean isDeleted, Pageable pageable);

    @Query(value = "SELECT new init.app.web.dto.response.AdResponseDto(ad.id, ad.href, ad.imageUrl, ad.type, ad.createTime) FROM Ad ad WHERE ad.type = :adType AND ad.isDeleted = FALSE ORDER BY temp_activity ASC")
    Page<AdResponseDto> getAll(@Param("adType") AdType type, Pageable pageable);

    @Query(value = "SELECT a FROM Ad a WHERE a.type = :adType AND a.isDeleted = FALSE")
    List<Ad> getAllByType(@Param("adType") AdType adType);
}
