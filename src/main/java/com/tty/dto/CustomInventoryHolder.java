package com.tty.dto;

import com.tty.enumType.GuiType;
import com.tty.lib.task.CancellableTask;
import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

@Data
public class CustomInventoryHolder implements InventoryHolder {
    private final Player player;
    private final GuiType type;
    private final Object meta;
    private CancellableTask task;

    public CustomInventoryHolder(Player player, GuiType type, Object meta) {
        this.player = player;
        this.type = type;
        this.meta = meta;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.player.getInventory();
    }
}
