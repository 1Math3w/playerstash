package dev.math3w.playerstash.api.stash;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayerStash {
    UUID getPlayerUniqueId();

    Player getPlayer();

    Collection<ItemStack> getItems();

    void addItem(ItemStack... items);

    CompletableFuture<Integer> claimItems();
}
