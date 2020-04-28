package com.ssxlulu.executor.check;

import com.ssxlulu.common.model.Column;
import com.ssxlulu.common.model.DataRecord;
import com.ssxlulu.common.model.FinishedRecord;
import com.ssxlulu.common.model.Record;
import com.ssxlulu.config.DatasourceConfiguration;
import com.ssxlulu.datasource.DataSourceManager;
import com.ssxlulu.exception.DataCheckException;
import com.ssxlulu.executor.AbstractExecutor;
import com.ssxlulu.executor.reader.RecorderReader;
import com.ssxlulu.metadata.TableMetaData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.MDC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

/**
 * @author ssxlulu
 */
@Slf4j
@RequiredArgsConstructor
public class RecorderChecker extends AbstractExecutor {

    private final DataSourceManager dataSourceManager;

    private final DatasourceConfiguration datasourceConfiguration;

    private final TableMetaData tableMetaData;

    private final RecorderReader recorderReader;

    private final Semaphore semaphore;

    @Override
    public void run() {
        start();
        MDC.put("table", tableMetaData.getTableName());
        try {
            while (isRunning()) {
                List<Record> extract = recorderReader.extract();
                if (extract != null && extract.size() > 0) {
                    if (FinishedRecord.class.equals(extract.get(extract.size() - 1).getClass())) {
                        check(extract.stream().limit(extract.size() - 1).collect(Collectors.toList()));
                        break;
                    }
                    check(extract);
                }
            }
        } finally {
            stop();
            recorderReader.stop();
            semaphore.release();
            log.info("Table {} check finished.", tableMetaData.getTableName());
            MDC.remove("table");
        }
    }

    private void check(List<Record> dataRecords) {
        if (dataRecords == null || dataRecords.size() == 0) {
            return;
        }
        List<DataRecord> dataRecordList1 = dataRecords.stream().filter(obj -> obj instanceof DataRecord).map(obj -> (DataRecord) obj).collect(Collectors.toList());
        List<DataRecord> dataRecordList2 = queryRecord(dataRecordList1);
        Map<String, DataRecord> recordMap = new HashMap<>();
        for (DataRecord dataRecord : dataRecordList2) {
            if (tableMetaData.hasPrimaryKey()) {
                recordMap.put(dataRecord.getPrimaryKeys().get(0).getValue().toString(), dataRecord);
            } else {
                recordMap.put(dataRecord.getColumn(0).getValue().toString(), dataRecord);
            }
        }
        for (DataRecord dataRecord : dataRecordList1) {
            if (tableMetaData.hasPrimaryKey()) {
                diff(dataRecord, recordMap.remove(dataRecord.getPrimaryKeys().get(0).getValue().toString()));
            } else {
                diff(dataRecord, recordMap.remove(dataRecord.getColumn(0).getValue().toString()));
            }
        }
    }

    private List<DataRecord> queryRecord(List<DataRecord> dataRecords) {
        String tableName = tableMetaData.getTableName();
        Map<String, List<Object>> queryMap = new HashMap<>();
        boolean hasPrimaryKey = tableMetaData.hasPrimaryKey();
        if (hasPrimaryKey) {
            queryMap.put(tableMetaData.getPrimaryKey(), new ArrayList<>());
        } else {
            queryMap.put(tableMetaData.getColumnMetaData(0).getName(), new ArrayList<>());
        }
        for (DataRecord dataRecord : dataRecords) {
            if (hasPrimaryKey) {
                queryMap.get(tableMetaData.getPrimaryKey()).add(dataRecord.getPrimaryKeys().get(0).getValue());
            } else {
                queryMap.get(tableMetaData.getColumnMetaData(0).getName()).add(dataRecord.getColumns().get(0).getValue());
            }
        }
        String sql = String.format("SELECT * FROM %s where %s in (", tableName, queryMap.entrySet().stream().findFirst().get().getKey());
        sql = sql + StringUtils.join(queryMap.entrySet().stream().findFirst().get().getValue(), ",") + ")";
        List<DataRecord> dataRecordList = new ArrayList<>();
        try (Connection conn = dataSourceManager.getDataSource(datasourceConfiguration).getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                DataRecord dataRecord = new DataRecord(metaData.getColumnCount());
                dataRecord.setTableName(tableMetaData.getTableName());
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    dataRecord.addColumn(new Column(metaData.getColumnName(i), readValue(resultSet, i), tableMetaData.isPrimaryKey(i - 1)));
                }
                dataRecordList.add(dataRecord);
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("doesn't exist")) {
                log.info("Destination database doesn't have table: {} {}", tableName, System.getProperty("line.separator"));
                throw new DataCheckException("Destination database doesn't have table: " + tableName);
            }
            log.info(e.getMessage(), e);
            throw new DataCheckException(e);
        }
        return dataRecordList;
    }

    private Object readValue(final ResultSet resultSet, final int index) throws SQLException {
        return resultSet.getObject(index);
    }

    private void diff(DataRecord dataRecord1, DataRecord dataRecord2) {
        if (!dataRecord1.equals(dataRecord2)) {
            log.info("Different records in table {} , source record: {} {} destination record: {} {}",
                    dataRecord1.getTableName(),
                    dataRecord1.getStringRecord(), System.getProperty("line.separator"),
                    dataRecord2 == null ? null : dataRecord2.getStringRecord(), System.getProperty("line.separator"));
        }
    }
}
