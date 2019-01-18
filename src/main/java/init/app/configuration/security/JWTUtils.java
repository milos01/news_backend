package init.app.configuration.security;

import init.app.domain.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;import init.app.domain.model.enumeration.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;


public final class JWTUtils {

    private static final long VALIDITY_IN_SECONDS = 30 * 24 * 60 * 60;

    private static final String AUTHORITIES_KEY = "auth";

    // TODO: 1/24/18 ispraviti ovaj scret key
    public final static String SECRET_KEY = "abcbbagrgkopkoeprj123iewansnasbfb123sanfnasfn123";

    private JWTUtils() {
    }

    public static String generateToken(User user) {
        return createToken(user.getId(), user.getRole(), SECRET_KEY);
    }

    public static String generateToken(Long id, Role role) {
        return createToken(id, role, SECRET_KEY);
    }

    private static String createToken(Long userId, Role role, String secretKey) {

        final ZonedDateTime validity = ZonedDateTime.now(ZoneId.of("UTC")).plusSeconds(VALIDITY_IN_SECONDS);

        return Jwts.builder().setSubject(userId.toString()).claim(AUTHORITIES_KEY, role).signWith(SignatureAlgorithm.HS512, secretKey).setExpiration(Date.from(validity.toInstant())).compact();
    }

    public static Authentication getAuthentication(String token, String secretKey) {

        final Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        final Long userId = Long.valueOf(claims.getSubject());
        final String role = claims.get(AUTHORITIES_KEY).toString();

        return new PreAuthenticatedAuthenticationToken(userId, null, Collections.singletonList(new SimpleGrantedAuthority(role)));
    }

}
