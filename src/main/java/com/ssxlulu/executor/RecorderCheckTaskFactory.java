package com.ssxlulu.executor;

import com.ssxlulu.config.CheckConfiguration;
import com.ssxlulu.datasource.DataSourceManager;

/**
 * Data check task factory.
 *
 * @author ssxlulu
 */
public class RecorderCheckTaskFactory {

    /**
     * Create data check task.
     *
     * @param dataSourceManager data source manager
     * @param checkConfiguration data check configuration
     * @param tableName table name
     * @param executeEngine execute engine
     * @return data check task
     */
    public static RecorderCheckTask createRecorderCheckTask(final DataSourceManager dataSourceManager, final CheckConfiguration checkConfiguration,
                                                            final String tableName, final ExecuteEngine executeEngine) {
        return new RecorderCheckTask(dataSourceManager, checkConfiguration, tableName, executeEngine);
    }
}
