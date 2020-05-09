package com.ssxlulu.executor;

import com.ssxlulu.config.CheckConfiguration;
import com.ssxlulu.datasource.DataSourceManager;

import java.util.concurrent.Semaphore;

/**
 * @author ssxlulu
 */
public class RecorderCheckTaskFactory {

    public static RecorderCheckTask createRecorderCheckTask(DataSourceManager dataSourceManager, CheckConfiguration checkConfiguration, String tableName, ExecuteEngine executeEngine) {
        return new RecorderCheckTask(dataSourceManager, checkConfiguration, tableName, executeEngine);
    }
}
