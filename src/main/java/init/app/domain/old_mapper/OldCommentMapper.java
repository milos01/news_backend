package init.app.domain.old_mapper;

import init.app.domain.old_model.OldComment;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static init.app.util.ConvertUtil.getUUIDFromByteArray;

public class OldCommentMapper implements RowMapper<OldComment> {
    @Override
    public OldComment mapRow(ResultSet resultSet, int i) throws SQLException {
        return new OldComment(
                getUUIDFromByteArray(resultSet.getBytes("CommentID")),
                getUUIDFromByteArray(resultSet.getBytes("AuthorRefID")),
                resultSet.getString("Text"),
                getUUIDFromByteArray(resultSet.getBytes("ArticleRefID")),
                resultSet.getBytes("CommentRefID")!=null?getUUIDFromByteArray(resultSet.getBytes("CommentRefID")):null,
                resultSet.getBoolean("IsCommentOnComment"),
                resultSet.getBoolean("IsCommentOnArticle"),
                resultSet.getString("Timestamp"),
                resultSet.getBoolean("IsDeleted"),
                resultSet.getString("PictureServerPath")
        );
    }
}
