package pe.nuevasonrisa.config;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final HikariDataSource dataSource;

    static {

        HikariConfig config = new HikariConfig();

        config.setDriverClassName("org.postgresql.Driver");

        config.setJdbcUrl("jdbc:postgresql://aws-1-us-east-1.pooler.supabase.com:5432/postgres?sslmode=require");
        config.setUsername("postgres.lzcusvckswiijukvdxgu");
        config.setPassword("arE$4#4-9cY/5z7");

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
