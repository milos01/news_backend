package init.app.domain.old_mapper;

import init.app.domain.old_model.OldVote;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static init.app.util.ConvertUtil.getUUIDFromByteArray;

public class OldVoteMapper implements RowMapper<OldVote> {
    @Override
    public OldVote mapRow(ResultSet resultSet, int i) throws SQLException {
        return new OldVote(
                getUUIDFromByteArray(resultSet.getBytes("VoteID")),
                getUUIDFromByteArray(resultSet.getBytes("UserRefID")),
                getUUIDFromByteArray(resultSet.getBytes("PollRefID")),
                resultSet.getBoolean("IsFirstChoice"),
                resultSet.getBoolean("IsSecondChoice"),
                resultSet.getBoolean("IsThirdChoice"),
                resultSet.getString("Timestamp"),
                resultSet.getBoolean("IsDeleted")
        );
    }
}
