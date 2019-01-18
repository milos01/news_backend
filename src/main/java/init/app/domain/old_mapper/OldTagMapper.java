package init.app.domain.old_mapper;

import init.app.domain.old_model.OldTag;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static init.app.util.ConvertUtil.getUUIDFromByteArray;

public class OldTagMapper implements RowMapper<OldTag> {
    @Override
    public OldTag mapRow(ResultSet resultSet, int i) throws SQLException {
        return new OldTag(
                getUUIDFromByteArray(resultSet.getBytes("TagID")),
                resultSet.getString("Text"),
                resultSet.getString("Timestamp"),
                resultSet.getBoolean("IsDeleted")
        );
    }
}
