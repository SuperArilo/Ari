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
import com.tty.lib.enum_type.LocationKeyType;
import com.tty.lib.task.CancellableTask;
import com.tty.lib.tool.ComponentUtils;
import com.tty.lib.tool.FormatUtils;
import com.tty.lib.tool.Log;
import com.tty.listener.BaseEditFunctionGuiListener;
import com.tty.tool.ConfigUtils;
import com.tty.tool.EconomyUtils;
import net.kyori.adventure.text.TextComponent;
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
        FunctionType type = FormatUtils.ItemNBT_TypeCheck(clickMeta.getPersistentDataContainer().get(icon_type, PersistentDataType.STRING));
        if(type == null) return;

        WarpEditor warpEditor = (WarpEditor) holder.getMeta();
        WarpManager warpManager = new WarpManager(true);
        switch (type) {
            case REBACK -> {
                inventory.close();
                new WarpList(player).open();
            }
            case DELETE -> warpManager.deleteInstance(warpEditor.currentWarp).thenAccept(i -> {
                if (i) {
                    player.sendMessage(ComponentUtils.text(ConfigUtils.getValue("function.warp.delete-success", FilePath.Lang)));
                    Lib.Scheduler.run(Ari.instance, ab -> {
                        inventory.close();
                        new WarpList(player).open();
                    });
                } else {
                    player.sendMessage(ComponentUtils.text(ConfigUtils.getValue("function.warp.not-found", FilePath.Lang)));
                }
            }).exceptionally(i -> {
                Log.error("deleting warp error", i);
                return null;
            });
            case RENAME, COST, PERMISSION -> {
                //检查是否有经济插件，如果没有就return
                if (type.equals(FunctionType.COST) && EconomyUtils.isNull()) return;
                if (type.equals(FunctionType.PERMISSION) && event.getClick().isRightClick()) {
                    clickMeta.displayName(ComponentUtils.text(""));
                    clickItem.setItemMeta(clickMeta);
                    warpEditor.currentWarp.setPermission(null);
                    return;
                }
                player.showTitle(
                        ComponentUtils.setPlayerTitle(
                                ConfigUtils.getValue("base.on-edit.title", FilePath.Lang),
                                ConfigUtils.getValue("base.on-edit.sub-title", FilePath.Lang),
                                1000,
                                10000 ,
                                1000));
                inventory.close();
                this.addEditInstance(player, OnEdit.build(holder, FunctionType.valueOf(type.name())));
                if (holder.getTask() == null) {
                    CancellableTask cancellableTask = Lib.Scheduler.runAsyncDelayed(Ari.instance, i -> {
                        if (this.removeEditInstance(player) != null) {
                            player.sendMessage(ComponentUtils.text(ConfigUtils.getValue("base.on-edit.timeout-cancel", FilePath.Lang)));
                        }
                        holder.setTask(null);
                    }, 200L);
                    holder.setTask(cancellableTask);
                }
            }
            case LOCATION -> {
                Location newLocation = player.getLocation();
                warpEditor.currentWarp.setLocation(newLocation.toString());
                clickMeta.displayName(ComponentUtils.text(FormatUtils.XYZText(newLocation.getX(), newLocation.getY(), newLocation.getZ())));
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
                clickMeta.lore(List.of(ComponentUtils.text(ConfigUtils.getValue("base.save.ing", FilePath.Lang))));
                clickItem.setItemMeta(clickMeta);
                CompletableFuture<Boolean> future = warpManager.modify(warpEditor.currentWarp);
                future.thenAccept(status -> {
                    clickMeta.lore(List.of(ComponentUtils.text(ConfigUtils.getValue(status ? "base.save.done":"base.save.error", FilePath.Lang))));
                    clickItem.setItemMeta(clickMeta);
                    if(status) {
                        Lib.Scheduler.runAsyncDelayed(Ari.instance, e ->{
                            clickMeta.lore(List.of());
                            clickItem.setItemMeta(clickMeta);
                        }, 20L);
                    }
                }).exceptionally(i -> {
                    Log.error("saving warp error", i);
                    clickMeta.lore(List.of(ComponentUtils.text(ConfigUtils.getValue("base.save.error", FilePath.Lang))));
                    clickItem.setItemMeta(clickMeta);
                    return null;
                });
            }
            case TOP_SLOT -> {
                warpEditor.currentWarp.setTopSlot(!warpEditor.currentWarp.isTopSlot());
                warpEditor.gui.getFunctionItems().forEach((k, v) -> {
                    if (v.getType().equals(FunctionType.TOP_SLOT)) {
                        List<String> lore = v.getLore();
                        List<TextComponent> list = lore.stream().map(p -> ComponentUtils.text(
                                p.replace(LocationKeyType.TOP_SLOT.getKey(),
                                        ConfigUtils.getValue(warpEditor.currentWarp.isTopSlot() ? "base.yes_re" : "base.no_re", FilePath.Lang)))).toList();
                        clickMeta.lore(list);
                        clickItem.setItemMeta(clickMeta);
                    }
                });
            }
        }
    }

    @Override
    public boolean onTitleEditStatus(String message, OnEdit onEdit) {
        Player player = onEdit.getHolder().getPlayer();
        List<String> value = ConfigUtils.getValue("main.name-check", FilePath.WarpConfig, new TypeToken<List<String>>(){}.getType(), List.of());
        if(value == null) {
            Log.error("name-check list is null, check config");
            player.sendMessage(ComponentUtils.text(ConfigUtils.getValue("base.on-error", FilePath.Lang)));
            return false;
        }
        WarpEditor warpEditor = (WarpEditor) onEdit.getHolder().getMeta();
        switch (onEdit.getType()) {
            case RENAME -> {
                if(!FormatUtils.checkName(message) || value.contains(message) || !FormatUtils.checkName(message)) {
                    player.sendMessage(ComponentUtils.text(ConfigUtils.getValue("base.on-edit.rename.name-error", FilePath.Lang)));
                    return false;
                }
                if(message.length() > ConfigUtils.getValue("main.name-length", FilePath.WarpConfig, new TypeToken<Integer>(){}.getType(), 15) &&
                        onEdit.getType().equals(FunctionType.RENAME)) {
                    player.sendMessage(ComponentUtils.text(ConfigUtils.getValue("base.on-edit.rename.name-too-long", FilePath.Lang)));
                    return false;
                }
                warpEditor.currentWarp.setWarpName(message);
            }
            case PERMISSION -> {
                if(!FormatUtils.isValidPermissionNode(message)) {
                    player.sendMessage(ComponentUtils.text(ConfigUtils.getValue("base.on-edit.permission.permission-error", FilePath.Lang)));
                    return false;
                }
                warpEditor.currentWarp.setPermission(message);
            }
            case COST -> {
                try {
                    Double i = Double.parseDouble(message);
                    warpEditor.currentWarp.setCost(i);
                } catch (NumberFormatException e) {
                    player.sendMessage(ComponentUtils.text(ConfigUtils.getValue("base.on-edit.cost.format-error", FilePath.Lang)));
                    return false;
                }
            }
        }
        Lib.Scheduler.runAtEntity(Ari.instance, player, i -> warpEditor.open(), () -> {});
        return true;
    }
}
