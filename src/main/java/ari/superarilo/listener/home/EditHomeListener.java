package ari.superarilo.listener.home;

import ari.superarilo.Ari;
import ari.superarilo.dto.CustomInventoryHolder;
import ari.superarilo.entity.sql.PlayerHome;
import ari.superarilo.enumType.FunctionType;
import ari.superarilo.enumType.GuiType;
import ari.superarilo.function.HomeManager;
import ari.superarilo.gui.home.HomeList;
import ari.superarilo.tool.Log;
import ari.superarilo.tool.TextTool;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
            PlayerHome home = (PlayerHome) holder.getMeta();
            HomeManager homeManager = HomeManager.create(player);
            switch (type) {
                case REBACK -> {
                    inventory.close();
                    new HomeList(player).open();
                }
                case DELETE -> {
                    //delete home
                    homeManager.deleteHome(home.getHomeId());
                    inventory.close();
                    new HomeList(player).open();
                }
                case RENAME -> {
                    Log.debug(clickItem.getType().name());
                    //rename home
                }
                case LOCATION -> {
                    //reset LOCATION
                    Location newLocation = player.getLocation();
                    home.setX(Double.valueOf(Ari.instance.numberFormatUtil.format_2(newLocation.getX())));
                    home.setY(Double.valueOf(Ari.instance.numberFormatUtil.format_2(newLocation.getY())));
                    home.setZ(Double.valueOf(Ari.instance.numberFormatUtil.format_2(newLocation.getZ())));
                    clickMeta.displayName(TextTool.setHEXColorText(TextTool.XYZText(home.getX(), home.getY(), home.getZ())));
                    clickItem.setItemMeta(clickMeta);
                }
                case ICON -> {
                    //修改显示ICON
                    Material curM = event.getCursor().getType();
                    if(curM.equals(Material.AIR)) return;
                    clickItem = new ItemStack(curM);
                    clickItem.setItemMeta(clickMeta);
                    inventory.setItem(event.getSlot(), clickItem);
                    home.setShowMaterial(curM.name());
                }
                case SAVE -> {
                    //save
                    ItemStack finalClickItem = clickItem;
                    Log.debug("start saving home");
                    clickMeta.lore(List.of(TextTool.setHEXColorText("&7保存中...")));
                    finalClickItem.setItemMeta(clickMeta);
                    Bukkit.getAsyncScheduler().runNow(Ari.instance, o -> {
                        if(homeManager.modifyHome(home)) {
                            clickMeta.lore(List.of(TextTool.setHEXColorText("&a保存成功")));
                            finalClickItem.setItemMeta(clickMeta);
                            Bukkit.getAsyncScheduler().runDelayed(Ari.instance, e ->{
                                clickMeta.lore(List.of());
                                finalClickItem.setItemMeta(clickMeta);
                            }, 2, TimeUnit.SECONDS);
                        } else {
                            //error
                            Log.error("save home error");
                        }
                    });
                }
            }
        }
    }
    @EventHandler
    public void getNeedEditHomeChat(AsyncChatEvent event) {

    }
}
