package ari.superarilo.listener.home;

import ari.superarilo.Ari;
import ari.superarilo.dto.CustomInventoryHolder;
import ari.superarilo.enumType.FunctionType;
import ari.superarilo.enumType.GuiType;
import ari.superarilo.gui.home.HomeList;
import org.bukkit.NamespacedKey;
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
        CustomInventoryHolder holder = (CustomInventoryHolder) event.getInventory().getHolder();
        if(holder == null || !holder.getType().equals(GuiType.EDITHOME)) return;
        Inventory inventory = holder.getInventory();
        if(event.getSlot() > inventory.getSize()) return;
        event.setCancelled(true);
        ItemStack clickItem = event.getCurrentItem();
        if(clickItem == null) return;
        ItemMeta clickItemMeta = clickItem.getItemMeta();
        FunctionType type = Ari.instance.objectConvert.ItemNBT_TypeCheck(clickItemMeta.getPersistentDataContainer().get(new NamespacedKey(Ari.instance, "type"), PersistentDataType.STRING));
        if (type == null) return;
        switch (type) {
            case REBACK -> {
                inventory.close();
                new HomeList(holder.getPlayer()).open();
            }
            case DELETE -> {
                //delete home
            }
            case RENAME -> {
                //rename home
            }
            case LOCATION -> {
                //reset LOCATION
            }
        }
    }
}
