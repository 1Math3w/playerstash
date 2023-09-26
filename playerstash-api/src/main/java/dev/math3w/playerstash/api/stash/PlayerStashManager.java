package dev.math3w.playerstash.api.stash;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayerStashManager {
    PlayerStash getPlayerStash(UUID playerUniqueId);

    CompletableFuture<PlayerStash> createPlayerStash(UUID playerUniqueId);

    void removeCachedPlayerStash(UUID playerUniqueId);

    Collection<? extends PlayerStash> getCachedStashes();
}
