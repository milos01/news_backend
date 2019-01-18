package init.app.web.dto.custom;

import init.app.web.dto.response.MessageResponseDto;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by bojan.stankovic@codetri.be on 4/17/18.
 */
@Data
public class GetAllMessagesDto {

    private List<MessageResponseDto> response;
    private Boolean anyMessageStatusChanged;

}
