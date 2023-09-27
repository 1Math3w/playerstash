package dev.math3w.playerstash.stash;

import dev.math3w.playerstash.api.stash.PlayerStash;
import dev.math3w.playerstash.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class DatabasePlayerStash implements PlayerStash {
    private final DatabasePlayerStashManager manager;
    private final UUID uuid;
    private final Map<Integer, ItemStack> items;

    public DatabasePlayerStash(DatabasePlayerStashManager manager, UUID uuid) {
        this.manager = manager;
        this.uuid = uuid;

        items = new ConcurrentHashMap<>();
        try (PreparedStatement statement = manager.getDatabase().getConnection().prepareStatement("SELECT * FROM playerstash_items WHERE player=?")) {
            statement.setString(1, uuid.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ItemStack item = deserializeItemStackBase64(resultSet.getString("item"));
                    items.put(resultSet.getInt("id"), item);
                }
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public UUID getPlayerUniqueId() {
        return uuid;
    }

    @Override
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    @Override
    public Collection<ItemStack> getItems() {
        return items.values();
    }

    @Override
    public CompletableFuture<Void> addItem(ItemStack... items) {
        return CompletableFuture.runAsync(() -> {
            for (ItemStack item : items) {
                try (PreparedStatement statement = manager.getDatabase().getConnection().prepareStatement("INSERT INTO playerstash_items (player, item) VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
                    statement.setString(1, uuid.toString());
                    statement.setString(2, serializeItemStackBase64(item));
                    statement.executeUpdate();
                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int id = generatedKeys.getInt(1);
                            this.items.put(id, item);
                        }
                    }
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        });
    }

    @Override
    public CompletableFuture<Integer> claimItems() {
        if (getPlayer() == null) {
            throw new IllegalStateException("Player is not connected to the server");
        }

        return CompletableFuture.supplyAsync(() -> {
            int claimedItems = 0;
            for (Map.Entry<Integer, ItemStack> itemEntry : items.entrySet()) {
                int id = itemEntry.getKey();
                ItemStack item = itemEntry.getValue();

                if (!Utils.canGiveItem(getPlayer(), item)) continue;

                this.items.remove(id);
                try (PreparedStatement removeStatement = manager.getDatabase().getConnection().prepareStatement("DELETE FROM playerstash_items WHERE id=?")) {
                    removeStatement.setInt(1, id);
                    removeStatement.executeUpdate();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }

                CompletableFuture<Boolean> giveFuture = new CompletableFuture<>();
                Bukkit.getScheduler().runTask(manager.getPlugin(), () -> {
                    getPlayer().getInventory().addItem(item);
                    giveFuture.complete(true);
                });

                giveFuture.join();
                claimedItems++;
            }

            return claimedItems;
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    private String serializeItemStackBase64(ItemStack item) throws IllegalStateException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
            dataOutput.writeObject(item);
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to serialize itemstacks", e);
        }
    }

    private ItemStack deserializeItemStackBase64(String data) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
            return (ItemStack) dataInput.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to deserialize itemstacks", e);
        }
    }
}
