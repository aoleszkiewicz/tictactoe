package org.tictactoe.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class Datasource {
    private static HikariConfig hikariConfig;
    private static HikariDataSource hikariDataSource;

    public Datasource() {}

    static {
        try {
            hikariConfig = new HikariConfig();
            hikariConfig.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
            hikariConfig.setJdbcUrl("jdbc:hsqldb:file:db/tictactoe;shutdown=true");
            hikariConfig.setUsername("admin");
            hikariConfig.setPassword("admin");
            hikariConfig.setAutoCommit(true);
            hikariDataSource = new HikariDataSource(hikariConfig);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database connection.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return hikariDataSource.getConnection();
    }
}
