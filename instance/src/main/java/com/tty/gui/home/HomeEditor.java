package com.tty.gui.home;

import com.tty.dto.CustomInventoryHolder;
import com.tty.entity.menu.FunctionItems;
import com.tty.entity.menu.Mask;
import com.tty.entity.menu.home.HomeEditorGUI;
import com.tty.entity.sql.ServerHome;
import com.tty.enumType.FilePath;
import com.tty.enumType.GuiType;
import com.tty.lib.enum_type.LocationKeyType;
import com.tty.gui.BaseGui;
import com.tty.tool.ConfigObjectUtils;
import com.tty.lib.tool.FormatUtils;
import com.tty.tool.TextTool;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;

public class HomeEditor extends BaseGui {
    private final HomeEditorGUI gui;
    private final ServerHome currentHome;

    public HomeEditor(ServerHome serverHome, Player player) {
        super(player);
        this.currentHome = serverHome;
        this.gui = ConfigObjectUtils.yamlConvertToObj(ConfigObjectUtils.getObject(FilePath.HomeEditor.getName()).saveToString(), HomeEditorGUI.class);
        this.inventory = Bukkit.createInventory(new CustomInventoryHolder(player, GuiType.HOMEEDIT, this.currentHome), this.gui.getRow() * 9, TextTool.setHEXColorText(this.gui.getTitle(), player));
    }

    @Override
    protected Mask renderMasks() {
        return this.gui.getMask();
    }

    @Override
    protected Map<String, FunctionItems> renderFunctionItems() {
        Map<String, FunctionItems> functionItems = this.gui.getFunctionItems();
        if (functionItems != null) {
            for (FunctionItems item : functionItems.values()) {
                switch (item.getType()) {
                    case ICON -> item.setMaterial(this.currentHome.getShowMaterial());
                    case RENAME -> item.setName(this.currentHome.getHomeName());
                    case LOCATION -> {
                        String name = item.getName();
                        Location location = ConfigObjectUtils.parseLocation(this.currentHome.getLocation());
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
                }
            }
        }
        return functionItems;
    }
}
