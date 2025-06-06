package com.tty.gui.warp;

import com.tty.Ari;
import com.tty.dto.CustomInventoryHolder;
import com.tty.entity.menu.FunctionItems;
import com.tty.entity.menu.Mask;
import com.tty.entity.menu.warp.WarpEditorGUI;
import com.tty.entity.sql.ServerWarp;
import com.tty.enumType.FilePath;
import com.tty.enumType.GuiType;
import com.tty.enumType.LocationKeyType;
import com.tty.gui.BaseGui;
import com.tty.tool.TextTool;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;

public class WarpEditor extends BaseGui {

    private final WarpEditorGUI gui;
    private final ServerWarp currentWarp;

    public WarpEditor(ServerWarp serverWarp, Player player) {
        super(player);
        this.currentWarp = serverWarp;
        this.gui = Ari.instance.objectConvert.yamlConvertToObj(Ari.instance.configManager.getObject(FilePath.WarpEditor.getName()).saveToString(), WarpEditorGUI.class);
        this.inventory = Bukkit.createInventory(new CustomInventoryHolder(player, GuiType.WARPEDIT, this.currentWarp), this.gui.getRow() * 9, TextTool.setHEXColorText(this.gui.getTitle()));
    }

    @Override
    protected Mask renderMasks() {
        return this.gui.getMask();
    }

    @Override
    protected Map<String, FunctionItems> renderFunctionItems() {
        Map<String, FunctionItems> functionItems = this.gui.getFunctionItems();
        if(functionItems != null) {
            for (FunctionItems item : functionItems.values()) {
                switch (item.getType()) {
                    case ICON -> item.setMaterial(this.currentWarp.getShowMaterial());
                    case RENAME -> item.setName(this.currentWarp.getWarpName());
                    case LOCATION -> {
                        String name = item.getName();
                        Location location = Ari.instance.objectConvert.parseLocation(this.currentWarp.getLocation());
                        for (LocationKeyType keyType : LocationKeyType.values()) {
                            name = switch (keyType) {
                                case X -> name.replace(keyType.getKey(), Ari.instance.formatUtils.formatTwoDecimalPlaces(location.getX()));
                                case Y -> name.replace(keyType.getKey(), Ari.instance.formatUtils.formatTwoDecimalPlaces(location.getY()));
                                case Z -> name.replace(keyType.getKey(), Ari.instance.formatUtils.formatTwoDecimalPlaces(location.getZ()));
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
                        if (Ari.instance.economyUtils.isNull()) {
                            item.setName(Ari.instance.configManager.getValue("server.message.no-economy", FilePath.Lang, String.class));
                            item.setMaterial("barrier");
                        } else {
                            Double cost = this.currentWarp.getCost();
                            item.setName(cost == null ? "":cost.toString());
                        }
                    }
                }
            }
        }
        return functionItems;
    }
}
