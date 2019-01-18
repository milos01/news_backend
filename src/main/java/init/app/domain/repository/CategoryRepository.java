package init.app.domain.repository;

import init.app.domain.model.Ad;
import init.app.domain.model.Category;
import init.app.web.dto.response.CategoryResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(value = "SELECT new init.app.web.dto.response.CategoryResponseDto(c.text, c.id) FROM Category c WHERE c.isDeleted = FALSE",
            countQuery = "SELECT c.id FROM Category c WHERE c.isDeleted = FALSE")
    List<CategoryResponseDto> getAll();

    Category findById(Long id);
}
