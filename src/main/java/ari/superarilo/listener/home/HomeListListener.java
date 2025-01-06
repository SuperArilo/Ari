package ari.superarilo.listener.home;


import ari.superarilo.Ari;
import ari.superarilo.dto.CustomInventoryHolder;
import ari.superarilo.entity.sql.PlayerHome;
import ari.superarilo.enumType.FunctionType;
import ari.superarilo.gui.home.HomeEditor;
import ari.superarilo.mapper.PlayerHomeMapper;
import ari.superarilo.tool.Log;
import ari.superarilo.tool.SQLInstance;
import ari.superarilo.tool.TeleportThread;
import org.apache.ibatis.session.SqlSession;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.logging.Level;


public class HomeListListener implements Listener {
    @EventHandler
    public void HomeListClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getHolder() instanceof CustomInventoryHolder holder) {
            event.setCancelled(true);
            if (event.getSlot() > inventory.getSize()) return;
            ItemStack currentItem = event.getCurrentItem();
            if (currentItem == null) return;
            String type = currentItem.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Ari.instance, "type"), PersistentDataType.STRING);
            if (type != null) {
                FunctionType functionType;
                try {
                    functionType = FunctionType.valueOf(type.toUpperCase());
                } catch (Exception e) {
                    Log.debug(Level.INFO, "Function type error", e);
                    return;
                }
                switch (functionType) {
                    case BACK:
                        inventory.close();
                        break;
                }
            }
            String homeId = currentItem.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Ari.instance, "home_id"), PersistentDataType.STRING);
            if (homeId == null) return;
            try(SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)) {
                PlayerHome home = sqlSession.getMapper(PlayerHomeMapper.class).getHome(homeId);
                ClickType click = event.getClick();
                if (click.equals(ClickType.LEFT)) {
                    inventory.close();
                    new TeleportThread(holder.getPlayer(), new Location(holder.getPlayer().getWorld(), home.getX(), home.getY(), home.getZ()), TeleportThread.Type.POINT).teleport();
                } else if (click.equals(ClickType.RIGHT)) {
                    inventory.close();
                    new HomeEditor(Ari.instance, home,(Player) event.getWhoClicked()).open();
                    sqlSession.close();
                }
            }
        }
    }
}
