package dev.math3w.playerstash.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayerStashAPI {
    /**
     * Gives an item to a player. If the player's inventory is full, the item will be added to their stash.
     *
     * @param player The player to give the item to.
     * @param item   The ItemStack to give to the player.
     * @return The result of the give operation, either INVENTORY or STASH.
     */
    GiveResult giveItem(Player player, ItemStack item);

    /**
     * Gives an item to a player, with an option to send a notification. If the player's inventory is full,
     * the item will be added to their stash.
     *
     * @param player           The player to give the item to.
     * @param item             The ItemStack to give to the player.
     * @param sendNotification Whether to send a notification to the player.
     * @return The result of the give operation, either INVENTORY or STASH.
     */
    GiveResult giveItem(Player player, ItemStack item, boolean sendNotification);

    /**
     * Gives an item to a player with the option to send a notification. If the player's inventory is full,
     * the item will be added to their stash. If the player is offline, the item will be added to their stash.
     *
     * @param playerUniqueId   The unique identifier of the player to give the item to.
     * @param item             The ItemStack to give to the player.
     * @param sendNotification Whether to send a notification if the player is online.
     * @return The result of the give operation, either INVENTORY or STASH. If the player is offline it will always be STASH
     */
    GiveResult giveItem(UUID playerUniqueId, ItemStack item, boolean sendNotification);

    /**
     * Asynchronously claims items from a player's stash and adds them to their inventory.
     *
     * @param playerUniqueId The unique ID of the player.
     * @return A CompletableFuture representing the number of items claimed.
     */
    CompletableFuture<Integer> claimItems(UUID playerUniqueId);

    /**
     * Gets a collection of ItemStacks representing the items stored in a player's stash.
     *
     * @param player The player whose stash items are retrieved.
     * @return A collection of ItemStacks in the player's stash.
     */
    Collection<ItemStack> getStashItems(Player player);

    /**
     * An enum representing the result of a give operation, indicating whether the item was added
     * to the player's inventory or stash.
     */
    enum GiveResult {
        INVENTORY,
        STASH
    }
}
