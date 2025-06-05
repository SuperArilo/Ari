package com.tty.listener.home;

import com.tty.Ari;
import com.tty.dto.CustomInventoryHolder;
import com.tty.entity.sql.ServerHome;
import com.tty.enumType.FilePath;
import com.tty.enumType.FunctionType;
import com.tty.enumType.GuiType;
import com.tty.function.TeleportThread;
import com.tty.gui.home.HomeEditor;
import com.tty.gui.home.HomeList;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;


public class HomeListListener implements Listener {
    @EventHandler
    public void HomeListClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if(inventory.getHolder() instanceof CustomInventoryHolder holder && holder.getType().equals(GuiType.HOMELIST)) {
            event.setCancelled(true);
            if(event.getSlot() > inventory.getSize()) return;
            ItemStack currentItem = event.getCurrentItem();
            if (currentItem == null) return;
            FunctionType type = Ari.instance.objectConvert.ItemNBT_TypeCheck(currentItem.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Ari.instance, "type"), PersistentDataType.STRING));
            if(type == null) return;
            Player player = holder.getPlayer();
            HomeList homeList = (HomeList) holder.getMeta();
            switch (type) {
                case BACK -> inventory.close();
                case DATA -> {
                    String homeId = currentItem.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Ari.instance, "home_id"), PersistentDataType.STRING);
                    if (homeId == null) break;
                    Bukkit.getAsyncScheduler().runNow(Ari.instance, i -> {
                        Optional<ServerHome> first = homeList.data.stream().filter(j -> j.getHomeId().equals(homeId) && j.getPlayerUUID().equals(player.getUniqueId().toString())).findFirst();
                        if(first.isPresent()) {
                            ServerHome home = first.get();
                            ClickType click = event.getClick();
                            if (click.equals(ClickType.LEFT)) {
                                TeleportThread.playerToLocation(
                                                player, Ari.instance.objectConvert.parseLocation(home.getLocation()))
                                        .teleport(Ari.instance.configManager.getValue("main.teleport.delay", FilePath.HomeConfig, Integer.class));
                            } else if (click.equals(ClickType.RIGHT)) {
                                new HomeEditor(home,(Player) event.getWhoClicked()).open();
                            }
                        }
                        Bukkit.getRegionScheduler().run(Ari.instance, player.getLocation(), o -> inventory.close());
                    });
                }
                case PREV -> homeList.prev();
                case NEXT -> homeList.next();
            }
        }
    }
}
