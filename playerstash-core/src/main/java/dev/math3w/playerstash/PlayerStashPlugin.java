package dev.math3w.playerstash;

import dev.math3w.playerstash.api.PlayerStashAPI;
import dev.math3w.playerstash.api.stash.PlayerStashManager;
import dev.math3w.playerstash.configs.configs.DatabaseConfig;
import dev.math3w.playerstash.stash.DatabasePlayerStashManager;
import dev.math3w.playerstash.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public final class PlayerStashPlugin extends JavaPlugin implements PlayerStashAPI {
    private PlayerStashManager playerStashManager;

    @Override
    public void onEnable() {
        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.loadConfiguration(this);

        playerStashManager = new DatabasePlayerStashManager(this, databaseConfig);
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
            return GiveResult.INVENTORY;
        }

        playerStashManager.getPlayerStash(playerUniqueId).addItem();
        return GiveResult.STASH;
    }
}
