package org.xfurkanadenia.xtuccar.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.xfurkanadenia.xtuccar.XTuccar;

import java.io.File;
import java.sql.*;

public class DatabaseManager {
    private final HikariDataSource dataSource;

    public DatabaseManager(DatabaseType type, String host, int port, String dbName, String user, String password) {
        HikariConfig config = new HikariConfig();

        if (type == DatabaseType.MYSQL) {
            // Önce database yoksa oluştur
            createMySQLDatabaseIfNotExists(host, port, dbName, user, password);

            // Ardından HikariCP config
            config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + dbName + "?useSSL=false&autoReconnect=true&allowPublicKeyRetrieval=true");
            config.setUsername(user);
            config.setPassword(password);
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        } else { // SQLITE
            File dbFile = new File(XTuccar.getInstance().getDataFolder(),"database.db");
            config.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
            config.setDriverClassName("org.sqlite.JDBC");
        }

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);
        config.setMaxLifetime(1800000);
        config.setConnectionTimeout(10000);

        dataSource = new HikariDataSource(config);
    }

    // MYSQL database oluşturma metodu
    private void createMySQLDatabaseIfNotExists(String host, int port, String dbName, String user, String password) {
        String url = "jdbc:mysql://" + host + ":" + port + "/?useSSL=false&allowPublicKeyRetrieval=true";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + dbName + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_turkish_ci");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    /* MARKET METHODS */

}