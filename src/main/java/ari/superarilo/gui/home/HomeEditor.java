package ari.superarilo.gui.home;

import ari.superarilo.Ari;
import ari.superarilo.dto.CustomInventoryHolder;
import ari.superarilo.entity.menu.FunctionItem;
import ari.superarilo.entity.menu.Mask;
import ari.superarilo.entity.menu.home.HomeEditorGUI;
import ari.superarilo.entity.sql.PlayerHome;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.enumType.GuiType;
import ari.superarilo.gui.BaseGui;
import ari.superarilo.tool.TextTool;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class HomeEditor extends BaseGui {
    private final HomeEditorGUI gui;
    private final PlayerHome currentHome;

    public HomeEditor(PlayerHome playerHome, Player player) {
        super(player);
        this.currentHome = playerHome;
        this.gui = Ari.instance.objectConvert.yamlConvertToObj(Ari.instance.configManager.getObject(FilePath.HomeEditor.getName()).saveToString(), HomeEditorGUI.class);
        this.inventory = Bukkit.createInventory(new CustomInventoryHolder(player, GuiType.EDITHOME, this.currentHome), this.gui.getRow() * 9, TextTool.setHEXColorText(this.gui.getTitle(), player));
    }

    @Override
    protected Mask getMask() {
        return this.gui.getMask();
    }

    @Override
    protected Map<String, FunctionItem> getFunctionItems() {
        Map<String, FunctionItem> functionItems = this.gui.getFunctionItems();
        if (functionItems != null) {
            for (FunctionItem item : functionItems.values()) {
                switch (item.getType()) {
                    case ICON -> item.setMaterial(this.currentHome.getShowMaterial());
                    case RENAME -> item.setName(this.currentHome.getHomeName());
                    case LOCATION -> item.setName(TextTool.XYZText(this.currentHome.getX(), this.currentHome.getY(), this.currentHome.getZ()));
                }
            }
        }
        return functionItems;
    }

    @Override
    public void renderDataItem() {

    }
}
