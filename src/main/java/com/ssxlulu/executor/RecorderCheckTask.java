package com.ssxlulu.executor;

import com.ssxlulu.config.CheckConfiguration;
import com.ssxlulu.datasource.DataSourceManager;
import com.ssxlulu.executor.check.RecorderChecker;
import com.ssxlulu.executor.reader.RecorderReader;
import com.ssxlulu.metadata.MetaDataManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.concurrent.Semaphore;

/**
 * @author ssxlulu
 */
@RequiredArgsConstructor
public class RecorderCheckTask {

    private final DataSourceManager dataSourceManager;

    private final CheckConfiguration checkConfiguration;

    private final String tableName;

    private final Semaphore semaphore;

    private RecorderReader recorderReader;

    private RecorderChecker recorderChecker;

    public void prepare() {
        MetaDataManager metaDataManager = new MetaDataManager(dataSourceManager.getDataSource(checkConfiguration.getSourceDatasource()));
        recorderReader = new RecorderReader(dataSourceManager, checkConfiguration.getSourceDatasource(), metaDataManager.getTableMetaData(tableName));
        recorderChecker = new RecorderChecker(dataSourceManager, checkConfiguration.getDestinationDataSource(), metaDataManager.getTableMetaData(tableName), recorderReader, semaphore);
    }

    @SneakyThrows
    public void start() {
        semaphore.acquire();
        Thread thread1 = new Thread(recorderReader, "Thread - Reader - " + recorderReader.getTableMetaData().getTableName());
        thread1.start();
        Thread thread2 = new Thread(recorderChecker, "Thread - Checker - " + recorderReader.getTableMetaData().getTableName());
        thread2.start();
    }
}
