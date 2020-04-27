package com.ssxlulu.metadata;

import com.ssxlulu.metadata.util.JdbcUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * @author ssxlulu
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ColumnMetaDataLoader {

    private static final String COLUMN_NAME = "COLUMN_NAME";

    private static final String DATA_TYPE = "DATA_TYPE";

    private static final String TYPE_NAME = "TYPE_NAME";

    /**
     * Load column meta data list.
     *
     * @param connection connection
     * @param table table name
     * @param databaseType database type
     * @return column meta data list
     * @throws SQLException SQL exception
     */
    public static Collection<ColumnMetaData> load(final Connection connection, final String table, final String databaseType) throws SQLException {
        if (!isTableExist(connection, connection.getCatalog(), table)) {
            return Collections.emptyList();
        }
        Collection<ColumnMetaData> result = new LinkedList<>();
        Collection<String> primaryKeys = loadPrimaryKeys(connection, table);
        List<String> columnNames = new ArrayList<>();
        List<Integer> columnTypes = new ArrayList<>();
        List<String> columnTypeNames = new ArrayList<>();
        List<Boolean> isPrimaryKeys = new ArrayList<>();
        try (ResultSet resultSet = connection.getMetaData().getColumns(connection.getCatalog(), JdbcUtil.getSchema(connection), table, "%")) {
            while (resultSet.next()) {
                String columnName = resultSet.getString(COLUMN_NAME);
                columnTypes.add(resultSet.getInt(DATA_TYPE));
                columnTypeNames.add(resultSet.getString(TYPE_NAME));
                isPrimaryKeys.add(primaryKeys.contains(columnName));
                columnNames.add(columnName);
            }
        }
        for (int i = 0; i < columnNames.size(); i++) {
            result.add(new ColumnMetaData(columnNames.get(i), columnTypes.get(i), columnTypeNames.get(i), isPrimaryKeys.get(i)));
        }
        return result;
    }

    private static boolean isTableExist(final Connection connection, final String catalog, final String table) throws SQLException {
        try (ResultSet resultSet = connection.getMetaData().getTables(catalog, JdbcUtil.getSchema(connection), table, null)) {
            return resultSet.next();
        }
    }

    private static Collection<String> loadPrimaryKeys(final Connection connection, final String table) throws SQLException {
        Collection<String> result = new HashSet<>();
        try (ResultSet resultSet = connection.getMetaData().getPrimaryKeys(connection.getCatalog(), JdbcUtil.getSchema(connection), table)) {
            while (resultSet.next()) {
                result.add(resultSet.getString(COLUMN_NAME));
            }
        }
        return result;
    }
}
