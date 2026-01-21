package org.xfurkanadenia.xtuccar.util;

import org.bukkit.Bukkit;
import org.xfurkanadenia.xtuccar.Logger;
import org.xfurkanadenia.xtuccar.XTuccar;
import org.xfurkanadenia.xtuccar.database.DatabaseManager;
import org.xfurkanadenia.xtuccar.database.DatabaseType;

import java.sql.Connection;

public class DatabaseUtils {
    XTuccar plugin;
    private static String SQLiteTable = """
                CREATE TABLE IF NOT EXISTS items (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    seller TEXT NOT NULL,
                    itemid TEXT NOT NULL,
                    amount INTEGER NOT NULL,
                    price REAL NOT NULL,
                    item BLOB NOT NULL,
                    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
                    UNIQUE(seller, itemid)
                );
            """;
    private static String MySQLTable = """
                        CREATE TABLE IF NOT EXISTS `items` (
                         `id` int(11) NOT NULL AUTO_INCREMENT,
                         `seller` text NOT NULL,
                         `itemid` text NOT NULL,
                         `amount` int(11) NOT NULL,
                         `price` double NOT NULL,
                         `item` mediumblob NOT NULL,
                         `createdAt` timestamp NOT NULL DEFAULT current_timestamp(),
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `unique_item` (`seller`, `itemid`)
                        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_turkish_ci;
                       """;

    public DatabaseUtils() {
        this.plugin = XTuccar.getInstance();
    }

    public static void setupDatabase() {
        XTuccar plugin = XTuccar.getInstance();
        try {
            Logger.info("Connecting to database...");

            plugin.setDatabaseManager(new DatabaseManager(
                    DatabaseType.valueOf(plugin.getConfig().getString("database.type")),
                    plugin.getConfig().getString("database.host"),
                    plugin.getConfig().getInt("database.port"),
                    plugin.getConfig().getString("database.tableName"),
                    plugin.getConfig().getString("database.username"),
                    plugin.getConfig().getString("database.password")
            ));

            // Bağlantıyı test et
            Connection conn = plugin.getDatabaseManager().getConnection();
            if (conn != null && !conn.isClosed()) {
                Logger.info("Database connection established.!");
                createTables();

            } else {
                Logger.error("Database connection could not be established!");
                Bukkit.getScheduler().runTask(plugin, () -> {
                    Bukkit.getPluginManager().disablePlugin(plugin);
                });
            }

        } catch (Exception e) {
            Logger.error("error while connecting database: " + e.getMessage());
            e.printStackTrace();

            // Ana thread'e dön ve plugin'i devre dışı bırak
            Bukkit.getScheduler().runTask(plugin, () -> {
                Bukkit.getPluginManager().disablePlugin(plugin);
            });
        }

    }

    private static void createTables() {
        XTuccar plugin = XTuccar.getInstance();
        DatabaseType dbType = DatabaseType.valueOf(plugin.getConfig().getString("database.type"));
        // Tablo oluşturma işlemleri de async'te kalmalı
        try (Connection conn = plugin.getDatabaseManager().getConnection()) {
            if (conn != null) {
                if (dbType == DatabaseType.MYSQL) {
                    conn.createStatement().execute(MySQLTable);
                } else if (dbType == DatabaseType.SQLITE) {
                    conn.createStatement().execute(SQLiteTable);
                }
            }
        } catch (Exception e) {
            Logger.error("Table cannot created: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
