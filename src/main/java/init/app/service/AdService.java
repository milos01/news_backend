package init.app.service;

import com.jcabi.aspects.Loggable;
import init.app.domain.model.Ad;
import init.app.domain.model.enumeration.AdType;
import init.app.domain.repository.AdRepository;
import init.app.exception.CustomException;
import init.app.web.dto.parent.IdDto;
import init.app.web.dto.response.AdResponseDto;
import init.app.web.dto.shared.GenericResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.servlet.ServletException;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Service
@Loggable(trim = false, prepend = true)
@Transactional(rollbackFor = Exception.class)
public class AdService {

    @Inject
    AdRepository adRepository;
    @Inject
    FileService fileService;

    public GenericResponseDto create(AdType type, String href) throws CustomException {

        Ad ad = new Ad();
        ad.setHref(href);
        ad.setType(type);
        ad.setCreateTime(ZonedDateTime.now());
        ad.setUpdateTime(ZonedDateTime.now());
        ad.setIsDeleted(false);
        ad.setTempActivity(0);
        ad.setTotalActivity(0);
        ad.setImageUrl("");

        adRepository.save(ad);

        updateAdsTotalActivity(type);

        return new GenericResponseDto(new IdDto(ad.getId()));
    }

    public void delete(Long adId) throws CustomException{
        Ad ad = getByRepoMethod(adRepository.findOne(adId));
        ad.setIsDeleted(true);
        ad.setUpdateTime(ZonedDateTime.now());

        adRepository.save(ad);
    }

    public void uploadImageForAd(Long adId, MultipartFile file) throws CustomException {

        Ad ad = getByRepoMethod(adRepository.findOne(adId));

        String key;
        try {
            key = fileService.getMediaUrl(file);
        } catch (IOException |ServletException s) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("BUCKET_UPLOAD"));
        } catch (Exception ex) {
            throw new CustomException(ex.getMessage());
        }

        ad.setImageUrl(key);
        ad.setUpdateTime(ZonedDateTime.now());
        adRepository.save(ad);
    }

    public GenericResponseDto getAll(AdType type, int size) throws CustomException{

        Page<AdResponseDto> response = adRepository.getAll(type, new PageRequest(0, size));

        updateAdsTempActivity(response.getContent().stream().map(AdResponseDto::getId).collect(Collectors.toList()));

        return new GenericResponseDto(response);
    }

    public Ad getByRepoMethod(Ad ad) throws CustomException {

        if (ad == null) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("AD_NOT_EXIST"));
        }

        return ad;
    }

    @Async
    void updateAdsTempActivity(List<Long> list) throws CustomException{
        for (Long aLong : list) {
            updateAdTempActivity(aLong);
        }
    }

    void updateAdTempActivity(Long adId) throws CustomException{
        Ad ad = getByRepoMethod(adRepository.findOne(adId));
        ad.setTempActivity(ad.getTempActivity()+1);
        adRepository.save(ad);
    }

    @Async
    void updateAdsTotalActivity(AdType type) throws CustomException{
        List<Ad> ads = adRepository.getAllByType(type);

        for (Ad ad : ads) {
            updateAdTotalActivity(ad);
        }
    }

    void updateAdTotalActivity(Ad ad) throws CustomException{
        ad.setTotalActivity(ad.getTotalActivity()+ad.getTempActivity());
        ad.setTempActivity(0);
        adRepository.save(ad);
    }
}
