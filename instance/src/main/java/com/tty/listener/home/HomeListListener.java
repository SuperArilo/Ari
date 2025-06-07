package com.tty.listener.home;

import com.tty.Ari;
import com.tty.dto.CustomInventoryHolder;
import com.tty.entity.sql.ServerHome;
import com.tty.enumType.FilePath;
import com.tty.lib.enum_type.FunctionType;
import com.tty.enumType.GuiType;
import com.tty.function.TeleportThread;
import com.tty.gui.home.HomeEditor;
import com.tty.gui.home.HomeList;
import com.tty.lib.Lib;
import com.tty.tool.ConfigObjectUtils;
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
            FunctionType type = ConfigObjectUtils.ItemNBT_TypeCheck(currentItem.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Ari.instance, "type"), PersistentDataType.STRING));
            if(type == null) return;
            Player player = holder.getPlayer();
            HomeList homeList = (HomeList) holder.getMeta();
            switch (type) {
                case BACK -> inventory.close();
                case DATA -> {
                    String homeId = currentItem.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Ari.instance, "home_id"), PersistentDataType.STRING);
                    if (homeId == null) break;
                    Lib.Scheduler.runAsync(Ari.instance, i -> {
                        Optional<ServerHome> first = homeList.data.stream().filter(j -> j.getHomeId().equals(homeId) && j.getPlayerUUID().equals(player.getUniqueId().toString())).findFirst();
                        if(first.isPresent()) {
                            ServerHome home = first.get();
                            ClickType click = event.getClick();
                            if (click.equals(ClickType.LEFT)) {
                                TeleportThread.playerToLocation(
                                                player, ConfigObjectUtils.parseLocation(home.getLocation()))
                                        .teleport(ConfigObjectUtils.getValue("main.teleport.delay", FilePath.Lang.getName(), Integer.class));
                            } else if (click.equals(ClickType.RIGHT)) {
                                Lib.Scheduler.run(Ari.instance, l -> inventory.close());
                                new HomeEditor(home,(Player) event.getWhoClicked()).open();
                            }
                        }
                        Lib.Scheduler.runAtRegion(Ari.instance, player.getLocation(), o -> inventory.close());
                    });
                }
                case PREV -> homeList.prev();
                case NEXT -> homeList.next();
            }
        }
    }
}
