package dev.math3w.playerstash.api.stash;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayerStashManager {
    /**
     * Retrieves the player stash associated with the given player's unique ID.
     *
     * @param playerUniqueId The unique ID of the player.
     * @return The player stash associated with the player's unique ID.
     */
    PlayerStash getPlayerStash(UUID playerUniqueId);

    /**
     * Asynchronously creates a new player stash for the specified player and returns it.
     *
     * @param playerUniqueId The unique ID of the player.
     * @return A CompletableFuture representing the newly created player stash.
     */
    CompletableFuture<PlayerStash> createPlayerStash(UUID playerUniqueId);

    /**
     * Removes a cached player stash associated with the given player's unique ID.
     *
     * @param playerUniqueId The unique ID of the player.
     */
    void removeCachedPlayerStash(UUID playerUniqueId);

    /**
     * Retrieves a collection of cached player stashes.
     *
     * @return A collection of player stashes.
     */
    Collection<? extends PlayerStash> getCachedStashes();
}
