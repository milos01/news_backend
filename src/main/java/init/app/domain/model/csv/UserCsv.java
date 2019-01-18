package init.app.domain.model.csv;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;

/**
 * Created by bojan.stankovic@codetri.be on 4/4/18.
 */
@Data
public class UserCsv {

    @CsvBindByPosition(position = 0)
    private String username;

    @CsvBindByPosition(position = 1)
    private String email;

    @CsvBindByPosition(position = 2)
    private String socialId;

    @CsvBindByPosition(position = 3)
    private String bio;

    @CsvBindByPosition(position = 4)
    private String role;

    @CsvBindByPosition(position = 5)
    private String profileImageUrl;

    @CsvBindByPosition(position = 6)
    private String tags;

    @CsvBindByPosition(position = 7)
    private String accountCreationTimestamp;

    @CsvBindByPosition(position = 8)
    private String accountActivationTimestamp;

}
