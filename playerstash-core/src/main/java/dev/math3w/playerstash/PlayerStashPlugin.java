package dev.math3w.playerstash;

import dev.math3w.playerstash.api.GiveResult;
import dev.math3w.playerstash.api.PlayerStashAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public final class PlayerStashPlugin extends JavaPlugin implements PlayerStashAPI {
    @Override
    public void onEnable() {

    }

    @Override
    public GiveResult giveItem(Player player, ItemStack item) {
        return giveItem(player.getUniqueId(), item);
    }

    @Override
    public GiveResult giveItem(UUID playerUniqueId, ItemStack item) {
        return null;
    }
}
