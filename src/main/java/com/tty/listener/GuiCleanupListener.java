package com.tty.listener;

import com.tty.dto.CustomInventoryHolder;
import com.tty.gui.BaseInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryView;

import java.lang.ref.WeakReference;

public class GuiCleanupListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        var inv = event.getInventory();
        if (inv.getHolder() instanceof CustomInventoryHolder holder) {
            Object meta = holder.meta();
            if (meta instanceof WeakReference<?> wr) {
                Object o = wr.get();
                if (o instanceof BaseInventory bi) {
                    bi.cleanup();
                }
            } else if (meta instanceof BaseInventory bi) {
                bi.cleanup();
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        InventoryView view = player.getOpenInventory();
        var top = view.getTopInventory();
        if (top.getHolder() instanceof CustomInventoryHolder holder) {
            Object meta = holder.meta();
            if (meta instanceof WeakReference<?> wr) {
                Object o = wr.get();
                if (o instanceof BaseInventory bi) {
                    bi.cleanup();
                }
            } else if (meta instanceof BaseInventory bi) {
                bi.cleanup();
            }
        }
    }
}
