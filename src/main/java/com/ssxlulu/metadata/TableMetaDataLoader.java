package com.ssxlulu.metadata;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author ssxlulu
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TableMetaDataLoader {

    /**
     * Load table meta data.
     *
     * @param dataSource data source
     * @param table table name
     * @param databaseType database type
     * @return table meta data
     * @throws SQLException SQL exception
     */
    public static TableMetaData load(final DataSource dataSource, final String table, final String databaseType) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            return new TableMetaData(table, ColumnMetaDataLoader.load(connection, table, databaseType), IndexMetaDataLoader.load(connection, table));
        }
    }
}
