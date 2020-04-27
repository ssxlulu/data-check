package com.ssxlulu;

import com.google.gson.Gson;
import com.ssxlulu.config.CheckConfiguration;
import com.ssxlulu.datasource.DataSourceManager;
import com.ssxlulu.executor.RecorderCheckTask;
import com.ssxlulu.executor.RecorderCheckTaskFactory;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Hello world!
 *
 */
public class Bootstrap {

    private static final Gson GSON = new Gson();

    @SneakyThrows
    public static void main( String[] args )
    {
        InputStream fileInputStream = Bootstrap.class.getResourceAsStream("/conf/config.json");
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        CheckConfiguration checkConfiguration = GSON.fromJson(inputStreamReader, CheckConfiguration.class);
        Semaphore semaphore = new Semaphore(checkConfiguration.getJobConfiguration().getConcurrency());
        DataSourceManager dataSourceManager = new DataSourceManager(checkConfiguration);
        try (Connection connection = dataSourceManager.getDataSource(checkConfiguration.getSourceDatasource()).getConnection()) {
            ResultSet tables = connection.getMetaData().getTables(connection.getCatalog(), null, "%", new String[]{"TABLE"});
            List<String> tableNames = new ArrayList<>();
            while (tables.next()) {
                tableNames.add(tables.getString(3));
            }
            for (String tableName : tableNames) {
                RecorderCheckTask recorderCheckTask = RecorderCheckTaskFactory.createRecorderCheckTask(dataSourceManager, checkConfiguration, tableName, semaphore);
                recorderCheckTask.prepare();
                recorderCheckTask.start();
            }
        }
    }
}
