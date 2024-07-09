package ari.superarilo.dto;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class CustomInventoryHolder implements InventoryHolder {
    private final Player player;
    private final String meta;
    public CustomInventoryHolder(Player player, String meta) {
        this.player = player;
        this.meta = meta;
    }
    @Override
    public @NotNull Inventory getInventory() {
        return this.player.getInventory();
    }

    public Player getPlayer() {
        return player;
    }

    public String getMeta() {
        return meta;
    }
}
