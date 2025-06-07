package com.tty.listener.home;

import com.tty.Ari;
import com.tty.dto.CustomInventoryHolder;
import com.tty.entity.sql.ServerHome;
import com.tty.enumType.FilePath;
import com.tty.lib.enum_type.FunctionType;
import com.tty.enumType.GuiType;
import com.tty.function.HomeManager;
import com.tty.gui.home.HomeEditor;
import com.tty.gui.home.HomeList;
import com.tty.lib.Lib;
import com.tty.tool.ConfigObjectUtils;
import com.tty.lib.tool.FormatUtils;
import com.tty.tool.Log;
import com.tty.tool.TextTool;
import com.google.gson.reflect.TypeToken;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.concurrent.*;

public class EditHomeListener implements Listener {

    private final List<CustomInventoryHolder> editStatus = new CopyOnWriteArrayList<>();

    @EventHandler
    public void editGuiClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null || event.getSlot() >= clickedInventory.getSize()) return;
        if (clickedInventory.getHolder() instanceof CustomInventoryHolder holder && holder.getType().equals(GuiType.HOMEEDIT)) {
            if (event.isShiftClick()) {
                event.setCancelled(true);
                return;
            }
            Player player = holder.getPlayer();
            this.removeIfPlayInEditList(player);
            ItemStack clickItem = event.getCurrentItem();

            //当拖起物品点击的地方为null取消操作
            if(clickItem == null) {
                event.setCancelled(true);
                return;
            }

            ItemMeta clickMeta = clickItem.getItemMeta();
            NamespacedKey icon_type = new NamespacedKey(Ari.instance, "type");
            FunctionType type = ConfigObjectUtils.ItemNBT_TypeCheck(clickMeta.getPersistentDataContainer().get(icon_type, PersistentDataType.STRING));
            event.setCancelled(true);
            if (type == null) return;

            ServerHome home = (ServerHome) holder.getMeta();
            HomeManager homeManager = HomeManager.create(home.getPlayerUUID());
            switch (type) {
                case REBACK -> {
                    clickedInventory.close();
                    new HomeList(player).open();
                }
                case DELETE -> //delete home
                        homeManager.deleteInstance(home.getHomeId()).thenAccept(i -> Lib.Scheduler.run(Ari.instance, j -> {
                            clickedInventory.close();
                            new HomeList(player).open();
                        }));
                case RENAME -> {
                    Audience.audience(player).showTitle(
                            TextTool.setPlayerTitle(
                                    ConfigObjectUtils.getValue("base.on-edit.title", FilePath.Lang.getName(), String.class),
                                   ConfigObjectUtils.getValue("base.on-edit.sub-title", FilePath.Lang.getName(), String.class),
                                    1000,
                                    10000 ,
                                    1000));
                    clickedInventory.close();
                    this.editStatus.add(holder);
                    //rename home
                }
                case LOCATION -> {
                    //reset LOCATION
                    Location newLocation = player.getLocation();
                    home.setLocation(newLocation.toString());
                    clickMeta.displayName(TextTool.setHEXColorText(TextTool.XYZText(newLocation.getX(), newLocation.getY(), newLocation.getZ())));
                    clickItem.setItemMeta(clickMeta);
                }
                case ICON -> {
                    ItemStack cursor = event.getCursor();
                    if(cursor == null) return;
                    Material current = cursor.getType();
                    if(current.equals(Material.AIR)) return;
                    ItemStack newItemStake = new ItemStack(current);
                    ItemMeta newItemMeta = newItemStake.getItemMeta();
                    newItemMeta.displayName(clickMeta.displayName());
                    newItemMeta.lore(clickItem.lore());
                    String string = clickMeta.getPersistentDataContainer().get(icon_type, PersistentDataType.STRING);
                    if (string == null) return;
                    newItemMeta.getPersistentDataContainer().set(icon_type, PersistentDataType.STRING, string);
                    newItemStake.setItemMeta(newItemMeta);
                    clickedInventory.setItem(event.getSlot(), newItemStake);
                    home.setShowMaterial(current.name());
                }
                case SAVE -> {
                    //save
                    Log.debug("start saving home");
                    clickMeta.lore(List.of(TextTool.setHEXColorText("base.save.ing", FilePath.Lang)));
                    clickItem.setItemMeta(clickMeta);
                    CompletableFuture<Boolean> future = homeManager.modify(home);
                    future.thenAccept(status -> {
                        if(status) {
                            clickMeta.lore(List.of(TextTool.setHEXColorText("base.save.done", FilePath.Lang)));
                            clickItem.setItemMeta(clickMeta);
                            Lib.Scheduler.runAsyncDelayed(Ari.instance, e -> {
                                clickMeta.lore(List.of());
                                clickItem.setItemMeta(clickMeta);
                            }, 20);
                        } else {
                            clickMeta.lore(List.of(TextTool.setHEXColorText("base.save.error", FilePath.Lang)));
                            Log.error("save home error");
                        }
                    });
                }
            }
            return;
        }
        if (event.getAction().equals(InventoryAction.COLLECT_TO_CURSOR) || event.isShiftClick() && event.isRightClick() && event.getView().getTopInventory().getHolder() instanceof CustomInventoryHolder) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void dragEdit(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof CustomInventoryHolder holder && holder.getType().equals(GuiType.HOMEEDIT)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void getNeedEditHomeChat(AsyncChatEvent event) {
        if (this.editStatus.isEmpty()) return;
        event.setCancelled(true);
        Player player = event.getPlayer();
        String message = TextTool.componentToString(event.message());
        List<String> value = ConfigObjectUtils.getValue("main.name-check", FilePath.HomeConfig.getName(), new TypeToken<List<String>>(){}.getType());
        if(value == null) {
            Log.error("name-check list is null, check config");
            player.sendMessage(TextTool.setHEXColorText("base.on-error", FilePath.Lang));
            return;
        }
        if(!FormatUtils.checkName(message) || value.contains(message)) {
            player.sendMessage(TextTool.setHEXColorText("base.on-edit.rename.name-error", FilePath.Lang));
            return;
        }
        if(message.length() > (Integer) ConfigObjectUtils.getValue("main.name-length", FilePath.HomeConfig.getName(), Integer.class)) {
            player.sendMessage(TextTool.setHEXColorText("base.on-edit.rename.name-too-long", FilePath.Lang));
            return;
        }

        Audience.audience(player).clearTitle();

        if (FunctionType.CANCEL.name().equals(message.toUpperCase())) {
            this.removeIfPlayInEditList(player);
            player.sendMessage(TextTool.setHEXColorText("base.on-edit.cancel", FilePath.Lang));
            return;
        }
        // 使用 removeIf 删除满足条件的元素
        this.editStatus.removeIf(i -> {
            if (i.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                ServerHome home = (ServerHome) i.getMeta();
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
