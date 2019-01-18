package init.app.domain.old_mapper;

import init.app.domain.old_model.OldUser;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static init.app.util.ConvertUtil.getUUIDFromByteArray;

public class OldUserMapper implements RowMapper<OldUser> {

    @Override
    public OldUser mapRow(ResultSet resultSet, int i) throws SQLException {

        return new OldUser(
                getUUIDFromByteArray(resultSet.getBytes("UserID")),
                resultSet.getString("Email"),
                resultSet.getString("Password"),
                resultSet.getString("UserName"),
                resultSet.getString("BIO"),
                resultSet.getString("FacebookId"),
                resultSet.getString("GoogleId"),
                resultSet.getString("TwitterId"),
                resultSet.getString("ProfilePictureServerPath"),
                resultSet.getString("Timestamp"),
                resultSet.getInt("Type"),
                resultSet.getBoolean("IsDeleted"),
                resultSet.getBoolean("IsDeactivated"),
                resultSet.getString("EmailActivatedTimestamp")
        );
    }
}