package ari.superarilo.dto;

import ari.superarilo.enumType.GuiType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class CustomInventoryHolder implements InventoryHolder {
    private final Player player;
    private final GuiType type;

    public CustomInventoryHolder(Player player, GuiType type) {
        this.player = player;
        this.type = type;
    }

    public Player getPlayer() {
        return player;
    }

    public GuiType getType() {
        return type;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.player.getOpenInventory().getTopInventory();
    }
}
