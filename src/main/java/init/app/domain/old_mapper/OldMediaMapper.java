package init.app.domain.old_mapper;

import init.app.domain.old_model.OldMedia;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static init.app.util.ConvertUtil.getUUIDFromByteArray;

public class OldMediaMapper implements RowMapper<OldMedia> {
    @Override
    public OldMedia mapRow(ResultSet resultSet, int i) throws SQLException {
        return new OldMedia(
                getUUIDFromByteArray(resultSet.getBytes("MediaID")),
                resultSet.getString("ServerPath"),
                getUUIDFromByteArray(resultSet.getBytes("ArticleRefID")),
                resultSet.getString("Timestamp"),
                resultSet.getString("Description"),
                resultSet.getBoolean("IsDeleted"),
                resultSet.getInt("OrdinalNumber")
        );
    }
}
