package com.ssxlulu.metadata;

import lombok.RequiredArgsConstructor;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ssxlulu
 */
@RequiredArgsConstructor
public class MetaDataManager {

    private final DataSource dataSource;

    private final Map<String, TableMetaData> tableMetaDataMap = new HashMap<>();

    /**
     * Get table meta data by table name.
     *
     * @param tableName table name
     * @return table meta data
     */
    public TableMetaData getTableMetaData(final String tableName) {
        if (!tableMetaDataMap.containsKey(tableName)) {
            try {
                tableMetaDataMap.put(tableName, TableMetaDataLoader.load(dataSource, tableName, ""));
            } catch (SQLException e) {
                throw new RuntimeException(String.format("Load metaData for table %s failed", tableName), e);
            }
        }
        return tableMetaDataMap.get(tableName);
    }
}
