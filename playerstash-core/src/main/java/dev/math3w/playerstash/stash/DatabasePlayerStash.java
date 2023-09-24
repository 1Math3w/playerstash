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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DatabasePlayerStash implements PlayerStash {
    private final DatabasePlayerStashManager manager;
    private final UUID uuid;

    public DatabasePlayerStash(DatabasePlayerStashManager manager, UUID uuid) {
        this.manager = manager;
        this.uuid = uuid;
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
    public CompletableFuture<List<ItemStack>> getItems() {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement statement = manager.getDatabase().getConnection().prepareStatement("SELECT * FROM playerstash_items WHERE player=?")) {
                statement.setString(1, uuid.toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    List<ItemStack> items = new ArrayList<>();
                    while (resultSet.next()) {
                        ItemStack item = deserializeItemStackBase64(resultSet.getString("item"));
                        items.add(item);
                    }
                    return items;
                } catch (IOException exception) {
                    throw new RuntimeException(exception);
                }
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    @Override
    public void addItem(ItemStack... items) {
        CompletableFuture.runAsync(() -> {
            for (ItemStack item : items) {
                try (PreparedStatement statement = manager.getDatabase().getConnection().prepareStatement("INSERT INTO playerstash_items (player, item) VALUES (?, ?)")) {
                    statement.setString(1, uuid.toString());
                    statement.setString(2, serializeItemStackBase64(item));
                    statement.executeUpdate();
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
            try (PreparedStatement statement = manager.getDatabase().getConnection().prepareStatement("SELECT * FROM playerstash_items WHERE player=?")) {
                statement.setString(1, uuid.toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    int claimedItems = 0;
                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        ItemStack item = deserializeItemStackBase64(resultSet.getString("item"));

                        if (!Utils.hasFreeSlot(getPlayer())) break;

                        CompletableFuture<Boolean> giveFuture = new CompletableFuture<>();
                        Bukkit.getScheduler().runTask(manager.getPlugin(), () -> {
                            getPlayer().getInventory().addItem(item);
                            giveFuture.complete(true);
                        });

                        giveFuture.join();

                        try (PreparedStatement removeStatement = manager.getDatabase().getConnection().prepareStatement("DELETE FROM playerstash_items WHERE id=?")) {
                            removeStatement.setInt(1, id);
                            removeStatement.executeUpdate();
                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        }

                        claimedItems++;
                    }

                    return claimedItems;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            return 0;
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
