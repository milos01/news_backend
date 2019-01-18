package init.app.domain.old_mapper;

import init.app.domain.old_model.OldArticleTag;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static init.app.util.ConvertUtil.getUUIDFromByteArray;

public class OldArticleTagMapper implements RowMapper<OldArticleTag> {
    @Override
    public OldArticleTag mapRow(ResultSet resultSet, int i) throws SQLException {
        return new OldArticleTag(
                getUUIDFromByteArray(resultSet.getBytes("ArticleTagID")),
                getUUIDFromByteArray(resultSet.getBytes("TagRefID")),
                getUUIDFromByteArray(resultSet.getBytes("ArticleRefID")),
                resultSet.getString("Timestamp"),
                resultSet.getBoolean("IsDeleted")
        );
    }
}
