package init.app.domain.repository;

import init.app.domain.model.Content;
import init.app.domain.model.Poll;
import init.app.domain.model.Stored;
import init.app.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StoredRepository extends JpaRepository<Stored, Long> {

    @Query(value = "SELECT id FROM stored ORDER BY ?#{#pageable}",

            countQuery = "SELECT * FROM stored ORDER BY ?#{#pageable}",
            nativeQuery = true)
    Page<Object[]> getAll(Long principalId, Pageable pageable);


    Stored findByContentAndUser(Content content, User user);

}
