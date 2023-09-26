package dev.math3w.playerstash;

import dev.math3w.playerstash.api.PlayerStashAPI;
import dev.math3w.playerstash.api.stash.PlayerStashManager;
import dev.math3w.playerstash.configs.configs.DatabaseConfig;
import dev.math3w.playerstash.configs.configs.MessagesConfig;
import dev.math3w.playerstash.stash.DatabasePlayerStashManager;
import dev.math3w.playerstash.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class PlayerStashPlugin extends JavaPlugin implements PlayerStashAPI {
    private PlayerStashManager playerStashManager;
    private MessagesConfig messagesConfig;

    @Override
    public void onEnable() {
        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.loadConfiguration(this);

        messagesConfig = new MessagesConfig();
        messagesConfig.loadConfiguration(this);

        playerStashManager = new DatabasePlayerStashManager(this, databaseConfig);

        for (Player player : Bukkit.getOnlinePlayers()) {
            playerStashManager.createPlayerStash(player.getUniqueId());
        }
    }

    @Override
    public GiveResult giveItem(Player player, ItemStack item) {
        return giveItem(player.getUniqueId(), item);
    }

    @Override
    public GiveResult giveItem(UUID playerUniqueId, ItemStack item) {
        Player player = Bukkit.getPlayer(playerUniqueId);
        if (player == null) {
            throw new IllegalStateException("Player is not connected to the server");
        }

        if (Utils.canGiveItem(player, item)) {
            player.getInventory().addItem(item);
            return GiveResult.INVENTORY;
        }

        playerStashManager.getPlayerStash(playerUniqueId).addItem(item);
        return GiveResult.STASH;
    }

    @Override
    public CompletableFuture<Integer> claimItems(UUID playerUniqueId) {
        return playerStashManager.getPlayerStash(playerUniqueId).claimItems();
    }

    @Override
    public Collection<ItemStack> getStashItems(Player player) {
        return playerStashManager.getPlayerStash(player.getUniqueId()).getItems();
    }

    public MessagesConfig getMessagesConfig() {
        return messagesConfig;
    }

    public PlayerStashManager getPlayerStashManager() {
        return playerStashManager;
    }
}
