package ari.superarilo.listener.home;

import ari.superarilo.Ari;
import ari.superarilo.dto.CustomInventoryHolder;
import ari.superarilo.entity.sql.PlayerHome;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.enumType.FunctionType;
import ari.superarilo.enumType.GuiType;
import ari.superarilo.function.HomeManager;
import ari.superarilo.gui.home.HomeEditor;
import ari.superarilo.gui.home.HomeList;
import ari.superarilo.tool.Log;
import ari.superarilo.tool.TextTool;
import com.google.gson.reflect.TypeToken;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class EditHomeListener implements Listener {

    private final List<CustomInventoryHolder> editStatus = new CopyOnWriteArrayList<>();

    @EventHandler
    public void editGuiClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if(inventory == null || event.getSlot() > inventory.getSize()) return;
        if(!(inventory.getHolder() instanceof CustomInventoryHolder) && (event.getClick().equals(ClickType.SHIFT_RIGHT) || event.getClick().equals(ClickType.SHIFT_LEFT))) {
            event.setCancelled(true);
            return;
        }
        if(inventory.getHolder() instanceof CustomInventoryHolder holder && holder.getType().equals(GuiType.EDITHOME)) {

            Player player = holder.getPlayer();
            this.removeIfPlayInEditList(player);
            ItemStack clickItem = event.getCurrentItem();

            //当拖起物品点击的地方为null取消操作
            if(clickItem == null) {
                event.setCancelled(true);
                return;
            }
            ItemMeta clickMeta = clickItem.getItemMeta();
            FunctionType type = Ari.instance.objectConvert.ItemNBT_TypeCheck(clickMeta.getPersistentDataContainer().get(new NamespacedKey(Ari.instance, "type"), PersistentDataType.STRING));
            event.setCancelled(true);

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
                    Audience.audience(player).showTitle(
                            TextTool.setPlayerTitle(
                                    Ari.instance.configManager.getValue("on-edit-home.rename.title", FilePath.Lang, String.class),
                                    Ari.instance.configManager.getValue("on-edit-home.rename.sub-title", FilePath.Lang, String.class),
                                    1000,
                                    10000 ,
                                    1000));
                    inventory.close();
                    this.editStatus.add(holder);
                    //rename home
                }
                case LOCATION -> {
                    //reset LOCATION
                    Location newLocation = player.getLocation();
                    home.setX(Double.valueOf(Ari.instance.formatUtil.format_2(newLocation.getX())));
                    home.setY(Double.valueOf(Ari.instance.formatUtil.format_2(newLocation.getY())));
                    home.setZ(Double.valueOf(Ari.instance.formatUtil.format_2(newLocation.getZ())));
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
                            }, 1, TimeUnit.SECONDS);
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
        if (this.editStatus.isEmpty()) return;
        event.setCancelled(true);
        Player player = event.getPlayer();
        String message = TextTool.componentToString(event.message());
        List<String> value = Ari.instance.configManager.getValue("main.edit-home.bad-words", FilePath.HomeConfig, new TypeToken<List<String>>(){}.getType());
        if(!Ari.instance.formatUtil.checkName(message) || value.contains(message)) {
            player.sendMessage(TextTool.setHEXColorText("command.sethome.name-error", FilePath.Lang));
            return;
        }
        if(message.length() > (Integer) Ari.instance.configManager.getValue("main.edit-home.name-length", FilePath.HomeConfig, Integer.class)) {
            player.sendMessage(TextTool.setHEXColorText("command.sethome.name-too-long", FilePath.Lang));
            return;
        }

        Audience.audience(player).clearTitle();

        if (FunctionType.CANCEL.name().equals(message.toUpperCase())) {
            this.removeIfPlayInEditList(player);
            player.sendMessage(TextTool.setHEXColorText("on-edit-home.rename.cancel", FilePath.Lang));
            return;
        }
        // 使用 removeIf 删除满足条件的元素
        this.editStatus.removeIf(i -> {
            if (i.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                PlayerHome home = (PlayerHome) i.getMeta();
                home.setHomeName(message);
                new HomeEditor(home, player).open();
                Log.debug("player: [" + player.getName() + "] edit home-name status removed");
                return true;
            }
            return false;
        });
    }
    protected synchronized void removeIfPlayInEditList(Player player) {
        this.editStatus.removeIf(e -> e.getPlayer().getUniqueId().equals(player.getUniqueId()));
    }
}
