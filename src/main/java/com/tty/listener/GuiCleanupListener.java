package com.tty.listener;

import com.tty.dto.CustomInventoryHolder;
import com.tty.gui.BaseInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.lang.ref.WeakReference;

public class GuiCleanupListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        this.clean(event.getInventory());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        InventoryView view = player.getOpenInventory();
        this.clean(view.getTopInventory());
    }

    private void clean(Inventory inv) {
        if (inv.getHolder() instanceof CustomInventoryHolder holder) {
            Object meta = holder.meta();
            if (meta instanceof WeakReference<?> wr) {
                Object o = wr.get();
                if (o instanceof BaseInventory bi) {
                    bi.cleanup();
                }
            }
        }
    }
}
