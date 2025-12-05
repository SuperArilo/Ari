package com.tty.dto;

import com.tty.enumType.GuiType;
import com.tty.gui.BaseInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

public record CustomInventoryHolder(Player player, Inventory inventory, GuiType type,
                                    WeakReference<BaseInventory> meta) implements InventoryHolder {

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }
}
