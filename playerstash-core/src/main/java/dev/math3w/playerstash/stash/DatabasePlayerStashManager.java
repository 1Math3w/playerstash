package dev.math3w.playerstash.stash;

import dev.math3w.playerstash.PlayerStashPlugin;
import dev.math3w.playerstash.api.stash.PlayerStashManager;
import dev.math3w.playerstash.configs.configs.DatabaseConfig;
import dev.math3w.playerstash.databases.MySQLDatabase;
import dev.math3w.playerstash.databases.SQLDatabase;
import dev.math3w.playerstash.databases.SQLiteDatabase;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class DatabasePlayerStashManager implements PlayerStashManager {
    private final PlayerStashPlugin plugin;
    private final SQLDatabase database;

    public DatabasePlayerStashManager(PlayerStashPlugin plugin, DatabaseConfig databaseConfig) {
        this.plugin = plugin;

        SQLDatabase.Type sqlType = databaseConfig.getSqlType();
        if (sqlType == SQLDatabase.Type.MYSQL) {
            database = new MySQLDatabase(databaseConfig.getHostname(), databaseConfig.getPort(), databaseConfig.getDatabase(), databaseConfig.getUsername(), databaseConfig.getPassword());
        } else {
            database = new SQLiteDatabase(plugin.getDataFolder(), "playerstash");
        }

        createTable();
    }

    private void createTable() {
        String autoIncrementSyntax = database.getType() == SQLDatabase.Type.SQLITE ? "AUTOINCREMENT" : "AUTO_INCREMENT";

        try (PreparedStatement statement = database.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS playerstash_items (" +
                "id INTEGER PRIMARY KEY " + autoIncrementSyntax + ", " +
                "player VARCHAR(36) NOT NULL, " +
                "item VARCHAR(100000) NOT NULL);")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DatabasePlayerStash getPlayerStash(UUID playerUniqueId) {
        return new DatabasePlayerStash(this, playerUniqueId);
    }

    public PlayerStashPlugin getPlugin() {
        return plugin;
    }

    public SQLDatabase getDatabase() {
        return database;
    }
}
