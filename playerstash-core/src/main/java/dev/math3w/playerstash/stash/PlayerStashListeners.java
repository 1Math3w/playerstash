package dev.math3w.playerstash.stash;

import dev.math3w.playerstash.api.stash.PlayerStashManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerStashListeners implements Listener {
    private final PlayerStashManager playerStashManager;

    public PlayerStashListeners(PlayerStashManager playerStashManager) {
        this.playerStashManager = playerStashManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        playerStashManager.createPlayerStash(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        playerStashManager.removeCachedPlayerStash(event.getPlayer().getUniqueId());
    }
}
