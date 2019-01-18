package init.app.domain.old_mapper;

import init.app.domain.old_model.OldArticle;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static init.app.util.ConvertUtil.getUUIDFromByteArray;

public class OldArticleMapper implements RowMapper<OldArticle> {


    @Override
    public OldArticle mapRow(ResultSet resultSet, int i) throws SQLException {

        return new OldArticle(
                getUUIDFromByteArray(resultSet.getBytes("ArticleID")),
                getUUIDFromByteArray(resultSet.getBytes("AuthorRefId")),
                resultSet.getInt("Type"),
                resultSet.getString("Text"),
                resultSet.getString("Headline"),
                resultSet.getString("Timestamp"),
                resultSet.getBoolean("IsPublished"),
                resultSet.getString("PublishedAt"),
                resultSet.getInt("Activity"),
                resultSet.getBoolean("IsDeleted"),
                resultSet.getString("ArticleVideoLink"),
                resultSet.getInt("TempActivity"),
                resultSet.getInt("SponsoredActivity")
        );
    }
}
