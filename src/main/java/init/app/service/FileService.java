package init.app.service;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.jcabi.aspects.Loggable;
import init.app.configuration.ConfigProperties;
import init.app.exception.CustomException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.servlet.ServletException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;

@Service
@Loggable(trim = false, prepend = true)
@Transactional(rollbackFor = Exception.class)
public class FileService {

    @Inject
    private ConfigProperties configProperties;

    private static Storage storage = null;

    static {
        storage = StorageOptions.getDefaultInstance().getService();
    }

    public String getMediaUrl(MultipartFile file) throws IOException, ServletException, CustomException {
        final String fileName = file.getOriginalFilename();
        // Check extension of file
        if (fileName == null || fileName.isEmpty() || !fileName.contains(".")) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("FILENAME_MISSING"));
        }

        final String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
        String[] allowedExt = {"jpg", "jpeg", "png", "gif"};
        for (String s : allowedExt) {
            if (extension.equals(s)) {
                return this.uploadFile(file);
            }
        }
        throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("MUST_BE_IMAGE"));
    }

    @SuppressWarnings("deprecation")
    private String uploadFile(MultipartFile file) throws IOException {

        RestTemplate restTemplate = new RestTemplate();

        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        String tempFileName = configProperties.getStoragefolder() + "/Image" + UUID.randomUUID() + ".jpeg";
        FileOutputStream fo = new FileOutputStream(tempFileName);
        fo.write(file.getBytes());
        fo.close();

        map.add("file", new FileSystemResource(tempFileName));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);

        String url = restTemplate.postForObject(configProperties.getImageHandlerUrl(), requestEntity, String.class);

        Files.delete(Paths.get(tempFileName));

        return url;
    }
}
