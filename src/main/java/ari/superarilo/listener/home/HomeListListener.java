package ari.superarilo.listener.home;

import ari.superarilo.Ari;
import ari.superarilo.dto.CustomInventoryHolder;
import ari.superarilo.entity.sql.PlayerHome;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.enumType.FunctionType;
import ari.superarilo.enumType.GuiType;
import ari.superarilo.function.TeleportThread;
import ari.superarilo.gui.home.HomeEditor;
import ari.superarilo.mapper.PlayerHomeMapper;
import ari.superarilo.tool.SQLInstance;
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

public class HomeListListener implements Listener {
    @EventHandler
    public void HomeListClick(InventoryClickEvent event) {
        Inventory inventory = event.getView().getTopInventory();
        if(inventory.getHolder() instanceof CustomInventoryHolder holder && holder.getType().equals(GuiType.HOMELIST)) {
            event.setCancelled(true);
            if(event.getSlot() > inventory.getSize()) return;
            ItemStack currentItem = event.getCurrentItem();
            if (currentItem == null) return;
            FunctionType type = Ari.instance.objectConvert.ItemNBT_TypeCheck(currentItem.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Ari.instance, "type"), PersistentDataType.STRING));
            if(type == null) return;
            Player player = holder.getPlayer();
            switch (type) {
                case BACK:
                    inventory.close();
                    break;
                case DATA:
                    String homeId = currentItem.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Ari.instance, "home_id"), PersistentDataType.STRING);
                    if (homeId == null) break;
                    try(SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)) {
                        PlayerHome home = sqlSession.getMapper(PlayerHomeMapper.class).getHome(homeId, player.getUniqueId().toString());
                        ClickType click = event.getClick();
                        if (click.equals(ClickType.LEFT)) {
                            TeleportThread.playerToLocation(
                                            player,
                                    new Location(player.getWorld(), home.getX(), home.getY(), home.getZ()))
                                    .teleport(Ari.instance.configManager.getValue("main.teleport.delay", FilePath.HomeConfig, Integer.class));
                        } else if (click.equals(ClickType.RIGHT)) {
                            new HomeEditor(home,(Player) event.getWhoClicked()).open();
                        }
                        inventory.close();
                    }
            }
        }
    }
}
