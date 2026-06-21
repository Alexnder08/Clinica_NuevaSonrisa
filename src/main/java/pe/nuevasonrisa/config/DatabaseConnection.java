package pe.nuevasonrisa.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public final class DatabaseConnection {

    private static final Object POOL_LOCK = new Object();
    private static final String JDBC_URL = System.getenv().getOrDefault(
            "DATABASE_URL",
            "jdbc:postgresql://aws-1-us-east-1.pooler.supabase.com:5432/postgres?sslmode=require"
    );
    private static final String USERNAME = System.getenv().getOrDefault(
            "DATABASE_USER",
            "postgres.lzcusvckswiijukvdxgu"
    );
    private static final String PASSWORD = System.getenv().getOrDefault(
            "DATABASE_PASSWORD",
            "arE$4#4-9cY/5z7"
    );

    private static volatile HikariDataSource dataSource;

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        SQLException primerError = null;

        for (int intento = 0; intento < 2; intento++) {
            HikariDataSource pool = obtenerPool();
            try {
                Connection connection = pool.getConnection();
                if (!connection.isValid(5)) {
                    connection.close();
                    throw new SQLException("La conexion a PostgreSQL no supero la validacion.");
                }
                return connection;
            } catch (SQLException e) {
                if (primerError == null) {
                    primerError = e;
                }
                descartarPool(pool);
            }
        }

        throw new SQLException("No se pudo establecer una conexion estable con PostgreSQL.", primerError);
    }

    private static HikariDataSource obtenerPool() {
        HikariDataSource pool = dataSource;
        if (pool != null && !pool.isClosed()) {
            return pool;
        }

        synchronized (POOL_LOCK) {
            if (dataSource == null || dataSource.isClosed()) {
                dataSource = crearPool();
            }
            return dataSource;
        }
    }

    private static HikariDataSource crearPool() {
        HikariConfig config = new HikariConfig();
        config.setPoolName("NuevaSonrisaPool");
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl(urlConParametrosDeEstabilidad(JDBC_URL));
        config.setUsername(USERNAME);
        config.setPassword(PASSWORD);
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(0);
        config.setConnectionTimeout(15_000);
        config.setValidationTimeout(5_000);
        config.setIdleTimeout(60_000);
        config.setMaxLifetime(300_000);
        config.setKeepaliveTime(30_000);
        config.setInitializationFailTimeout(-1);
        config.setConnectionTestQuery("SELECT 1");
        config.setAutoCommit(true);
        return new HikariDataSource(config);
    }

    private static void descartarPool(HikariDataSource pool) {
        synchronized (POOL_LOCK) {
            if (dataSource == pool) {
                dataSource = null;
            }
        }
        if (pool != null && !pool.isClosed()) {
            pool.close();
        }
    }

    private static String urlConParametrosDeEstabilidad(String url) {
        String separador = url.contains("?") ? "&" : "?";
        return url + separador + "tcpKeepAlive=true&connectTimeout=10&socketTimeout=30";
    }

    public static String getJdbcUrl() { return JDBC_URL; }
    public static String getUsername() { return USERNAME; }
    public static String getPassword() { return PASSWORD; }
}
