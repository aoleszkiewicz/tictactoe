package com.example.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.File;

public class DataSource {
    private static HikariConfig config;
    private static HikariDataSource ds;
    
    static {
        try {
            // sprawdz czy katalog db istnieje
            File dbDir = new File("db");
            if (!dbDir.exists()) {
                dbDir.mkdirs();
            }
            
            config = new HikariConfig();
            config.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
            config.setJdbcUrl("jdbc:hsqldb:file:db/oxgame;shutdown=true");
            config.setUsername("admin");
            config.setPassword("admin");
            config.setAutoCommit(true);
            ds = new HikariDataSource(config);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }

    private DataSource() {}

    public static Connection getConnection() throws SQLException {
        return ds.getConnection(); // zwroc buforwana liste polaczen aby uniknac korzystania z DriverManagera i uniknac tym samym tworzenie nowych polaczen przy otwarciu okienka
    }
}
