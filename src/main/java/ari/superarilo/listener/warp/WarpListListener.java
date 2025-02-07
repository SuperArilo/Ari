package ari.superarilo.listener.warp;

import ari.superarilo.dto.CustomInventoryHolder;
import ari.superarilo.enumType.GuiType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class WarpListListener implements Listener {
    @EventHandler
    public void warpListClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if(inventory.getHolder() instanceof CustomInventoryHolder holder && holder.getType().equals(GuiType.WARPLIST)) {
            event.setCancelled(true);
        }
    }
}
