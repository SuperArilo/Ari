package com.tty.gui.spawn;

import com.tty.dto.CustomInventoryHolder;
import com.tty.entity.menu.BaseMenu;
import com.tty.entity.menu.FunctionItems;
import com.tty.entity.menu.Mask;
import com.tty.entity.sql.ServerSpawn;
import com.tty.enumType.FilePath;
import com.tty.enumType.GuiType;
import com.tty.gui.BaseInventory;
import com.tty.lib.enum_type.IconKeyType;
import com.tty.lib.tool.FormatUtils;
import com.tty.lib.tool.PublicFunctionUtils;
import com.tty.tool.ConfigUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;

public class SpawnEditor extends BaseInventory {

    public final ServerSpawn serverSpawn;

    public SpawnEditor(ServerSpawn serverSpawn, Player player) {
        super(FormatUtils.yamlConvertToObj(ConfigUtils.getObject(FilePath.SpawnEditor.name()).saveToString(), BaseMenu.class), player);
        this.serverSpawn = serverSpawn;
    }

    @Override
    protected Mask getMasks() {
        return null;
    }

    @Override
    protected Map<String, FunctionItems> getFunctionItems() {
        Map<String, FunctionItems> functionItemsMap = PublicFunctionUtils.deepCopyBySerialization(this.baseInstance.getFunctionItems());
        if (functionItemsMap == null) return null;
        for (FunctionItems item : functionItemsMap.values()) {
            switch (item.getType()) {
                case ICON -> item.setMaterial(this.serverSpawn.getShowMaterial());
                case RENAME -> item.setName(this.serverSpawn.getSpawnName());
                case LOCATION -> {
                    String name = item.getName();
                    Location location = FormatUtils.parseLocation(this.serverSpawn.getLocation());
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
                    String permission = this.serverSpawn.getPermission();
                    item.setName(permission == null ? "":permission);
                }
                case TOP_SLOT -> item.setLore(item.getLore().stream().map(lore -> lore.replace(
                        IconKeyType.TOP_SLOT.getKey(),
                        ConfigUtils.getValue(
                                this.serverSpawn.isTopSlot() ? "base.yes_re":"base.no_re",
                                FilePath.Lang))).toList());
            }
        }
        return functionItemsMap;
    }

    @Override
    protected CustomInventoryHolder createHolder() {
        return new CustomInventoryHolder(player, GuiType.SPAWNEDIT, this);
    }

}
