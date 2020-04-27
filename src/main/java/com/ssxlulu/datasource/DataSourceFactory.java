package com.ssxlulu.datasource;

import com.ssxlulu.config.DatasourceConfiguration;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * @author ssxlulu
 */
public class DataSourceFactory {

    /**
     * New instance data source.
     *
     * @param dataSourceConfiguration data source configuration
     * @return new data source
     */
    public static DataSource newInstance(final DatasourceConfiguration dataSourceConfiguration) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(dataSourceConfiguration.getJdbcUrl());
        dataSource.setUsername(dataSourceConfiguration.getUsername());
        dataSource.setPassword(dataSourceConfiguration.getPassword());
        dataSource.setMaximumPoolSize(100);
        return dataSource;
    }
}
