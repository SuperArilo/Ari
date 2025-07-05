package com.tty.gui.warp;

import com.tty.Ari;
import com.tty.dto.CustomInventoryHolder;
import com.tty.entity.menu.FunctionItems;
import com.tty.entity.menu.Mask;
import com.tty.entity.menu.warp.WarpListGUI;
import com.tty.entity.sql.ServerWarp;
import com.tty.enumType.FilePath;
import com.tty.enumType.GuiType;
import com.tty.function.WarpManager;
import com.tty.gui.BasePageGui;
import com.tty.lib.dto.Page;
import com.tty.lib.enum_type.FunctionType;
import com.tty.lib.enum_type.LocationKeyType;
import com.tty.lib.tool.ComponentUtils;
import com.tty.lib.tool.FormatUtils;
import com.tty.lib.tool.Log;
import com.tty.tool.ConfigUtils;
import com.tty.tool.EconomyUtils;
import com.tty.tool.PermissionUtils;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class WarpList extends BasePageGui<ServerWarp> {

    public WarpListGUI gui;

    public WarpList(Player player) {
        super(player);
    }

    @Override
    public CompletableFuture<List<ServerWarp>> requestData() {
        return new WarpManager(true).getList(Page.create(this.pageNum, this.pageSize));
    }

    @Override
    protected void init() {
        this.gui = FormatUtils.yamlConvertToObj(
                ConfigUtils.getObject(FilePath.WarpList.getName()).saveToString(),
                WarpListGUI.class
        );
        this.pageSize = this.gui.getDataItems().getSlot().size();
        this.inventory = Bukkit.createInventory(
                new CustomInventoryHolder(player, GuiType.WARPLIST, this), this.gui.getRow() * 9,
                ComponentUtils.text(this.gui.getTitle(), player));
    }

    @Override
    protected Mask renderMasks() {
        return this.gui.getMask();
    }

    @Override
    protected Map<String, FunctionItems> renderFunctionItems() {
        return this.gui.getFunctionItems();
    }

    @Override
    protected void renderDataItem() {
        List<Integer> dataSlot = this.gui.getDataItems().getSlot();
        List<String> rawLore = this.gui.getDataItems().getLore();
        for (int i = 0;i < this.data.size();i++) {
            ServerWarp serverWarp = this.data.get(i);
            ItemStack itemStack = new ItemStack(Material.valueOf(serverWarp.getShowMaterial().toUpperCase()));
            ItemMeta itemMeta = itemStack.getItemMeta();
            if(itemMeta == null) {
                Log.warning("There is a problem with the warpID: [" + serverWarp.getWarpId() + "] of the player: [" + this.player.getName() + "]");
                Log.error("Skip the rendering warpId [" + serverWarp.getWarpId() + "] process...");
                continue;
            }
            itemMeta.displayName(ComponentUtils.text(serverWarp.getWarpName(), this.player));
            List<TextComponent> textComponents = new ArrayList<>();
            Location location = FormatUtils.parseLocation(serverWarp.getLocation());
            rawLore.stream().filter(line -> {
                for (LocationKeyType keyType : LocationKeyType.values()) {
                    if(keyType == LocationKeyType.PERMISSION && line.contains(keyType.getKey())) {
                        return ConfigUtils.getValue("main.permission", FilePath.WarpConfig, Boolean.class, false);
                    }
                    if(keyType == LocationKeyType.COST && line.contains(keyType.getKey())) {
                        return ConfigUtils.getValue("main.cost", FilePath.WarpConfig, Boolean.class, false) && !EconomyUtils.isNull();
                    }
                }
                return true;
            }).map(line -> {
                for (LocationKeyType keyType : LocationKeyType.values()) {
                    line = switch (keyType) {
                        case ID -> line.replace(keyType.getKey(), serverWarp.getWarpId());
                        case X -> line.replace(keyType.getKey(), FormatUtils.formatTwoDecimalPlaces(location.getX()));
                        case Y -> line.replace(keyType.getKey(), FormatUtils.formatTwoDecimalPlaces(location.getY()));
                        case Z -> line.replace(keyType.getKey(), FormatUtils.formatTwoDecimalPlaces(location.getZ()));
                        case WORLDNAME -> line.replace(keyType.getKey(), location.getWorld().getName());
                        case PLAYERNAME -> {
                            UUID uuid = UUID.fromString(serverWarp.getCreateBy());
                            String name;
                            Player onlinePlayer = Bukkit.getPlayer(uuid);
                            if (onlinePlayer != null) {
                                name = onlinePlayer.getName();
                            } else {
                                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                                name = offlinePlayer.getName() != null ? offlinePlayer.getName() : "null";
                            }
                            yield line.replace(keyType.getKey(), name);
                        }
                        case COST -> {
                            Double cost = serverWarp.getCost();
                            if(cost == null || cost == 0) {
                                yield line.replace(keyType.getKey(), "&afree");
                            } else {
                                yield line.replace(keyType.getKey(), cost.toString());
                            }
                        }
                        case TOP_SLOT ->
                                line.replace(LocationKeyType.TOP_SLOT.getKey(), serverWarp.isTopSlot() ? "base.yes_re":"base.no_re");
                        case PERMISSION -> {
                            boolean hasPermission = serverWarp.getPermission() == null ||
                                    serverWarp.getPermission().isEmpty() ||
                                    PermissionUtils.hasPermission(this.player, serverWarp.getPermission()) ||
                                    UUID.fromString(serverWarp.getCreateBy()).equals(this.player.getUniqueId());
                            yield line.replace(keyType.getKey(), ConfigUtils.getValue(hasPermission ? "base.yes_re":"base.no_re", FilePath.Lang));
                        }
                    };
                }
                return ComponentUtils.text(line, player);
            }).forEach(textComponents::add);
            itemMeta.lore(textComponents);
            itemMeta.getPersistentDataContainer().set(new NamespacedKey(Ari.instance, "warp_id"), PersistentDataType.STRING, serverWarp.getWarpId());
            itemMeta.getPersistentDataContainer().set(new NamespacedKey(Ari.instance, "type"), PersistentDataType.STRING, FunctionType.DATA.name());
            if (serverWarp.isTopSlot()) {
                itemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            itemStack.setItemMeta(itemMeta);
            this.inventory.setItem(dataSlot.get(i), itemStack);
        }
    }

}
