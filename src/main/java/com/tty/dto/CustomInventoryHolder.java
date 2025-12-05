package com.tty.dto;

import com.tty.enumType.GuiType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public record CustomInventoryHolder(Player player, Inventory inventory, GuiType type,
                                    Object meta) implements InventoryHolder {

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }
}
