package ari.superarilo.listener.home;

import ari.superarilo.Ari;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.logging.Level;

public class HomeListListener implements Listener {
    @EventHandler
    public void HomeListClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        InventoryHolder holder = clickedInventory.getHolder();
    }
}
