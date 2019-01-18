package init.app.domain.old_mapper;

import init.app.domain.old_model.OldReadLater;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static init.app.util.ConvertUtil.getUUIDFromByteArray;

public class OldReadLaterMapper implements RowMapper<OldReadLater> {
    @Override
    public OldReadLater mapRow(ResultSet resultSet, int i) throws SQLException {
        return new OldReadLater(
                getUUIDFromByteArray(resultSet.getBytes("ReadLaterID")),
                getUUIDFromByteArray(resultSet.getBytes("ArticleRefID")),
                getUUIDFromByteArray(resultSet.getBytes("UserRefID")),
                resultSet.getString("Timestamp"),
                resultSet.getString("Note"),
                resultSet.getBoolean("IsDeleted")
        );
    }
}
