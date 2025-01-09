package ari.superarilo.listener.home;

import ari.superarilo.Ari;
import ari.superarilo.dto.CustomInventoryHolder;
import ari.superarilo.entity.sql.PlayerHome;
import ari.superarilo.enumType.FunctionType;
import ari.superarilo.enumType.GuiType;
import ari.superarilo.function.HomeManager;
import ari.superarilo.gui.home.HomeList;
import ari.superarilo.tool.Log;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class EditHomeListener implements Listener {
    @EventHandler
    public void editGuiClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if(inventory == null || event.getSlot() > inventory.getSize()) return;
        if(inventory.getHolder() instanceof CustomInventoryHolder holder && holder.getType().equals(GuiType.EDITHOME)) {
            ItemStack clickItem = event.getCurrentItem();

            //当拖起物品点击的地方为null取消操作
            if(clickItem == null) {
                event.setCancelled(true);
                return;
            }
            ItemMeta clickMeta = clickItem.getItemMeta();
            FunctionType type = Ari.instance.objectConvert.ItemNBT_TypeCheck(clickMeta.getPersistentDataContainer().get(new NamespacedKey(Ari.instance, "type"), PersistentDataType.STRING));
            event.setCancelled(true);
            Player player = holder.getPlayer();
            switch (type) {
                case REBACK -> {
                    inventory.close();
                    new HomeList(player).open();
                }
                case DELETE -> {
                    //delete home
                    PlayerHome home = (PlayerHome) holder.getMeta();
                    Integer i = HomeManager.create(player).deleteHome(home.getHomeId());
                    Log.debug(i.toString());
                    inventory.close();
                    new HomeList(player).open();
                }
                case RENAME -> {
                    Log.debug(clickItem.getType().name());
                    //rename home
                }
                case LOCATION -> {
                    //reset LOCATION
                }
                case ICON -> {
                    clickItem = new ItemStack(event.getCursor().getType());
                    clickItem.setItemMeta(clickMeta);
                    inventory.setItem(event.getSlot(), clickItem);
                }
            }
        }
    }
}
