package dev.math3w.playerstash.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface PlayerStashAPI {
    GiveResult giveItem(Player player, ItemStack item);

    GiveResult giveItem(UUID playerUniqueId, ItemStack item);
}
