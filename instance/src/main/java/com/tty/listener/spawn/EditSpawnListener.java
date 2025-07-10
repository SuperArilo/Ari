package com.tty.listener.spawn;

import com.google.gson.reflect.TypeToken;
import com.tty.Ari;
import com.tty.dto.CustomInventoryHolder;
import com.tty.dto.OnEdit;
import com.tty.enumType.FilePath;
import com.tty.enumType.GuiType;
import com.tty.function.SpawnManager;
import com.tty.gui.spawn.SpawnEditor;
import com.tty.gui.spawn.SpawnList;
import com.tty.lib.Lib;
import com.tty.lib.enum_type.FunctionType;
import com.tty.lib.enum_type.IconKeyType;
import com.tty.lib.task.CancellableTask;
import com.tty.lib.tool.ComponentUtils;
import com.tty.lib.tool.FormatUtils;
import com.tty.lib.tool.Log;
import com.tty.listener.BaseEditFunctionGuiListener;
import com.tty.tool.ConfigUtils;
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

public class EditSpawnListener extends BaseEditFunctionGuiListener {

    public EditSpawnListener(GuiType guiType) {
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

        SpawnEditor spawnEditor = (SpawnEditor) holder.getMeta();
        SpawnManager manager = new SpawnManager(true);
        switch (type) {
            case REBACK -> {
                inventory.close();
                new SpawnList(player).open();
            }
            case DELETE -> manager.deleteInstance(spawnEditor.serverSpawn)
                    .thenAccept(status -> {
                        if (status) {
                            player.sendMessage(ComponentUtils.text(ConfigUtils.getValue("function.spawn.delete-success", FilePath.Lang)));
                            Lib.Scheduler.runAtEntity(Ari.instance,
                                    player,
                                    i -> {
                                        inventory.close();
                                        new SpawnList(player).open();
                                    },
                                    () -> {});
                        }
                    });
            case RENAME, PERMISSION -> {
                if (type.equals(FunctionType.PERMISSION) && event.getClick().isRightClick()) {
                    clickMeta.displayName(ComponentUtils.text(""));
                    clickItem.setItemMeta(clickMeta);
                    spawnEditor.serverSpawn.setPermission(null);
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
                spawnEditor.serverSpawn.setLocation(newLocation.toString());
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
                spawnEditor.serverSpawn.setShowMaterial(current.name());
            }
            case SAVE -> {
                clickMeta.lore(List.of(ComponentUtils.text(ConfigUtils.getValue("base.save.ing", FilePath.Lang))));
                clickItem.setItemMeta(clickMeta);
                manager.modify(spawnEditor.serverSpawn)
                    .thenAccept(status -> {
                        clickMeta.lore(List.of(ComponentUtils.text(ConfigUtils.getValue(status ? "base.save.done":"base.save.error", FilePath.Lang))));
                        clickItem.setItemMeta(clickMeta);
                        if(status) {
                            Lib.Scheduler.runAsyncDelayed(Ari.instance, e ->{
                                clickMeta.lore(List.of());
                                clickItem.setItemMeta(clickMeta);
                            }, 20L);
                        } else {
                            clickMeta.lore(List.of(ComponentUtils.text(ConfigUtils.getValue("base.save.error", FilePath.Lang))));
                            clickItem.setItemMeta(clickMeta);
                        }
                    }).exceptionally(i -> {
                        Log.error("saving warp error", i);
                        clickMeta.lore(List.of(ComponentUtils.text(ConfigUtils.getValue("base.save.error", FilePath.Lang))));
                        clickItem.setItemMeta(clickMeta);
                        return null;
                    });
            }
            case TOP_SLOT -> {
                spawnEditor.serverSpawn.setTopSlot(!spawnEditor.serverSpawn.isTopSlot());
                spawnEditor.baseInstance.getFunctionItems().forEach((k, v) -> {
                    if (v.getType().equals(FunctionType.TOP_SLOT)) {
                        List<String> lore = v.getLore();
                        List<TextComponent> list = lore.stream().map(p -> ComponentUtils.text(
                                p.replace(IconKeyType.TOP_SLOT.getKey(),
                                        ConfigUtils.getValue(spawnEditor.serverSpawn.isTopSlot() ? "base.yes_re" : "base.no_re", FilePath.Lang)))).toList();
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
        SpawnEditor spawnEditor = (SpawnEditor) onEdit.getHolder().getMeta();
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
                spawnEditor.serverSpawn.setSpawnName(message);
            }
            case PERMISSION -> {
                if(!FormatUtils.isValidPermissionNode(message)) {
                    player.sendMessage(ComponentUtils.text(ConfigUtils.getValue("base.on-edit.permission.permission-error", FilePath.Lang)));
                    return false;
                }
                spawnEditor.serverSpawn.setPermission(message);
            }
        }
        Lib.Scheduler.runAtEntity(Ari.instance, player, i -> spawnEditor.open(), () -> {});
        return true;
    }
}
