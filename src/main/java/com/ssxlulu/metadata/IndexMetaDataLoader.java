package com.ssxlulu.metadata;

import com.ssxlulu.metadata.util.JdbcUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

/**
 * Index meta data loader.
 *
 * @author ssxlulu
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IndexMetaDataLoader {

    private static final String INDEX_NAME = "INDEX_NAME";

    /**
     * Load column meta data list.
     *
     * @param connection connection
     * @param table table name
     * @return index meta data list
     * @throws SQLException SQL exception
     */
    public static Collection<IndexMetaData> load(final Connection connection, final String table) throws SQLException {
        Collection<IndexMetaData> result = new HashSet<>();
        try (ResultSet resultSet = connection.getMetaData().getIndexInfo(connection.getCatalog(), JdbcUtil.getSchema(connection), table, false, false)) {
            while (resultSet.next()) {
                String indexName = resultSet.getString(INDEX_NAME);
                result.add(new IndexMetaData(indexName));
            }
        }
        return result;
    }
}
