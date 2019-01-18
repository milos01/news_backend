package init.app.web.client;

import com.jcabi.aspects.Loggable;
import init.app.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static init.app.exception.CustomExceptionHandler.DEFAULT_EXCEPTION;

@Service
@Transactional(rollbackFor = Exception.class)
@Loggable(trim = false, prepend = true)
public class RestTemplateClient {

    @Autowired
    private RestTemplate restTemplate;

    public <T, R> T exchange(String url, HttpMethod type, Optional<R> requestBody, HttpHeaders headers, Class<T> clazz) throws CustomException {
        HttpEntity<?> request = new HttpEntity<>(requestBody.orElse(null), headers);

        ResponseEntity<T> response;
        try {
            response = restTemplate.exchange(url, type, request, clazz);
        } catch (HttpClientErrorException ex) {
            throw new CustomException(ex.getResponseBodyAsString(), ex.getStatusCode());
        } catch (HttpServerErrorException ex) {
            throw new CustomException(ex.getResponseBodyAsString(), HttpStatus.BAD_REQUEST);
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new CustomException(DEFAULT_EXCEPTION, HttpStatus.CONFLICT);
        }

        return response.getBody();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
