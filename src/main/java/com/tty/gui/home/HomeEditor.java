package com.tty.gui.home;

import com.tty.Ari;
import com.tty.dto.CustomInventoryHolder;
import com.tty.entity.menu.BaseMenu;
import com.tty.entity.menu.FunctionItems;
import com.tty.entity.menu.Mask;
import com.tty.entity.sql.ServerHome;
import com.tty.enumType.FilePath;
import com.tty.enumType.GuiType;
import com.tty.gui.BaseInventory;
import com.tty.lib.enum_type.IconKeyType;
import com.tty.lib.tool.FormatUtils;
import com.tty.lib.tool.PublicFunctionUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;

public class HomeEditor extends BaseInventory {

    public final ServerHome currentHome;

    public HomeEditor(ServerHome serverHome, Player player) {
        super(FormatUtils.yamlConvertToObj(Ari.C_INSTANCE.getObject(FilePath.HOME_EDIT_GUI.name()).saveToString(), BaseMenu.class), player);
        this.currentHome = serverHome;
    }

    @Override
    protected Mask getMasks() {
        return null;
    }

    @Override
    protected Map<String, FunctionItems> getFunctionItems() {
        Map<String, FunctionItems> functionItems = PublicFunctionUtils.deepCopyBySerialization(this.baseInstance.getFunctionItems());
        if (functionItems != null) {
            for (FunctionItems item : functionItems.values()) {
                switch (item.getType()) {
                    case ICON -> item.setMaterial(this.currentHome.getShowMaterial());
                    case RENAME -> item.setName(this.currentHome.getHomeName());
                    case LOCATION -> {
                        String name = item.getName();
                        Location location = FormatUtils.parseLocation(this.currentHome.getLocation());
                        for (IconKeyType keyType : IconKeyType.values()) {
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
                            IconKeyType.TOP_SLOT.getKey(),
                            Ari.C_INSTANCE.getValue(
                                    this.currentHome.isTopSlot() ? "base.yes_re":"base.no_re",
                                    FilePath.LANG))).toList());
                }
            }
        }
        return functionItems;
    }

    @Override
    protected CustomInventoryHolder createHolder() {
        return new CustomInventoryHolder(player, GuiType.HOMEEDIT, this);
    }

}
