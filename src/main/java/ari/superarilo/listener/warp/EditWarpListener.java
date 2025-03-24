package ari.superarilo.listener.warp;

import ari.superarilo.Ari;
import ari.superarilo.dto.CustomInventoryHolder;
import ari.superarilo.dto.OnEdit;
import ari.superarilo.entity.sql.ServerWarp;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.enumType.FunctionType;
import ari.superarilo.enumType.GuiType;
import ari.superarilo.enumType.TitleInputType;
import ari.superarilo.function.WarpManager;
import ari.superarilo.gui.warp.WarpEditor;
import ari.superarilo.gui.warp.WarpList;
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
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class EditWarpListener implements Listener {

    private final Map<UUID, OnEdit> editMap = new ConcurrentHashMap<>();

    @EventHandler
    public void editClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if(event.getSlot() > inventory.getSize()) return;

        if(inventory.getHolder() instanceof CustomInventoryHolder holder && holder.getType().equals(GuiType.WARPEDIT)) {
            if(event.getClick().equals(ClickType.SHIFT_RIGHT) || event.getClick().equals(ClickType.SHIFT_LEFT)) {
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

            FunctionType type = Ari.instance.objectConvert.ItemNBT_TypeCheck(clickMeta.getPersistentDataContainer().get(new NamespacedKey(Ari.instance, "type"), PersistentDataType.STRING));
            event.setCancelled(true);

            ServerWarp serverWarp = (ServerWarp) holder.getMeta();
            WarpManager warpManager = WarpManager.create(player);
            switch (type) {
                case REBACK -> {
                    inventory.close();
                    new WarpList(player).open();
                }
                case DELETE -> {
                    warpManager.deleteInstance(serverWarp.getWarpId());
                    inventory.close();
                    new WarpList(player).open();
                }
                case RENAME, COST, PERMISSION -> {
                    Audience.audience(player).showTitle(
                            TextTool.setPlayerTitle(
                                    Ari.instance.configManager.getValue("base.on-edit.title", FilePath.Lang, String.class),
                                    Ari.instance.configManager.getValue("base.on-edit.sub-title", FilePath.Lang, String.class),
                                    1000,
                                    10000 ,
                                    1000));
                    inventory.close();
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
                    if(cursor == null) return;
                    Material current = cursor.getType();
                    if(current.equals(Material.AIR)) return;
                    clickItem = new ItemStack(current);
                    clickItem.setItemMeta(clickMeta);
                    inventory.setItem(event.getSlot(), clickItem);
                    serverWarp.setShowMaterial(current.name());
                }
                case SAVE -> {
                    ItemStack finalClickItem = clickItem;
                    Log.debug("start saving warp id:" + serverWarp.getWarpId());
                    clickMeta.lore(List.of(TextTool.setHEXColorText("base.save.ing", FilePath.Lang)));
                    finalClickItem.setItemMeta(clickMeta);
                    CompletableFuture<Boolean> future = warpManager.modify(serverWarp);
                    Boolean status;
                    try {
                        status = future.get();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    if(status) {
                        clickMeta.lore(List.of(TextTool.setHEXColorText("base.save.done", FilePath.Lang)));
                        finalClickItem.setItemMeta(clickMeta);
                        Bukkit.getAsyncScheduler().runDelayed(Ari.instance, e ->{
                            clickMeta.lore(List.of());
                            finalClickItem.setItemMeta(clickMeta);
                        }, 1, TimeUnit.SECONDS);
                    } else {
                        clickMeta.lore(List.of(TextTool.setHEXColorText("base.save.error", FilePath.Lang)));
                        Log.error("save warp error id:" + serverWarp.getWarpId());
                    }
                }
            }
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
        List<String> value = Ari.instance.configManager.getValue("main.name-check", FilePath.WarpConfig, new TypeToken<List<String>>(){}.getType());
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
                    if(!Ari.instance.formatUtils.checkName(message) ||
                            value.contains(message) && onEdit.getType().equals(TitleInputType.RENAME) ||
                            !Ari.instance.formatUtils.checkName(message)) {
                        player.sendMessage(TextTool.setHEXColorText("base.on-edit.rename.name-error", FilePath.Lang));
                        return;
                    }
                    if(message.length() >  (Integer) Ari.instance.configManager.getValue("main.name-length", FilePath.WarpConfig, new TypeToken<Integer>(){}.getType()) &&
                            onEdit.getType().equals(TitleInputType.RENAME)) {
                        player.sendMessage(TextTool.setHEXColorText("base.on-edit.rename.name-too-long", FilePath.Lang));
                        return;
                    }
                    serverWarp.setWarpName(message);
                }
                case PERMISSION -> {
                     if(!Ari.instance.formatUtils.isValidPermissionNode(message)) {
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
