package com.ssxlulu.common.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Data record.
 *
 * @author ssxlulu
 */
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class DataRecord implements Record {

    private String tableName;

    private final List<Column> columns;

    private final List<Column> primaryKeys;

    public DataRecord(final int columnCount) {
        this.columns = new ArrayList<>(columnCount);
        this.primaryKeys = new ArrayList<>();
    }

    /**
     * Add a column to record.
     *
     * @param data column
     */
    public void addColumn(final Column data) {
        if (data.isPrimaryKey()) {
            primaryKeys.add(data);
        }
        columns.add(data);
    }

    /**
     * Return column count.
     *
     * @return count
     */
    public int getColumnCount() {
        return columns.size();
    }

    /**
     * Get column by index.
     *
     * @param index of column
     * @return column
     */
    public Column getColumn(final int index) {
        return columns.get(index);
    }

    /**
     * Get primary keys.
     *
     * @return primary keys
     */
    public List<Column> getPrimaryKeys() {
        return primaryKeys;
    }

    /**
     * Get string format of data record.
     *
     * @return data record
     */
    public String getStringRecord() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        List<String> columnValues = new ArrayList<>();
        for (Column column : columns) {
            columnValues.add(column.getValue().toString());
        }
        stringBuilder.append(StringUtils.join(columnValues, ",")).append("]");
        return stringBuilder.toString();
    }
}
