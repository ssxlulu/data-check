package com.ssxlulu.config;

import lombok.Getter;
import lombok.Setter;

/**
 * Data source configuration.
 *
 * @author ssxlulu
 */
@Getter
@Setter
public class DatasourceConfiguration {

    private String jdbcUrl;

    private String username;

    private String password;

    public DatasourceConfiguration(final String jdbcUrl, final String username, final String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }
}
