package com.tty.listener.warp;

import com.tty.Ari;
import com.tty.dto.CustomInventoryHolder;
import com.tty.entity.sql.ServerWarp;
import com.tty.enumType.FilePath;
import com.tty.enumType.GuiType;
import com.tty.command.check.TeleportCheck;
import com.tty.function.Teleport;
import com.tty.gui.warp.WarpEditor;
import com.tty.gui.warp.WarpList;
import com.tty.lib.enum_type.FunctionType;
import com.tty.lib.enum_type.LangType;
import com.tty.lib.enum_type.TeleportType;
import com.tty.lib.tool.ComponentUtils;
import com.tty.lib.tool.FormatUtils;
import com.tty.lib.tool.Log;
import com.tty.listener.BaseGuiListener;
import com.tty.tool.*;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;
import java.util.UUID;


public class WarpListListener extends BaseGuiListener {

    private final NamespacedKey TYPE_KEY = new NamespacedKey(Ari.instance, "type");
    private final NamespacedKey WARP_ID_KEY = new NamespacedKey(Ari.instance, "warp_id");

    public WarpListListener(GuiType guiType) {
        super(guiType);
    }


    @Override
    public void passClick(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        assert currentItem != null;
        Inventory inventory = event.getInventory();
        CustomInventoryHolder holder = (CustomInventoryHolder) inventory.getHolder();
        assert holder != null;

        FunctionType type = FormatUtils.ItemNBT_TypeCheck(currentItem.getItemMeta().getPersistentDataContainer().get(this.TYPE_KEY, PersistentDataType.STRING));
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
                ServerWarp instance = first.get();
                boolean isOwner = UUID.fromString(instance.getCreateBy()).equals(player.getUniqueId());
                ClickType eventClick = event.getClick();
                if(eventClick.isLeftClick()) {
                    String permission = instance.getPermission();
                    if(permission != null && !permission.isEmpty()) {
                        boolean hasPermission = PermissionUtils.hasPermission(player, permission);
                        if (!hasPermission && !isOwner) {
                            player.sendMessage(ComponentUtils.text(ConfigUtils.getValue("function.warp.no-permission-teleport", FilePath.Lang)));
                            return;
                        }
                    }
                    Location targetLocation = FormatUtils.parseLocation(instance.getLocation());
                    Teleport.create(player,
                                    targetLocation,
                                    ConfigUtils.getValue("main.teleport.delay", FilePath.WarpConfig, Integer.class, 3))
                            .before(t -> {
                                if(!EconomyUtils.hasEnoughBalance(player, instance.getCost()) && !isOwner && ConfigUtils.getValue("main.permission", FilePath.WarpConfig, Boolean.class, true)) {
                                    player.sendMessage(ComponentUtils.text(ConfigUtils.getValue("function.warp.not-enough-money", FilePath.Lang)));
                                    t.cancel();
                                }
                                if(!TeleportCheck.preCheckStatus(player, targetLocation, 200L)) {
                                    t.cancel();
                                }
                            })
                            .aborted(() -> TeleportCheck.remove(player, targetLocation,TeleportType.POINT))
                            .teleport()
                            .after(() -> {
                                //判断是否是地标拥有者或者是不是op，如果是则不扣
                                if(!isOwner &&
                                        !player.isOp() &&
                                        ConfigUtils.getValue("main.cost", FilePath.WarpConfig, Boolean.class, false) &&
                                        !EconomyUtils.isNull()) {
                                    EconomyUtils.withdrawPlayer(player, instance.getCost());
                                    String value = ConfigUtils.getValue("teleport.costed", FilePath.Lang);
                                    player.sendMessage(ComponentUtils.text(value.replace(LangType.COSTED.getType(), instance.getCost().toString() + EconomyUtils.getNamePlural())));
                                }
                                TeleportCheck.remove(player, targetLocation, TeleportType.POINT);
                            });
                    inventory.close();
                } else if(eventClick.isRightClick()) {
                    if(isOwner || player.isOp()) {
                        inventory.close();
                        new WarpEditor(instance, player).open();
                    } else {
                        player.sendMessage(ComponentUtils.text(ConfigUtils.getValue("function.warp.no-permission-edit", FilePath.Lang)));
                    }
                }
            }
            case PREV -> warpList.prev();
            case NEXT -> warpList.next();
        }
    }
}
