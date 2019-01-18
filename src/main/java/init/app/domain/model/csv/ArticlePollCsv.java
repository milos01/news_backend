package init.app.domain.model.csv;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;

/**
 * Created by bojan.stankovic@codetri.be on 4/4/18.
 */
@Data
public class ArticlePollCsv {

    @CsvBindByPosition(position = 0)
    private String headline;

    @CsvBindByPosition(position = 1)
    private String question;

    @CsvBindByPosition(position = 2)
    private String firstChoiceText;

    @CsvBindByPosition(position = 3)
    private String firstChoiceVotes;

    @CsvBindByPosition(position = 4)
    private String secondChoiceText;

    @CsvBindByPosition(position = 5)
    private String secondChoiceVotes;

    @CsvBindByPosition(position = 6)
    private String thirdChoiceText;

    @CsvBindByPosition(position = 7)
    private String thirdChoiceVotes;

    @CsvBindByPosition(position = 8)
    private String timestamp;
}
