package init.app.util;

import org.springframework.http.HttpHeaders;

import java.util.Base64;

/**
 * Created by bojan.stankovic@codetri.be on 6/29/17.
 */
public class RequestBuilderUtil {

    public static HttpHeaders buildHeaderWithBasicAuthenticationAndJsonContentType(String username, String apiKey) {
        final String base64 = new String(Base64.getEncoder().encode((username + ":" + apiKey).getBytes()));

        HttpHeaders header = new HttpHeaders();
        header.add("Authorization", "Basic " + base64);
        header.add("Content-Type", "application/json");
        return header;
    }
}
