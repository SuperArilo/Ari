package com.tty.gui.warp;

import com.tty.dto.CustomInventoryHolder;
import com.tty.entity.menu.FunctionItems;
import com.tty.entity.menu.Mask;
import com.tty.entity.menu.warp.WarpEditorGUI;
import com.tty.entity.sql.ServerWarp;
import com.tty.enumType.FilePath;
import com.tty.enumType.GuiType;
import com.tty.gui.BaseGui;
import com.tty.lib.enum_type.LocationKeyType;
import com.tty.lib.tool.FormatUtils;
import com.tty.lib.tool.PublicFunctionUtils;
import com.tty.tool.ConfigObjectUtils;
import com.tty.tool.EconomyUtils;
import com.tty.tool.TextTool;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;

public class WarpEditor extends BaseGui {

    public final WarpEditorGUI gui;
    public final ServerWarp currentWarp;

    public WarpEditor(ServerWarp serverWarp, Player player) {
        super(player);
        this.currentWarp = serverWarp;
        this.gui = ConfigObjectUtils.yamlConvertToObj(ConfigObjectUtils.getObject(FilePath.WarpEditor.getName()).saveToString(), WarpEditorGUI.class);
        this.inventory = Bukkit.createInventory(new CustomInventoryHolder(player, GuiType.WARPEDIT, this), this.gui.getRow() * 9, TextTool.setHEXColorText(this.gui.getTitle()));
    }

    @Override
    protected Mask renderMasks() {
        return this.gui.getMask();
    }

    @Override
    protected Map<String, FunctionItems> renderFunctionItems() {
        Map<String, FunctionItems> functionItems = PublicFunctionUtils.deepCopyBySerialization(this.gui.getFunctionItems());
        if(functionItems != null) {
            for (FunctionItems item : functionItems.values()) {
                switch (item.getType()) {
                    case ICON -> item.setMaterial(this.currentWarp.getShowMaterial());
                    case RENAME -> item.setName(this.currentWarp.getWarpName());
                    case LOCATION -> {
                        String name = item.getName();
                        Location location = ConfigObjectUtils.parseLocation(this.currentWarp.getLocation());
                        for (LocationKeyType keyType : LocationKeyType.values()) {
                            name = switch (keyType) {
                                case X -> name.replace(keyType.getKey(), FormatUtils.formatTwoDecimalPlaces(location.getX()));
                                case Y -> name.replace(keyType.getKey(), FormatUtils.formatTwoDecimalPlaces(location.getY()));
                                case Z -> name.replace(keyType.getKey(), FormatUtils.formatTwoDecimalPlaces(location.getZ()));
                                default -> name;
                            };
                        }
                        item.setName(name);
                    }
                    case PERMISSION -> {
                        String permission = this.currentWarp.getPermission();
                        item.setName(permission == null ? "":permission);
                    }
                    case COST -> {
                        if (EconomyUtils.isNull()) {
                            item.setName(ConfigObjectUtils.getValue("server.message.no-economy", FilePath.Lang.getName(), String.class, "null"));
                            item.setMaterial("barrier");
                        } else {
                            Double cost = this.currentWarp.getCost();
                            item.setName(cost == null ? "":cost.toString());
                        }
                    }
                    case TOP_SLOT -> item.setLore(item.getLore().stream().map(lore -> lore.replace(
                            LocationKeyType.TOP_SLOT.getKey(),
                            ConfigObjectUtils.getValue(
                                    this.currentWarp.isTopSlot() ? "base.yes_re":"base.no_re",
                                    FilePath.Lang.getName(),
                                    String.class,
                                    "null"))).toList());
                }
            }
        }
        return functionItems;
    }
}
