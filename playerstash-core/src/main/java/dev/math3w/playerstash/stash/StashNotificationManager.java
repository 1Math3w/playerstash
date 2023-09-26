package dev.math3w.playerstash.stash;

import dev.math3w.playerstash.PlayerStashPlugin;
import dev.math3w.playerstash.api.stash.PlayerStash;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class StashNotificationManager {
    private final PlayerStashPlugin plugin;
    private final BukkitTask task;

    public StashNotificationManager(PlayerStashPlugin plugin) {
        this.plugin = plugin;
        this.task = runTask();
    }

    private BukkitTask runTask() {
        return Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (PlayerStash playerStash : plugin.getPlayerStashManager().getCachedStashes()) {
                Player player = playerStash.getPlayer();
                if (player == null) continue;
                if (playerStash.getItems().isEmpty()) continue;

                sendNotification(playerStash, player);
            }
        }, 0, plugin.getMessagesConfig().getNotificationPeriod());
    }

    public void sendNotification(PlayerStash playerStash, Player player) {
        player.spigot().sendMessage(plugin.getMessagesConfig().getNotification(playerStash.getItems().size()));
    }
}
