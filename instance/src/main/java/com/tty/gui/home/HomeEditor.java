package com.tty.gui.home;

import com.tty.dto.CustomInventoryHolder;
import com.tty.entity.menu.FunctionItems;
import com.tty.entity.menu.Mask;
import com.tty.entity.menu.home.HomeEditorGUI;
import com.tty.entity.sql.ServerHome;
import com.tty.enumType.FilePath;
import com.tty.enumType.GuiType;
import com.tty.gui.BaseGui;
import com.tty.lib.enum_type.LocationKeyType;
import com.tty.lib.tool.ComponentUtils;
import com.tty.lib.tool.FormatUtils;
import com.tty.lib.tool.PublicFunctionUtils;
import com.tty.tool.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;

public class HomeEditor extends BaseGui<HomeEditorGUI> {

    public final ServerHome currentHome;

    public HomeEditor(ServerHome serverHome, Player player) {
        super(player, FormatUtils.yamlConvertToObj(ConfigUtils.getObject(FilePath.HomeEditor.name()).saveToString(), HomeEditorGUI.class));
        this.currentHome = serverHome;
        this.inventory = Bukkit.createInventory(new CustomInventoryHolder(player, GuiType.HOMEEDIT, this), this.instance.getRow() * 9, ComponentUtils.text(this.instance.getTitle(), player));
    }

    @Override
    protected Mask renderMasks() {
        return this.instance.getMask();
    }

    @Override
    protected Map<String, FunctionItems> renderFunctionItems() {
        Map<String, FunctionItems> functionItems = PublicFunctionUtils.deepCopyBySerialization(this.instance.getFunctionItems());
        if (functionItems != null) {
            for (FunctionItems item : functionItems.values()) {
                switch (item.getType()) {
                    case ICON -> item.setMaterial(this.currentHome.getShowMaterial());
                    case RENAME -> item.setName(this.currentHome.getHomeName());
                    case LOCATION -> {
                        String name = item.getName();
                        Location location = FormatUtils.parseLocation(this.currentHome.getLocation());
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
                    case TOP_SLOT -> item.setLore(item.getLore().stream().map(lore -> lore.replace(
                            LocationKeyType.TOP_SLOT.getKey(),
                            ConfigUtils.getValue(
                                    this.currentHome.isTopSlot() ? "base.yes_re":"base.no_re",
                                    FilePath.Lang))).toList());
                }
            }
        }
        return functionItems;
    }
}
