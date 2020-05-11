package com.ssxlulu.metadata.util;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Jdbc util.
 *
 * @author ssxlulu
 */
public class JdbcUtil {

    /**
     * Get schema.
     *
     * @param connection connection
     * @return schema
     */
    public static String getSchema(final Connection connection) {
        String result = null;
        try {
            result = connection.getSchema();
        } catch (final SQLException ignore) {
        }
        return result;
    }
}
