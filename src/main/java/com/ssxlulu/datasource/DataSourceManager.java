package com.ssxlulu.datasource;

import com.ssxlulu.config.CheckConfiguration;
import com.ssxlulu.config.DatasourceConfiguration;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Data source manager.
 *
 * @author ssxlulu
 */
public class DataSourceManager {

    @Getter
    private final ConcurrentHashMap<DatasourceConfiguration, HikariDataSource> cachedDataSources = new ConcurrentHashMap<>();

    public DataSourceManager(final CheckConfiguration checkConfiguration) {
        List<DatasourceConfiguration> datasourceConfigurations = new ArrayList<>();
        datasourceConfigurations.add(checkConfiguration.getSourceDatasource());
        datasourceConfigurations.add(checkConfiguration.getDestinationDataSource());
        for (DatasourceConfiguration datasourceConfiguration : datasourceConfigurations) {
            HikariDataSource hikariDataSource = (HikariDataSource) DataSourceFactory.newInstance(datasourceConfiguration);
            cachedDataSources.put(datasourceConfiguration, hikariDataSource);
        }
    }

    /**
     * Get data source by {@code DataSourceConfiguration}.
     *
     * @param dataSourceConfiguration data source configuration
     * @return data source
     */
    public DataSource getDataSource(final DatasourceConfiguration dataSourceConfiguration) {
        if (cachedDataSources.containsKey(dataSourceConfiguration)) {
            return cachedDataSources.get(dataSourceConfiguration);
        }
        synchronized (cachedDataSources) {
            if (cachedDataSources.containsKey(dataSourceConfiguration)) {
                return cachedDataSources.get(dataSourceConfiguration);
            }
            HikariDataSource result = (HikariDataSource) DataSourceFactory.newInstance(dataSourceConfiguration);
            cachedDataSources.put(dataSourceConfiguration, result);
            return result;
        }
    }

    /**
     * Close, close cached data source.
     */
    public void close() {
        for (HikariDataSource each : cachedDataSources.values()) {
            if (!each.isClosed()) {
                each.close();
            }
        }
        cachedDataSources.clear();
    }
}
