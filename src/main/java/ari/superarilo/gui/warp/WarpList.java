package ari.superarilo.gui.warp;

import ari.superarilo.Ari;
import ari.superarilo.dto.CustomInventoryHolder;
import ari.superarilo.entity.menu.FunctionItems;
import ari.superarilo.entity.menu.Mask;
import ari.superarilo.entity.menu.warp.WarpListGUI;
import ari.superarilo.entity.sql.ServerWarp;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.enumType.FunctionType;
import ari.superarilo.enumType.GuiType;
import ari.superarilo.enumType.LocationKeyType;
import ari.superarilo.function.impl.WarpManager;
import ari.superarilo.gui.BasePageGui;
import ari.superarilo.tool.Log;
import ari.superarilo.tool.TextTool;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.logging.Level;

public class WarpList extends BasePageGui<ServerWarp> {

    private final WarpListGUI gui;

    public WarpList(Player player) {
        super(player);
        this.gui = Ari.instance.objectConvert.yamlConvertToObj(
                Ari.instance.configManager.getObject(FilePath.WarpList.getName()).saveToString(),
                WarpListGUI.class
        );
        this.pageSize = this.gui.getDataItems().getSlot().size();
        this.inventory = Bukkit.createInventory(new CustomInventoryHolder(player, GuiType.WARPLIST, this), this.gui.getRow() * 9, TextTool.setHEXColorText(this.gui.getTitle(), player));
        WarpManager.create(this.player.getUniqueId().toString())
                .asyncGetList(this.pageNum, this.pageSize)
                .thenAccept(list -> {
                    this.data = list;
                    this.updateGui(this.gui.getDataItems().getSlot());
                });
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
        Log.debug(Level.INFO, "---------- render warp list ----------");
        long start = System.currentTimeMillis();
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
            itemMeta.displayName(TextTool.setHEXColorText(serverWarp.getWarpName(), this.player));
            List<TextComponent> textComponents = new ArrayList<>();
            Location location = Ari.instance.objectConvert.parseLocation(serverWarp.getLocation());
            rawLore.stream().filter(line -> {
                for (LocationKeyType keyType : LocationKeyType.values()) {
                    if(keyType == LocationKeyType.PERMISSION && line.contains(keyType.getKey())) {
                        return Ari.instance.configManager.getValue("main.permission", FilePath.WarpConfig, Boolean.class);
                    }
                    if(keyType == LocationKeyType.COST && line.contains(keyType.getKey())) {
                        return (Boolean) Ari.instance.configManager.getValue("main.cost", FilePath.WarpConfig, Boolean.class) && !Ari.instance.economyUtils.isNull();
                    }
                }
                return true;
            }).map(line -> {
                for (LocationKeyType keyType : LocationKeyType.values()) {
                    line = switch (keyType) {
                        case ID -> line.replace(keyType.getKey(), serverWarp.getWarpId());
                        case X -> line.replace(keyType.getKey(), Ari.instance.formatUtils.formatTwoDecimalPlaces(location.getX()));
                        case Y -> line.replace(keyType.getKey(), Ari.instance.formatUtils.formatTwoDecimalPlaces(location.getY()));
                        case Z -> line.replace(keyType.getKey(), Ari.instance.formatUtils.formatTwoDecimalPlaces(location.getZ()));
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
                        case PERMISSION -> {
                            boolean hasPermission = serverWarp.getPermission() == null ||
                                    serverWarp.getPermission().isEmpty() ||
                                    Ari.instance.permissionUtils.hasPermission(this.player, serverWarp.getPermission()) ||
                                    UUID.fromString(serverWarp.getCreateBy()).equals(this.player.getUniqueId());
                            yield line.replace(keyType.getKey(), Ari.instance.configManager.getValue(hasPermission ? "base.yes_re":"base.no_re", FilePath.Lang, String.class));
                        }
                    };
                }
                return TextTool.setHEXColorText(line, player);
            }).forEach(textComponents::add);
            itemMeta.lore(textComponents);
            itemMeta.getPersistentDataContainer().set(new NamespacedKey(Ari.instance, "warp_id"), PersistentDataType.STRING, serverWarp.getWarpId());
            itemMeta.getPersistentDataContainer().set(new NamespacedKey(Ari.instance, "type"), PersistentDataType.STRING, FunctionType.DATA.name());
            itemStack.setItemMeta(itemMeta);
            this.inventory.setItem(dataSlot.get(i), itemStack);
        }
        Log.debug(Level.INFO, "---------- render time: " + (System.currentTimeMillis() - start) + "ms ----------");
    }

    @Override
    public void prev() {
        this.pageNum--;
        if(this.pageNum <= 0) {
            this.player.sendMessage(TextTool.setHEXColorText("base.page-change.none-prev", FilePath.Lang));
            Log.debug("warp list: 第一页");
            this.pageNum = 1;
            return;
        }
        WarpManager.create(this.player.getUniqueId().toString())
                .asyncGetList(this.pageNum, this.pageSize)
                .thenAccept(list -> {
                    this.data = list;
                    this.updateGui(this.gui.getDataItems().getSlot());
                });
    }

    @Override
    public void next() {
        this.pageNum++;
        WarpManager.create(this.player.getUniqueId().toString())
                .asyncGetList(this.pageNum, this.pageSize)
                .thenAccept(list -> {
                    if (list.isEmpty()) {
                        this.player.sendMessage(TextTool.setHEXColorText("base.page-change.none-next", FilePath.Lang));
                        Log.debug("warp list: 最后一页");
                        this.pageNum--;
                    } else {
                        this.data = list;
                        this.updateGui(this.gui.getDataItems().getSlot());
                    }
                });
    }
}
