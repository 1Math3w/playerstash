package dev.math3w.playerstash.api.stash;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayerStash {
    /**
     * Gets the unique ID of the player associated with this stash.
     *
     * @return The unique ID of the player.
     */
    UUID getPlayerUniqueId();

    /**
     * Gets the player associated with this stash.
     *
     * @return The player associated with this stash, or null if the player is not online.
     */
    Player getPlayer();

    /**
     * Retrieves a collection of ItemStacks representing the items stored in the stash.
     *
     * @return A collection of ItemStacks in the stash.
     */
    Collection<ItemStack> getItems();

    /**
     * Asynchronously adds one or more ItemStacks to the stash.
     *
     * @param items The ItemStacks to add to the stash.
     * @return A CompletableFuture representing the completion of the operation.
     */
    CompletableFuture<Void> addItem(ItemStack... items);

    /**
     * Asynchronously claims items from the stash and adds them to the player's inventory.
     *
     * @return A CompletableFuture representing the number of items claimed.
     */
    CompletableFuture<Integer> claimItems();
}
