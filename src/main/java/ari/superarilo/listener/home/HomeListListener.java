package ari.superarilo.listener.home;


import ari.superarilo.Ari;
import ari.superarilo.dto.CustomInventoryHolder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;


public class HomeListListener implements Listener {
    @EventHandler
    public void HomeListClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getHolder() instanceof CustomInventoryHolder holder) {
            event.setCancelled(true);
        }
    }
}
