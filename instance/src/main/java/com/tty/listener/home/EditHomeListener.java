package com.tty.listener.home;

import com.google.gson.reflect.TypeToken;
import com.tty.Ari;
import com.tty.dto.CustomInventoryHolder;
import com.tty.dto.OnEdit;
import com.tty.enumType.FilePath;
import com.tty.enumType.GuiType;
import com.tty.function.HomeManager;
import com.tty.gui.home.HomeEditor;
import com.tty.gui.home.HomeList;
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
import java.util.concurrent.CompletableFuture;

public class EditHomeListener extends BaseEditFunctionGuiListener {

    public EditHomeListener(GuiType guiType) {
        super(guiType);
    }

    @Override
    public void passClick(InventoryClickEvent event) {
        super.passClick(event);
        Inventory inventory = event.getInventory();
        CustomInventoryHolder holder = (CustomInventoryHolder) inventory.getHolder();
        assert holder != null;

        Player player = holder.getPlayer();
        ItemStack clickItem = event.getCurrentItem();
        assert clickItem != null;

        ItemMeta clickMeta = clickItem.getItemMeta();
        NamespacedKey icon_type = new NamespacedKey(Ari.instance, "type");
        FunctionType type = FormatUtils.ItemNBT_TypeCheck(clickMeta.getPersistentDataContainer().get(icon_type, PersistentDataType.STRING));
        event.setCancelled(true);
        if (type == null) return;

        HomeEditor homeEditor = (HomeEditor) holder.getMeta();
        HomeManager homeManager = new HomeManager(player, true);
        switch (type) {
            case REBACK -> {
                inventory.close();
                new HomeList(player).open();
            }
            case DELETE -> //delete home
                    homeManager.deleteInstance(homeEditor.currentHome).thenAccept(i -> {
                        if (i) {
                            player.sendMessage(ComponentUtils.text(ConfigUtils.getValue("function.home.delete-success", FilePath.Lang)));
                            Lib.Scheduler.run(Ari.instance, j -> {
                                inventory.close();
                                new HomeList(player).open();
                            });
                        } else {
                            player.sendMessage(ComponentUtils.text(ConfigUtils.getValue("function.home.not-found", FilePath.Lang)));
                        }

                    });
            case RENAME -> {
                player.showTitle(
                        ComponentUtils.setPlayerTitle(
                                ConfigUtils.getValue("base.on-edit.title", FilePath.Lang),
                                ConfigUtils.getValue("base.on-edit.sub-title", FilePath.Lang),
                                1000,
                                10000 ,
                                1000));
                inventory.close();
                this.addEditInstance(player, OnEdit.build(holder, type));
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
                //reset LOCATION
                Location newLocation = player.getLocation();
                homeEditor.currentHome.setLocation(newLocation.toString());
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
                homeEditor.currentHome.setShowMaterial(current.name());
            }
            case TOP_SLOT -> {
                homeEditor.currentHome.setTopSlot(!homeEditor.currentHome.isTopSlot());
                homeEditor.baseInstance.getFunctionItems().forEach((k, v) -> {
                    if (v.getType().equals(FunctionType.TOP_SLOT)) {
                        List<String> lore = v.getLore();
                        List<TextComponent> list = lore.stream().map(p -> ComponentUtils.text(
                                p.replace(IconKeyType.TOP_SLOT.getKey(), ConfigUtils.getValue(homeEditor.currentHome.isTopSlot() ? "base.yes_re" : "base.no_re", FilePath.Lang)))
                        ).toList();
                        clickMeta.lore(list);
                        clickItem.setItemMeta(clickMeta);
                    }
                });
            }
            case SAVE -> {
                clickMeta.lore(List.of(ComponentUtils.text(ConfigUtils.getValue("base.save.ing", FilePath.Lang))));
                clickItem.setItemMeta(clickMeta);
                CompletableFuture<Boolean> future = homeManager.modify(homeEditor.currentHome);
                future.thenAccept(status -> {
                    clickMeta.lore(List.of(ComponentUtils.text(ConfigUtils.getValue(status ? "base.save.done":"base.save.error", FilePath.Lang))));
                    clickItem.setItemMeta(clickMeta);
                    Lib.Scheduler.runAsyncDelayed(Ari.instance, e -> {
                        clickMeta.lore(List.of());
                        clickItem.setItemMeta(clickMeta);
                    }, 20);
                }).exceptionally(i -> {
                    Log.error("save home error", i);
                    clickMeta.lore(List.of(ComponentUtils.text(ConfigUtils.getValue("base.save.error", FilePath.Lang))));
                    clickItem.setItemMeta(clickMeta);
                    player.sendMessage(ComponentUtils.text(ConfigUtils.getValue("base.on-error", FilePath.Lang)));
                    return null;
                });
            }
        }
    }

    @Override
    public boolean onTitleEditStatus(String message, OnEdit onEdit) {
        Player player = onEdit.getHolder().getPlayer();
        List<Object> checkList = ConfigUtils
                .getValue(
                        "main.name-check",
                        FilePath.HomeConfig,
                        new TypeToken<List<String>>() {
                        }.getType(),
                        List.of());
        if(!FormatUtils.checkName(message) || checkList.contains(message)) {
            player.sendMessage(ComponentUtils.text(ConfigUtils.getValue("base.on-edit.rename.name-error", FilePath.Lang)));
            return false;
        }
        if(message.length() > ConfigUtils.getValue("main.name-length", FilePath.HomeConfig, Integer.class, 15)) {
            player.sendMessage(ComponentUtils.text(ConfigUtils.getValue("base.on-edit.rename.name-too-long", FilePath.Lang)));
            return false;
        }
        HomeEditor editor = (HomeEditor) onEdit.getHolder().getMeta();
        editor.currentHome.setHomeName(message);
        Lib.Scheduler.runAtEntity(Ari.instance, player, p -> editor.open(), () -> {});
        return true;
    }
}
