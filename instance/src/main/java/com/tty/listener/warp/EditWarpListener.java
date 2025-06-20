package com.tty.listener.warp;

import com.google.gson.reflect.TypeToken;
import com.tty.Ari;
import com.tty.dto.CustomInventoryHolder;
import com.tty.dto.OnEdit;
import com.tty.entity.sql.ServerWarp;
import com.tty.enumType.FilePath;
import com.tty.enumType.GuiType;
import com.tty.function.WarpManager;
import com.tty.gui.warp.WarpEditor;
import com.tty.gui.warp.WarpList;
import com.tty.lib.Lib;
import com.tty.lib.enum_type.FunctionType;
import com.tty.lib.enum_type.TitleInputType;
import com.tty.lib.tool.FormatUtils;
import com.tty.lib.tool.Log;
import com.tty.tool.ConfigObjectUtils;
import com.tty.tool.EconomyUtils;
import com.tty.tool.TextTool;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
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
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class EditWarpListener implements Listener {

    private final Map<UUID, OnEdit> editMap = new ConcurrentHashMap<>();

    @EventHandler
    public void editClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null || event.getSlot() >= clickedInventory.getSize()) {
            return;
        }
        if (clickedInventory.getHolder() instanceof CustomInventoryHolder holder && holder.getType().equals(GuiType.WARPEDIT)) {
            if(event.isShiftClick()) {
                event.setCancelled(true);
                return;
            }
            Player player = holder.getPlayer();
            this.removeIfPlayerInMap(player);
            ItemStack clickItem = event.getCurrentItem();

            //当托起物品点击的地方是null的时候取消操作
            if(clickItem == null) {
                event.setCancelled(true);
                return;
            }

            ItemMeta clickMeta = clickItem.getItemMeta();
            NamespacedKey icon_type = new NamespacedKey(Ari.instance, "type");
            FunctionType type = ConfigObjectUtils.ItemNBT_TypeCheck(clickMeta.getPersistentDataContainer().get(icon_type, PersistentDataType.STRING));
            if(type == null) return;
            event.setCancelled(true);

            ServerWarp serverWarp = (ServerWarp) holder.getMeta();
            WarpManager warpManager = WarpManager.create(Bukkit.getPlayer(UUID.fromString(serverWarp.getCreateBy())));
            switch (type) {
                case REBACK -> {
                    clickedInventory.close();
                    new WarpList(player).open();
                }
                case DELETE -> {
                    warpManager.deleteInstance(serverWarp.getWarpId()).thenAccept(i -> {
                        if (i) {
                            player.sendMessage(TextTool.setHEXColorText("function.warp.delete-success", FilePath.Lang));
                        } else {
                            player.sendMessage(TextTool.setHEXColorText("function.warp.not-found", FilePath.Lang));
                        }
                    }).exceptionally(i -> {
                        Log.error("deleting warp error", i);
                       return null;
                    });
                    clickedInventory.close();
                    new WarpList(player).open();
                }
                case RENAME, COST, PERMISSION -> {
                    //检查是否有经济插件，如果没有就return
                    if (type.equals(FunctionType.COST) && EconomyUtils.isNull()) return;
                    if (type.equals(FunctionType.PERMISSION) && event.getClick().isRightClick()) {
                        clickMeta.displayName(TextTool.setHEXColorText(""));
                        clickItem.setItemMeta(clickMeta);
                        serverWarp.setPermission(null);
                        return;
                    }
                    Audience.audience(player).showTitle(
                            TextTool.setPlayerTitle(
                                    ConfigObjectUtils.getValue("base.on-edit.title", FilePath.Lang.getName(), String.class, "null"),
                                    ConfigObjectUtils.getValue("base.on-edit.sub-title", FilePath.Lang.getName(), String.class, "null"),
                                    1000,
                                    10000 ,
                                    1000));
                    clickedInventory.close();
                    this.editMap.put(player.getUniqueId(), OnEdit.build(holder, TitleInputType.valueOf(type.name())));
                }
                case LOCATION -> {
                    Location newLocation = player.getLocation();
                    serverWarp.setLocation(newLocation.toString());
                    clickMeta.displayName(TextTool.setHEXColorText(TextTool.XYZText(newLocation.getX(), newLocation.getY(), newLocation.getZ())));
                    clickItem.setItemMeta(clickMeta);
                }
                case ICON -> {
                    ItemStack cursor = event.getCursor();
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
                    serverWarp.setShowMaterial(current.name());
                }
                case SAVE -> {
                    Log.debug("start saving warp id:" + serverWarp.getWarpId());
                    clickMeta.lore(List.of(TextTool.setHEXColorText("base.save.ing", FilePath.Lang)));
                    clickItem.setItemMeta(clickMeta);
                    CompletableFuture<Boolean> future = warpManager.modify(serverWarp);
                    future.thenAccept(status -> {
                        if(status) {
                            clickMeta.lore(List.of(TextTool.setHEXColorText("base.save.done", FilePath.Lang)));
                            clickItem.setItemMeta(clickMeta);
                            Lib.Scheduler.runAsyncDelayed(Ari.instance, e ->{
                                clickMeta.lore(List.of());
                                clickItem.setItemMeta(clickMeta);
                            }, 20L);
                        }
                    }).exceptionally(i -> {
                        Log.error("saving warp error", i);
                        clickMeta.lore(List.of(TextTool.setHEXColorText("base.save.error", FilePath.Lang)));
                        clickItem.setItemMeta(clickMeta);
                        Lib.Scheduler.runAsyncDelayed(Ari.instance, e ->{
                            clickMeta.lore(List.of());
                            clickItem.setItemMeta(clickMeta);
                        }, 20L);
                        return null;
                    });
                }
            }
            return;
        }
        if (event.getAction().equals(InventoryAction.COLLECT_TO_CURSOR) && event.isShiftClick() && event.getView().getTopInventory().getHolder() instanceof CustomInventoryHolder) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void dragWarpEdit(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof CustomInventoryHolder holder && holder.getType().equals(GuiType.WARPEDIT)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void getNeedEditWarpChat(AsyncChatEvent event) {
        if (this.editMap.isEmpty()) return;
        event.setCancelled(true);

        Player player = event.getPlayer();
        OnEdit onEdit = this.editMap.get(player.getUniqueId());
        if(onEdit == null) return;
        String message = TextTool.componentToString(event.message());
        List<String> value = ConfigObjectUtils.getValue("main.name-check", FilePath.WarpConfig.getName(), new TypeToken<List<String>>(){}.getType(), List.of());
        if(value == null) {
            Log.error("name-check list is null, check config");
            player.sendMessage(TextTool.setHEXColorText("base.on-error", FilePath.Lang));
            return;
        }
        Audience.audience(player).clearTitle();
        if (FunctionType.CANCEL.name().equals(message.toUpperCase())) {
            this.removeIfPlayerInMap(player);
            player.sendMessage(TextTool.setHEXColorText("base.on-edit.cancel", FilePath.Lang));
            return;
        }

        ServerWarp serverWarp = (ServerWarp) onEdit.getHolder().getMeta();
        try {
            switch (onEdit.getType()) {
                case RENAME -> {
                    if(!FormatUtils.checkName(message) || value.contains(message) || !FormatUtils.checkName(message)) {
                        player.sendMessage(TextTool.setHEXColorText("base.on-edit.rename.name-error", FilePath.Lang));
                        return;
                    }
                    if(message.length() > ConfigObjectUtils.getValue("main.name-length", FilePath.WarpConfig.getName(), new TypeToken<Integer>(){}.getType(), 15) &&
                            onEdit.getType().equals(TitleInputType.RENAME)) {
                        player.sendMessage(TextTool.setHEXColorText("base.on-edit.rename.name-too-long", FilePath.Lang));
                        return;
                    }
                    serverWarp.setWarpName(message);
                }
                case PERMISSION -> {
                     if(!FormatUtils.isValidPermissionNode(message)) {
                         player.sendMessage(TextTool.setHEXColorText("base.on-edit.permission.permission-error", FilePath.Lang));
                         return;
                     }
                     serverWarp.setPermission(message);
                }
                case COST -> {
                    try {
                        Double i = Double.parseDouble(message);
                        serverWarp.setCost(i);
                    } catch (NumberFormatException e) {
                        player.sendMessage(TextTool.setHEXColorText("base.on-edit.cost.format-error", FilePath.Lang));
                        return;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.editMap.remove(player.getUniqueId());
        new WarpEditor(serverWarp, player).open();
        Log.debug("player: [" + player.getName() + "] edit warp-name status removed");
    }


    private synchronized void removeIfPlayerInMap(@NotNull Player player) {
        this.editMap.remove(player.getUniqueId());
    }
}
