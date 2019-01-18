package init.app.util;

import init.app.web.dto.shared.AllResponseDto;
import init.app.web.dto.shared.GenericResponseDto;
import init.app.web.dto.shared.PageResponseDto;
import init.app.web.dto.shared.SingleResponseDto;
import org.springframework.data.domain.Page;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;

import static init.app.util.ResultSetParserUtil.*;

public class ConvertUtil {

    public static SingleResponseDto convertQueryToSingleResponse(Object[] object, Class clazz) {
        SingleResponseDto response = new SingleResponseDto();
        response.setContent(parseSingleArray(object, clazz));
        return response;
    }

    public static SingleResponseDto convertModelToSingleResponse(Object object, Class clazz) {
        SingleResponseDto response = new SingleResponseDto();
        response.setContent(parseSingleObject(object, clazz));
        return response;
    }

    public static AllResponseDto convertToAllResponse(List<Object[]> objects, Class clazz) {
        AllResponseDto response = new AllResponseDto();
        response.setContent(parseAll(objects, clazz));
        response.setTotalElements(objects.size());
        return response;
    }

    public static GenericResponseDto convertToAutocompleteResponse(List<Object> objects, Class clazz) {
        GenericResponseDto response = new GenericResponseDto();
        response.setContent(parseListOfObjects(objects, clazz));
        return response;
    }

    public static PageResponseDto convertToPageResponse(Page page, Class clazz) {
        PageResponseDto response = new PageResponseDto();
        response.setContent(parseAll(page.getContent(), clazz));
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setRequestSize(page.getSize());
        response.setRequestPage(page.getNumber());
        response.setNumberOfElements(page.getNumberOfElements());
        response.setFirst(page.isFirst());
        response.setLast(page.isLast());
        return response;
    }

    public static GenericResponseDto convertToAutocompleteResponse(Object object) {
        return new GenericResponseDto(object);
    }

    public static UUID getUUIDFromByteArray(byte[]  bytes){
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long high = bb.getLong();
        long low = bb.getLong();
        return new UUID(high, low);
    }
}
