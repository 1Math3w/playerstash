package dev.math3w.playerstash.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Utils {
    private Utils() {
        throw new IllegalStateException("Utility class cannot be instantiated");
    }

    public static boolean hasFreeSlot(Player player) {
        return player.getInventory().firstEmpty() != -1;
    }

    public static boolean canGiveItem(Player player, ItemStack item) {
        Inventory inventory = player.getInventory();

        int remainingAmount = item.getAmount();

        if (remainingAmount == 0) return true;

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack slotItem = inventory.getItem(i);
            if (slotItem != null && !slotItem.isSimilar(item)) continue;

            int slotAvailableAmount = item.getMaxStackSize() - (slotItem == null ? 0 : slotItem.getAmount());
            if (slotAvailableAmount <= 0) continue;

            if (slotAvailableAmount >= remainingAmount) {
                remainingAmount = 0;
            } else {
                remainingAmount -= slotAvailableAmount;
            }

            if (remainingAmount == 0) {
                return true;
            }
        }

        return false;
    }
}
