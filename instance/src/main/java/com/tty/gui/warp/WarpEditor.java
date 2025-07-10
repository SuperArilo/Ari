package com.tty.gui.warp;

import com.tty.dto.CustomInventoryHolder;
import com.tty.entity.menu.BaseMenu;
import com.tty.entity.menu.FunctionItems;
import com.tty.entity.menu.Mask;
import com.tty.entity.sql.ServerWarp;
import com.tty.enumType.FilePath;
import com.tty.enumType.GuiType;
import com.tty.gui.BaseInventory;
import com.tty.lib.enum_type.IconKeyType;
import com.tty.lib.tool.FormatUtils;
import com.tty.lib.tool.PublicFunctionUtils;
import com.tty.tool.ConfigUtils;
import com.tty.tool.EconomyUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;

public class WarpEditor extends BaseInventory {

    public final ServerWarp currentWarp;

    public WarpEditor(ServerWarp serverWarp, Player player) {
        super(FormatUtils.yamlConvertToObj(ConfigUtils.getObject(FilePath.WarpEditor.name()).saveToString(), BaseMenu.class), player);
        this.currentWarp = serverWarp;
    }

    @Override
    protected Mask getMasks() {
        return null;
    }

    @Override
    protected Map<String, FunctionItems> getFunctionItems() {
        Map<String, FunctionItems> functionItems = PublicFunctionUtils.deepCopyBySerialization(this.baseInstance.getFunctionItems());
        if(functionItems != null) {
            for (FunctionItems item : functionItems.values()) {
                switch (item.getType()) {
                    case ICON -> item.setMaterial(this.currentWarp.getShowMaterial());
                    case RENAME -> item.setName(this.currentWarp.getWarpName());
                    case LOCATION -> {
                        String name = item.getName();
                        Location location = FormatUtils.parseLocation(this.currentWarp.getLocation());
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
                    case PERMISSION -> {
                        String permission = this.currentWarp.getPermission();
                        item.setName(permission == null ? "":permission);
                    }
                    case COST -> {
                        if (EconomyUtils.isNull()) {
                            item.setName(ConfigUtils.getValue("server.message.no-economy", FilePath.Lang));
                            item.setMaterial("barrier");
                        } else {
                            Double cost = this.currentWarp.getCost();
                            item.setName(cost == null ? "":cost.toString());
                        }
                    }
                    case TOP_SLOT -> item.setLore(item.getLore().stream().map(lore -> lore.replace(
                            IconKeyType.TOP_SLOT.getKey(),
                            ConfigUtils.getValue(
                                    this.currentWarp.isTopSlot() ? "base.yes_re":"base.no_re",
                                    FilePath.Lang))).toList());
                }
            }
        }
        return functionItems;
    }

    @Override
    protected CustomInventoryHolder createHolder() {
        return new CustomInventoryHolder(player, GuiType.WARPEDIT, this);
    }

}
