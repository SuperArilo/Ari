package com.tty.listener.home;

import com.google.gson.reflect.TypeToken;
import com.tty.Ari;
import com.tty.dto.CustomInventoryHolder;
import com.tty.enumType.FilePath;
import com.tty.enumType.GuiType;
import com.tty.function.HomeManager;
import com.tty.gui.home.HomeEditor;
import com.tty.gui.home.HomeList;
import com.tty.lib.Lib;
import com.tty.lib.enum_type.FunctionType;
import com.tty.lib.enum_type.LocationKeyType;
import com.tty.lib.task.CancellableTask;
import com.tty.lib.tool.FormatUtils;
import com.tty.lib.tool.Log;
import com.tty.tool.ConfigObjectUtils;
import com.tty.tool.TextTool;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

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

            HomeEditor homeEditor = (HomeEditor) holder.getMeta();
            HomeManager homeManager = HomeManager.create(Bukkit.getPlayer(UUID.fromString(homeEditor.currentHome.getPlayerUUID())));
            switch (type) {
                case REBACK -> {
                    clickedInventory.close();
                    new HomeList(player).open();
                }
                case DELETE -> //delete home
                        homeManager.deleteInstance(homeEditor.currentHome.getHomeId()).thenAccept(i -> Lib.Scheduler.run(Ari.instance, j -> {
                            clickedInventory.close();
                            new HomeList(player).open();
                        }));
                case RENAME -> {
                    player.showTitle(
                            TextTool.setPlayerTitle(
                                    ConfigObjectUtils.getValue("base.on-edit.title", FilePath.Lang.getName(), String.class, ""),
                                   ConfigObjectUtils.getValue("base.on-edit.sub-title", FilePath.Lang.getName(), String.class, ""),
                                    1000,
                                    10000 ,
                                    1000));
                    clickedInventory.close();
                    this.editStatus.add(holder);
                    if (holder.getTask() == null) {
                        CancellableTask cancellableTask = Lib.Scheduler.runAsyncDelayed(Ari.instance, i -> {
                            if (this.removeIfPlayInEditList(player)) {
                                player.sendMessage(TextTool.setHEXColorText("base.on-edit.timeout-cancel", FilePath.Lang));
                            }
                            holder.setTask(null);
                        }, 200L);
                        holder.setTask(cancellableTask);
                        //延迟执行
                    }
                }
                case LOCATION -> {
                    //reset LOCATION
                    Location newLocation = player.getLocation();
                    homeEditor.currentHome.setLocation(newLocation.toString());
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
                    homeEditor.currentHome.setShowMaterial(current.name());
                }
                case TOP_SLOT -> {
                    homeEditor.currentHome.setTopSlot(!homeEditor.currentHome.isTopSlot());
                    homeEditor.gui.getFunctionItems().forEach((k, v) -> {
                        if (v.getType().equals(FunctionType.TOP_SLOT)) {
                            List<String> lore = v.getLore();
                            List<TextComponent> list = lore.stream().map(p -> TextTool.setHEXColorText(
                                    p.replace(LocationKeyType.TOP_SLOT.getKey(),
                                    ConfigObjectUtils.getValue(homeEditor.currentHome.isTopSlot() ? "base.yes_re" : "base.no_re", FilePath.Lang.getName(), String.class, "null")))).toList();
                            clickMeta.lore(list);
                            clickItem.setItemMeta(clickMeta);
                        }
                    });
                }
                case SAVE -> {
                    //save
                    Log.debug("start saving home");
                    clickMeta.lore(List.of(TextTool.setHEXColorText("base.save.ing", FilePath.Lang)));
                    clickItem.setItemMeta(clickMeta);
                    CompletableFuture<Boolean> future = homeManager.modify(homeEditor.currentHome);
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
        if (event.getAction().equals(InventoryAction.COLLECT_TO_CURSOR) && event.isShiftClick() && event.isRightClick() && event.getView().getTopInventory().getHolder() instanceof CustomInventoryHolder) {
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
    public void onPlayerLeave(PlayerQuitEvent event) {
        this.removeIfPlayInEditList(event.getPlayer());
    }
    @EventHandler
    public void getNeedEditHomeChat(AsyncChatEvent event) {
        if (this.editStatus.isEmpty()) return;
        Player player = event.getPlayer();
        if (this.editStatus.stream().anyMatch(i -> i.getPlayer().equals(player))) {
            event.setCancelled(true);

            String message = TextTool.componentToString(event.message());
            List<String> value = ConfigObjectUtils.getValue("main.name-check", FilePath.HomeConfig.getName(), new TypeToken<List<String>>(){}.getType(), List.of());
            if(value == null) {
                Log.error("name-check list is null, check config");
                player.sendMessage(TextTool.setHEXColorText("base.on-error", FilePath.Lang));
                return;
            }
            if(!FormatUtils.checkName(message) || value.contains(message)) {
                player.sendMessage(TextTool.setHEXColorText("base.on-edit.rename.name-error", FilePath.Lang));
                return;
            }
            if(message.length() > ConfigObjectUtils.getValue("main.name-length", FilePath.HomeConfig.getName(), Integer.class, 15)) {
                player.sendMessage(TextTool.setHEXColorText("base.on-edit.rename.name-too-long", FilePath.Lang));
                return;
            }
            player.clearTitle();

            if (FunctionType.CANCEL.name().equals(message.toUpperCase())) {
                this.removeIfPlayInEditList(player);
                player.sendMessage(TextTool.setHEXColorText("base.on-edit.cancel", FilePath.Lang));
                return;
            }
            // 使用 removeIf 删除满足条件的元素
            this.editStatus.removeIf(i -> {
                if (i.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                    HomeEditor editor = (HomeEditor) i.getMeta();
                    editor.currentHome.setHomeName(message);
                    editor.open();
                    Log.debug("player: [" + player.getName() + "] edit home-name status removed");
                    return true;
                }
                return false;
            });
        }
    }
    protected synchronized boolean removeIfPlayInEditList(Player player) {
        return this.editStatus.removeIf(e -> e.getPlayer().getUniqueId().equals(player.getUniqueId()));
    }
}
