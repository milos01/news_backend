package init.app.domain.old_mapper;

import init.app.domain.old_model.OldShare;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static init.app.util.ConvertUtil.getUUIDFromByteArray;

public class OldShareMapper implements RowMapper<OldShare> {
    @Override
    public OldShare mapRow(ResultSet resultSet, int i) throws SQLException {
        return new OldShare(
                getUUIDFromByteArray(resultSet.getBytes("ShareID")),
                getUUIDFromByteArray(resultSet.getBytes("SharingUserRefID")),
                getUUIDFromByteArray(resultSet.getBytes("ArticleToShareRefID")),
                resultSet.getString("Timestamp"),
                resultSet.getBoolean("IsDeleted")
        );
    }
}
