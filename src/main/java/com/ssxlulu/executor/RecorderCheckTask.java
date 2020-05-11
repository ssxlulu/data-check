package com.ssxlulu.executor;

import com.ssxlulu.config.CheckConfiguration;
import com.ssxlulu.datasource.DataSourceManager;
import com.ssxlulu.executor.check.RecorderChecker;
import com.ssxlulu.executor.reader.RecorderReader;
import com.ssxlulu.metadata.MetaDataManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * Data check task.
 *
 * @author ssxlulu
 */
@RequiredArgsConstructor
public class RecorderCheckTask {

    private final DataSourceManager dataSourceManager;

    private final CheckConfiguration checkConfiguration;

    @Getter
    private final String tableName;

    private final ExecuteEngine executeEngine;

    @Getter
    private RecorderReader recorderReader;

    @Getter
    private RecorderChecker recorderChecker;

    /**
     * Prepare for data check task.
     */
    public void prepare() {
        MetaDataManager metaDataManager = new MetaDataManager(dataSourceManager.getDataSource(checkConfiguration.getSourceDatasource()));
        recorderReader = new RecorderReader(dataSourceManager, checkConfiguration.getSourceDatasource(), metaDataManager.getTableMetaData(tableName));
        recorderChecker = new RecorderChecker(dataSourceManager, checkConfiguration.getDestinationDataSource(), metaDataManager.getTableMetaData(tableName), recorderReader);
    }

    /**
     * Start data check task.
     */
    @SneakyThrows
    public void start() {
        executeEngine.submitTask(this);
    }
}
