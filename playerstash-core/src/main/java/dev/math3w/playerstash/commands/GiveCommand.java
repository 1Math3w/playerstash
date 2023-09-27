package dev.math3w.playerstash.commands;

import dev.math3w.playerstash.PlayerStashPlugin;
import dev.math3w.playerstash.api.PlayerStashAPI;
import dev.math3w.playerstash.utils.ItemSerializationUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class GiveCommand implements CommandExecutor {
    private final PlayerStashPlugin plugin;

    public GiveCommand(PlayerStashPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "You need to define item /stashgive <player> <item>");
            return true;
        }

        String playerName = args[0];
        String itemType = args[1];

        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "This player is not online");
            return true;
        }

        if (sender instanceof Player && itemType.equalsIgnoreCase("-hand")) {
            Player senderPlayer = (Player) sender;
            ItemStack item = senderPlayer.getInventory().getItemInMainHand();
            if (args.length >= 3 && args[2].equalsIgnoreCase("-s")) {
                Bukkit.getConsoleSender().sendMessage(ItemSerializationUtils.serializeItemStackToJson(item));
            }
            giveItem(sender, player, item);
            return true;
        }

        if (itemType.startsWith("{")) {
            ItemStack item = ItemSerializationUtils.deserializeItemStackFromJson(itemType);
            giveItem(sender, player, item);
            return true;
        }

        try {
            itemType = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            Material itemMaterial = Material.valueOf(itemType.toUpperCase().replaceAll(" ", "_"));
            ItemStack item = new ItemStack(itemMaterial);
            giveItem(sender, player, item);
        } catch (IllegalArgumentException exception) {
            sender.sendMessage(ChatColor.RED + "Invalid item!");
        }

        return true;
    }

    private void giveItem(CommandSender sender, Player player, ItemStack item) {
        PlayerStashAPI.GiveResult giveResult = plugin.giveItem(player, item);
        String name = item.getItemMeta() != null && item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType().name().toLowerCase().replaceAll("_", " ");
        sender.sendMessage(ChatColor.GREEN + "Gived " + player.getName() + " item " + ChatColor.WHITE + name + ChatColor.GREEN + " to " + giveResult.name().toLowerCase());
    }
}
