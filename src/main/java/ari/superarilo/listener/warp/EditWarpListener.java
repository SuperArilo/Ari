package ari.superarilo.listener.warp;

import ari.superarilo.dto.CustomInventoryHolder;
import ari.superarilo.enumType.GuiType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class EditWarpListener implements Listener {

    @EventHandler
    public void editClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if(inventory == null || event.getSlot() > inventory.getSize()) return;
        if(!(inventory.getHolder() instanceof CustomInventoryHolder) && (event.getClick().equals(ClickType.SHIFT_RIGHT) || event.getClick().equals(ClickType.SHIFT_LEFT))) {
            event.setCancelled(true);
            return;
        }
        if(!(inventory.getHolder() instanceof CustomInventoryHolder holder && holder.getType().equals(GuiType.WARPEDIT))) return;
        event.setCancelled(true);
    }
}
