package com.ssxlulu.config;

import lombok.Getter;
import lombok.Setter;

/**
 * DataCheck Configuraion.
 *
 * @author ssxlulu
 */
@Setter
@Getter
public class CheckConfiguration {

    private DatasourceConfiguration sourceDatasource;

    private DatasourceConfiguration destinationDataSource;

    private JobConfiguration jobConfiguration;
}
