package ari.superarilo.listener.warp;

import ari.superarilo.Ari;
import ari.superarilo.dto.CustomInventoryHolder;
import ari.superarilo.entity.sql.ServerWarp;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.enumType.FunctionType;
import ari.superarilo.enumType.GuiType;
import ari.superarilo.function.TeleportThread;
import ari.superarilo.gui.warp.WarpEditor;
import ari.superarilo.gui.warp.WarpList;
import ari.superarilo.tool.Log;
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

import java.util.List;
import java.util.Optional;


public class WarpListListener implements Listener {
    @EventHandler
    public void warpListClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if(inventory.getHolder() instanceof CustomInventoryHolder holder && holder.getType().equals(GuiType.WARPLIST)) {
            event.setCancelled(true);
            if(event.getSlot() >= inventory.getSize()) return;
            ItemStack currentItem = event.getCurrentItem();
            if (currentItem == null) return;
            FunctionType type = Ari.instance.objectConvert.ItemNBT_TypeCheck(
                    currentItem
                            .getItemMeta()
                            .getPersistentDataContainer()
                            .get(new NamespacedKey(Ari.instance, "type"), PersistentDataType.STRING));
            if(type == null) return;
            Player player = holder.getPlayer();
            WarpList warpList = (WarpList) holder.getMeta();
            switch (type) {
                case BACK -> inventory.close();
                case DATA -> {
                    String warpId = currentItem.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Ari.instance, "warp_id"), PersistentDataType.STRING);
                    if(warpId == null) break;
                    Bukkit.getAsyncScheduler().runNow(Ari.instance, i -> {
                        List<ServerWarp> serverWarpList = warpList.getWarpList();
                        Optional<ServerWarp> first = serverWarpList.stream().filter(j -> j.getWarpId().equals(warpId) && j.getCreateBy().equals(player.getUniqueId().toString())).findFirst();
                        if(first.isPresent()) {
                            ServerWarp warp = first.get();
                            ClickType eventClick = event.getClick();
                            if(eventClick.equals(ClickType.LEFT)) {
                                TeleportThread.playerToLocation(
                                                player,
                                                Ari.instance.objectConvert.parseLocation(warp.getLocation()))
                                        .teleport(Ari.instance.configManager.getValue("main.teleport.delay", FilePath.WarpConfig, Integer.class));
                            } else if(eventClick.equals(ClickType.RIGHT)) {
                                new WarpEditor(warp, player).open();
                            }
                        } else {
                            Log.error("can't find warpId: " + warpId);
                        }
                        Bukkit.getRegionScheduler().run(Ari.instance, player.getLocation(), j -> inventory.close());
                    });
                }
            }
        }
    }
}
