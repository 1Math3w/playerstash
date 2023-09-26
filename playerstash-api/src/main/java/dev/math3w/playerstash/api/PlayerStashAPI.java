package dev.math3w.playerstash.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayerStashAPI {
    GiveResult giveItem(Player player, ItemStack item);

    GiveResult giveItem(Player player, ItemStack item, boolean sendNotification);

    CompletableFuture<Integer> claimItems(UUID playerUniqueId);

    Collection<ItemStack> getStashItems(Player player);

    enum GiveResult {
        INVENTORY,
        STASH
    }
}
