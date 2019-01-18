package init.app.domain.old_mapper;

import init.app.domain.old_model.OldFollowing;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static init.app.util.ConvertUtil.getUUIDFromByteArray;

public class OldFollowingMapper implements RowMapper<OldFollowing> {
    @Override
    public OldFollowing mapRow(ResultSet resultSet, int i) throws SQLException {
        return new OldFollowing(
                getUUIDFromByteArray(resultSet.getBytes("FollowingID")),
                resultSet.getBytes("UserToFollowRefID")!=null?getUUIDFromByteArray(resultSet.getBytes("UserToFollowRefID")):null,
                resultSet.getBytes("TagToFollowRefID")!=null?getUUIDFromByteArray(resultSet.getBytes("TagToFollowRefID")):null,
                getUUIDFromByteArray(resultSet.getBytes("FollowingUserRefID")),
                resultSet.getString("Timestamp"),
                resultSet.getBoolean("IsDeleted"),
                resultSet.getInt("Type")
        );
    }
}
