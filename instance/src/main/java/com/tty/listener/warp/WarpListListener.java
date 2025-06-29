package com.tty.listener.warp;

import com.tty.Ari;
import com.tty.dto.CustomInventoryHolder;
import com.tty.entity.sql.ServerWarp;
import com.tty.enumType.FilePath;
import com.tty.enumType.GuiType;
import com.tty.function.TeleportCallback;
import com.tty.command.check.TeleportCheck;
import com.tty.function.TeleportThread;
import com.tty.function.WarpManager;
import com.tty.gui.warp.WarpEditor;
import com.tty.gui.warp.WarpList;
import com.tty.lib.Lib;
import com.tty.lib.enum_type.FunctionType;
import com.tty.lib.enum_type.LangType;
import com.tty.lib.enum_type.TeleportType;
import com.tty.lib.tool.Log;
import com.tty.tool.*;
import org.bukkit.Bukkit;
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

import java.util.Optional;
import java.util.UUID;


public class WarpListListener extends TeleportCheck implements Listener {

    private final NamespacedKey TYPE_KEY = new NamespacedKey(Ari.instance, "type");
    private final NamespacedKey WARP_ID_KEY = new NamespacedKey(Ari.instance, "warp_id");

    @EventHandler
    public void warpListClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if(inventory.getHolder() instanceof CustomInventoryHolder holder && holder.getType().equals(GuiType.WARPLIST)) {
            event.setCancelled(true);
            if(event.getSlot() >= inventory.getSize()) return;
            ItemStack currentItem = event.getCurrentItem();
            if (currentItem == null) return;
            FunctionType type = ConfigObjectUtils.ItemNBT_TypeCheck(currentItem.getItemMeta().getPersistentDataContainer().get(this.TYPE_KEY, PersistentDataType.STRING));
            if(type == null) return;
            Player player = holder.getPlayer();
            WarpList warpList = (WarpList) holder.getMeta();
            switch (type) {
                case BACK -> inventory.close();
                case DATA -> {
                    String warpId = currentItem.getItemMeta().getPersistentDataContainer().get(this.WARP_ID_KEY, PersistentDataType.STRING);
                    if(warpId == null) break;
                    Optional<ServerWarp> first = warpList.data.stream().filter(j -> j.getWarpId().equals(warpId)).findFirst();
                    if (first.isEmpty()) {
                        Log.error("can't find warpId: " + warpId);
                        break;
                    }
                    WarpManager.create(Bukkit.getPlayer(UUID.fromString(first.get().getCreateBy())))
                        .asyncGetInstance(warpId)
                        .thenAccept(instance -> {
                            if(instance == null) {
                                player.sendMessage(TextTool.setHEXColorText("function.warp.not-found", FilePath.Lang));
                                return;
                            }
                            boolean isOwner = UUID.fromString(instance.getCreateBy()).equals(player.getUniqueId());
                            ClickType eventClick = event.getClick();
                            if(eventClick.isLeftClick()) {
                                String permission = instance.getPermission();
                                if(permission != null && !permission.isEmpty()) {
                                    boolean hasPermission = PermissionUtils.hasPermission(player, permission);
                                    if (!hasPermission && !isOwner) {
                                        player.sendMessage(TextTool.setHEXColorText("function.warp.no-permission-teleport", FilePath.Lang));
                                        return;
                                    }
                                }
                                Location targetLocation = ConfigObjectUtils.parseLocation(instance.getLocation());
                                TeleportThread.playerToLocation(player,targetLocation)
                                        .teleport(
                                                ConfigObjectUtils.getValue("main.teleport.delay", FilePath.WarpConfig.getName(), Integer.class, 3),
                                                new TeleportCallback() {
                                                    @Override
                                                    public void onCancel() {
                                                        TeleportCheck.remove(player, targetLocation,TeleportType.POINT);
                                                    }
                                                    @Override
                                                    public void after() {
                                                        //判断是否是地标拥有者或者是不是op，如果是则不扣
                                                        if(!isOwner && !player.isOp() && ConfigObjectUtils.getValue("main.cost", FilePath.WarpConfig.getName(), Boolean.class, false)) {
                                                            EconomyUtils.withdrawPlayer(player, instance.getCost());
                                                            String value = ConfigObjectUtils.getValue("teleport.costed", FilePath.Lang.getName(), String.class, "0.0");
                                                            player.sendMessage(TextTool.setHEXColorText(value.replace(LangType.COSTED.getType(), instance.getCost().toString() + EconomyUtils.getNamePlural())));
                                                        }
                                                        TeleportCheck.remove(player, targetLocation, TeleportType.POINT);
                                                    }
                                                    @Override
                                                    public void before(TeleportThread teleportThread) {
                                                        if(!EconomyUtils.hasEnoughBalance(player, instance.getCost()) && !isOwner && ConfigObjectUtils.getValue("main.permission", FilePath.WarpConfig.getName(), Boolean.class, true)) {
                                                            player.sendMessage(TextTool.setHEXColorText("function.warp.not-enough-money", FilePath.Lang));
                                                            teleportThread.cancel();
                                                            return;
                                                        }
                                                        if(!WarpListListener.super.preCheckStatus(player, targetLocation, 200L)) {
                                                            teleportThread.cancel();
                                                        }
                                                    }
                                                });
                            } else if(eventClick.isRightClick()) {
                                if(isOwner || player.isOp()) {
                                    new WarpEditor(instance, player).open();
                                } else {
                                    player.sendMessage(TextTool.setHEXColorText("function.warp.no-permission-edit", FilePath.Lang));
                                }
                            }
                        }).exceptionally(b -> {
                            Log.error("error", b);
                            player.sendMessage(TextTool.setHEXColorText("base.on-error", FilePath.Lang));
                            return null;
                        });
                    Lib.Scheduler.runAtRegion(Ari.instance, player.getLocation(), j -> inventory.close());
                }
                case PREV -> warpList.prev();
                case NEXT -> warpList.next();
            }
        }
    }
}
