package dev.math3w.playerstash.commands;

import dev.math3w.playerstash.PlayerStashPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PickupCommand implements CommandExecutor {
    private final PlayerStashPlugin plugin;

    public PickupCommand(PlayerStashPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        if (plugin.getStashItems(player).isEmpty()) {
            player.sendMessage(plugin.getMessagesConfig().getEmptyStash());
            return true;
        }
        
        plugin.claimItems(player.getUniqueId()).thenAccept(claimed -> {
            if (claimed <= 0) {
                player.sendMessage(plugin.getMessagesConfig().getFullInventory());
                return;
            }
            player.sendMessage(plugin.getMessagesConfig().getClaimed(claimed));
        });

        return true;
    }
}
