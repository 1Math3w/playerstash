package dev.math3w.playerstash.api.stash;

import java.util.UUID;

public interface PlayerStashManager {
    PlayerStash getPlayerStash(UUID playerUniqueId);
}
