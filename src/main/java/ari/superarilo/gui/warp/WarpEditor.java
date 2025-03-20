package ari.superarilo.gui.warp;

import ari.superarilo.Ari;
import ari.superarilo.dto.CustomInventoryHolder;
import ari.superarilo.entity.menu.FunctionItems;
import ari.superarilo.entity.menu.Mask;
import ari.superarilo.entity.menu.warp.WarpEditorGUI;
import ari.superarilo.entity.sql.ServerWarp;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.enumType.GuiType;
import ari.superarilo.enumType.LocationKeyType;
import ari.superarilo.gui.BaseGui;
import ari.superarilo.tool.TextTool;
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
    protected Mask getMask() {
        return this.gui.getMask();
    }

    @Override
    protected Map<String, FunctionItems> getFunctionItems() {
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
                        Integer cost = this.currentWarp.getCost();
                        item.setName(cost == null ? "0":cost.toString());
                    }
                }
            }
        }
        return functionItems;
    }

    @Override
    public void renderDataItem() {}
}
