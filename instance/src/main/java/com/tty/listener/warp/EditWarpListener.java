package com.tty.listener.warp;

import com.google.gson.reflect.TypeToken;
import com.tty.Ari;
import com.tty.dto.CustomInventoryHolder;
import com.tty.dto.OnEdit;
import com.tty.enumType.FilePath;
import com.tty.enumType.GuiType;
import com.tty.function.WarpManager;
import com.tty.gui.warp.WarpEditor;
import com.tty.gui.warp.WarpList;
import com.tty.lib.Lib;
import com.tty.lib.enum_type.FunctionType;
import com.tty.lib.enum_type.TitleInputType;
import com.tty.lib.task.CancellableTask;
import com.tty.lib.tool.FormatUtils;
import com.tty.lib.tool.Log;
import com.tty.listener.BaseEditFunctionGuiListener;
import com.tty.tool.ConfigObjectUtils;
import com.tty.tool.EconomyUtils;
import com.tty.tool.TextTool;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EditWarpListener extends BaseEditFunctionGuiListener {

    public EditWarpListener(GuiType guiType) {
        super(guiType);
    }

    @Override
    public void passClick(InventoryClickEvent event) {
        super.passClick(event);
        Inventory inventory = event.getInventory();
        ItemStack clickItem = event.getCurrentItem();
        assert clickItem != null;
        CustomInventoryHolder holder = (CustomInventoryHolder) inventory.getHolder();
        assert holder != null;
        Player player = holder.getPlayer();

        ItemMeta clickMeta = clickItem.getItemMeta();
        NamespacedKey icon_type = new NamespacedKey(Ari.instance, "type");
        FunctionType type = ConfigObjectUtils.ItemNBT_TypeCheck(clickMeta.getPersistentDataContainer().get(icon_type, PersistentDataType.STRING));
        if(type == null) return;

        WarpEditor warpEditor = (WarpEditor) holder.getMeta();
        WarpManager warpManager = new WarpManager();
        switch (type) {
            case REBACK -> {
                inventory.close();
                new WarpList(player).open();
            }
            case DELETE -> warpManager.deleteInstance(warpEditor.currentWarp).thenAccept(i -> {
                if (i) {
                    player.sendMessage(TextTool.setHEXColorText("function.warp.delete-success", FilePath.Lang));
                    Lib.Scheduler.run(Ari.instance, ab -> {
                        inventory.close();
                        new WarpList(player).open();
                    });
                } else {
                    player.sendMessage(TextTool.setHEXColorText("function.warp.not-found", FilePath.Lang));
                }
            }).exceptionally(i -> {
                Log.error("deleting warp error", i);
                return null;
            });
            case RENAME, COST, PERMISSION -> {
                //检查是否有经济插件，如果没有就return
                if (type.equals(FunctionType.COST) && EconomyUtils.isNull()) return;
                if (type.equals(FunctionType.PERMISSION) && event.getClick().isRightClick()) {
                    clickMeta.displayName(TextTool.setHEXColorText(""));
                    clickItem.setItemMeta(clickMeta);
                    warpEditor.currentWarp.setPermission(null);
                    return;
                }
                player.showTitle(
                        TextTool.setPlayerTitle(
                                ConfigObjectUtils.getValue("base.on-edit.title", FilePath.Lang.getName(), String.class, "null"),
                                ConfigObjectUtils.getValue("base.on-edit.sub-title", FilePath.Lang.getName(), String.class, "null"),
                                1000,
                                10000 ,
                                1000));
                inventory.close();
                this.addEditInstance(player, OnEdit.build(holder, TitleInputType.valueOf(type.name())));
                if (holder.getTask() == null) {
                    CancellableTask cancellableTask = Lib.Scheduler.runAsyncDelayed(Ari.instance, i -> {
                        if (this.removeEditInstance(player) != null) {
                            player.sendMessage(TextTool.setHEXColorText("base.on-edit.timeout-cancel", FilePath.Lang));
                        }
                        holder.setTask(null);
                    }, 200L);
                    holder.setTask(cancellableTask);
                }
            }
            case LOCATION -> {
                Location newLocation = player.getLocation();
                warpEditor.currentWarp.setLocation(newLocation.toString());
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
                inventory.setItem(event.getSlot(), newItemStake);
                warpEditor.currentWarp.setShowMaterial(current.name());
            }
            case SAVE -> {
                Log.debug("start saving warp id:" + warpEditor.currentWarp.getWarpId());
                clickMeta.lore(List.of(TextTool.setHEXColorText("base.save.ing", FilePath.Lang)));
                clickItem.setItemMeta(clickMeta);
                CompletableFuture<Boolean> future = warpManager.modify(warpEditor.currentWarp);
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
    }

    @Override
    public boolean onTitleEditStatus(String message, OnEdit onEdit) {
        Player player = onEdit.getHolder().getPlayer();
        List<String> value = ConfigObjectUtils.getValue("main.name-check", FilePath.WarpConfig.getName(), new TypeToken<List<String>>(){}.getType(), List.of());
        if(value == null) {
            Log.error("name-check list is null, check config");
            player.sendMessage(TextTool.setHEXColorText("base.on-error", FilePath.Lang));
            return false;
        }
        WarpEditor warpEditor = (WarpEditor) onEdit.getHolder().getMeta();
        switch (onEdit.getType()) {
            case RENAME -> {
                if(!FormatUtils.checkName(message) || value.contains(message) || !FormatUtils.checkName(message)) {
                    player.sendMessage(TextTool.setHEXColorText("base.on-edit.rename.name-error", FilePath.Lang));
                    return false;
                }
                if(message.length() > ConfigObjectUtils.getValue("main.name-length", FilePath.WarpConfig.getName(), new TypeToken<Integer>(){}.getType(), 15) &&
                        onEdit.getType().equals(TitleInputType.RENAME)) {
                    player.sendMessage(TextTool.setHEXColorText("base.on-edit.rename.name-too-long", FilePath.Lang));
                    return false;
                }
                warpEditor.currentWarp.setWarpName(message);
            }
            case PERMISSION -> {
                if(!FormatUtils.isValidPermissionNode(message)) {
                    player.sendMessage(TextTool.setHEXColorText("base.on-edit.permission.permission-error", FilePath.Lang));
                    return false;
                }
                warpEditor.currentWarp.setPermission(message);
            }
            case COST -> {
                try {
                    Double i = Double.parseDouble(message);
                    warpEditor.currentWarp.setCost(i);
                } catch (NumberFormatException e) {
                    player.sendMessage(TextTool.setHEXColorText("base.on-edit.cost.format-error", FilePath.Lang));
                    return false;
                }
            }
        }
        Lib.Scheduler.runAtEntity(Ari.instance, player, i -> warpEditor.open(), () -> {});
        return true;
    }
}
