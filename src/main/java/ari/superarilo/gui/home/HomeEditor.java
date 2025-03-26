package ari.superarilo.gui.home;

import ari.superarilo.Ari;
import ari.superarilo.dto.CustomInventoryHolder;
import ari.superarilo.entity.menu.FunctionItems;
import ari.superarilo.entity.menu.Mask;
import ari.superarilo.entity.menu.home.HomeEditorGUI;
import ari.superarilo.entity.sql.ServerHome;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.enumType.GuiType;
import ari.superarilo.enumType.LocationKeyType;
import ari.superarilo.gui.BaseGui;
import ari.superarilo.tool.TextTool;
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
        this.gui = Ari.instance.objectConvert.yamlConvertToObj(Ari.instance.configManager.getObject(FilePath.HomeEditor.getName()).saveToString(), HomeEditorGUI.class);
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
                        Location location = Ari.instance.objectConvert.parseLocation(this.currentHome.getLocation());
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
                }
            }
        }
        return functionItems;
    }
}
