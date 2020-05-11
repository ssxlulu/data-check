package com.ssxlulu.executor.reader;

import com.ssxlulu.common.model.Column;
import com.ssxlulu.common.model.DataRecord;
import com.ssxlulu.common.model.FinishedRecord;
import com.ssxlulu.common.model.Record;
import com.ssxlulu.config.DatasourceConfiguration;
import com.ssxlulu.datasource.DataSourceManager;
import com.ssxlulu.executor.AbstractExecutor;
import com.ssxlulu.metadata.TableMetaData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Data record reader.
 *
 * @author ssxlulu
 */
@Slf4j
@RequiredArgsConstructor
public class RecorderReader extends AbstractExecutor {

    private final DataSourceManager dataSourceManager;

    private final DatasourceConfiguration datasourceConfiguration;

    @Getter
    private final TableMetaData tableMetaData;

    private final LinkedBlockingQueue<Record> queue = new LinkedBlockingQueue<>(4000);

    @Override
    public void run() {
        start();
        read();
    }

    @SneakyThrows
    private void read() {
        MDC.put("table", tableMetaData.getTableName());
        try (Connection conn = dataSourceManager.getDataSource(datasourceConfiguration).getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(String.format("SELECT * FROM %s", tableMetaData.getTableName()));
            preparedStatement.setFetchSize(2000);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (isRunning() && resultSet.next()) {
                DataRecord dataRecord = new DataRecord(metaData.getColumnCount());
                dataRecord.setTableName(tableMetaData.getTableName());
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    dataRecord.addColumn(new Column(metaData.getColumnName(i), readValue(resultSet, i), tableMetaData.isPrimaryKey(i - 1)));
                }
                queue.put(dataRecord);
            }
            queue.put(new FinishedRecord());
            stop();
            log.info("Table {} read finished.", tableMetaData.getTableName());
        }
    }

    /**
     * Extract data records.
     *
     * @return data records
     */
    public List<Record> extract() {
        List<Record> dataRecords = new ArrayList<>();
        for (int i = 0; i < 2000; i++) {
            Record dataRecord = queue.poll();
            if (dataRecord != null) {
                dataRecords.add(dataRecord);
            } else if (!isRunning() && queue.isEmpty()) {
                break;
            }
        }
        return dataRecords;
    }

    private Object readValue(final ResultSet resultSet, final int index) throws SQLException {
        return resultSet.getObject(index);
    }
}
