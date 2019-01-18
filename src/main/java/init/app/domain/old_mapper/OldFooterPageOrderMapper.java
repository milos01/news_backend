package init.app.domain.old_mapper;

import init.app.domain.old_model.OldFooterPageOrder;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static init.app.util.ConvertUtil.getUUIDFromByteArray;

public class OldFooterPageOrderMapper implements RowMapper<OldFooterPageOrder> {
    @Override
    public OldFooterPageOrder mapRow(ResultSet resultSet, int i) throws SQLException {
        return new OldFooterPageOrder(
                getUUIDFromByteArray(resultSet.getBytes("FooterPageOrderId")),
                resultSet.getInt("OrderNumber"),
                resultSet.getString("Timestamp"),
                getUUIDFromByteArray(resultSet.getBytes("ArticleRefID")),
                resultSet.getBoolean("IsDeleted")
        );
    }
}
