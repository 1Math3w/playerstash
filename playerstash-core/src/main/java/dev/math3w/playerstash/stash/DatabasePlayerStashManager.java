package dev.math3w.playerstash.stash;

import dev.math3w.playerstash.PlayerStashPlugin;
import dev.math3w.playerstash.api.stash.PlayerStash;
import dev.math3w.playerstash.api.stash.PlayerStashManager;
import dev.math3w.playerstash.configs.configs.DatabaseConfig;
import dev.math3w.playerstash.databases.MySQLDatabase;
import dev.math3w.playerstash.databases.SQLDatabase;
import dev.math3w.playerstash.databases.SQLiteDatabase;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DatabasePlayerStashManager implements PlayerStashManager {
    private final PlayerStashPlugin plugin;
    private final SQLDatabase database;
    private final Map<UUID, DatabasePlayerStash> playerStashes = new HashMap<>();

    public DatabasePlayerStashManager(PlayerStashPlugin plugin, DatabaseConfig databaseConfig) {
        this.plugin = plugin;

        SQLDatabase.Type sqlType = databaseConfig.getSqlType();
        if (sqlType == SQLDatabase.Type.MYSQL) {
            database = new MySQLDatabase(databaseConfig.getHostname(), databaseConfig.getPort(), databaseConfig.getDatabase(), databaseConfig.getUsername(), databaseConfig.getPassword());
        } else {
            database = new SQLiteDatabase(plugin.getDataFolder(), "playerstash");
        }

        createTable();
        Bukkit.getPluginManager().registerEvents(new PlayerStashListeners(this), plugin);
    }

    private void createTable() {
        String autoIncrementSyntax = database.getType() == SQLDatabase.Type.SQLITE ? "AUTOINCREMENT" : "AUTO_INCREMENT";

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (PreparedStatement statement = database.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS playerstash_items (" +
                    "id INTEGER PRIMARY KEY " + autoIncrementSyntax + ", " +
                    "player VARCHAR(36) NOT NULL, " +
                    "item TEXT NOT NULL);")) {
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public DatabasePlayerStash getPlayerStash(UUID playerUniqueId) {
        return playerStashes.get(playerUniqueId);
    }

    @Override
    public CompletableFuture<PlayerStash> createPlayerStash(UUID playerUniqueId) {
        return CompletableFuture.supplyAsync(() -> {
            DatabasePlayerStash playerStash = new DatabasePlayerStash(this, playerUniqueId);
            playerStashes.put(playerUniqueId, playerStash);
            return playerStash;
        });
    }

    @Override
    public void removeCachedPlayerStash(UUID playerUniqueId) {
        playerStashes.remove(playerUniqueId);
    }

    @Override
    public Collection<? extends PlayerStash> getCachedStashes() {
        return playerStashes.values();
    }

    public PlayerStashPlugin getPlugin() {
        return plugin;
    }

    public SQLDatabase getDatabase() {
        return database;
    }
}
