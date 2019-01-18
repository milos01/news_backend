package init.app.domain.old_mapper;

import init.app.domain.old_model.OldUserTag;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static init.app.util.ConvertUtil.getUUIDFromByteArray;

public class OldUserTagMapper implements RowMapper<OldUserTag> {
    @Override
    public OldUserTag mapRow(ResultSet resultSet, int i) throws SQLException {
        return new OldUserTag(
                getUUIDFromByteArray(resultSet.getBytes("UserTagID")),
                getUUIDFromByteArray(resultSet.getBytes("TagRefID")),
                getUUIDFromByteArray(resultSet.getBytes("UserRefID")),
                resultSet.getString("Timestamp"),
                resultSet.getBoolean("IsDeleted")
        );
    }
}
