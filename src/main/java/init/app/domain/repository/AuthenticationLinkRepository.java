package init.app.domain.repository;

import init.app.domain.model.AuthenticationLink;
import init.app.domain.model.Category;
import init.app.domain.model.User;
import init.app.domain.model.enumeration.LinkType;
import init.app.web.dto.response.UserVerifyLinkResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuthenticationLinkRepository extends JpaRepository<AuthenticationLink, Long> {
    AuthenticationLink findByUrlIsAndTypeIsAndExpiryTimeIsAfter(String url, LinkType type, ZonedDateTime expiryTime);

    List<AuthenticationLink> findAllByUserAndExpiryTimeAfter(User user, ZonedDateTime expiryTime);

}
