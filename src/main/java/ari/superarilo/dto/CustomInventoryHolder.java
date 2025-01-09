package ari.superarilo.dto;

import ari.superarilo.enumType.GuiType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class CustomInventoryHolder implements InventoryHolder {
    private final Player player;
    private final GuiType type;
    private final Object meta;

    public CustomInventoryHolder(Player player, GuiType type, Object meta) {
        this.player = player;
        this.type = type;
        this.meta = meta;
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

    public Object getMeta() {
        return meta;
    }
}
