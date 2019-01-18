package init.app.domain.old_mapper;

import init.app.domain.old_model.OldPoll;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static init.app.util.ConvertUtil.getUUIDFromByteArray;

public class OldPollMapper implements RowMapper<OldPoll> {
    @Override
    public OldPoll mapRow(ResultSet resultSet, int i) throws SQLException {
        return new OldPoll(
                getUUIDFromByteArray(resultSet.getBytes("PollID")),
                resultSet.getString("Question"),
                resultSet.getString("FirstChoiceText"),
                resultSet.getString("SecondChoiceText"),
                resultSet.getString("ThirdChoiceText"),
                resultSet.getInt("R_FirstChoiceAmount"),
                resultSet.getInt("R_SecondChoiceAmount"),
                resultSet.getInt("R_ThirdChoiceAmount"),
                getUUIDFromByteArray(resultSet.getBytes("ArticleRefID")),
                resultSet.getString("Timestamp"),
                resultSet.getBoolean("IsDeleted"),
                resultSet.getBoolean("IsPollOfTheDay")
        );
    }
}
