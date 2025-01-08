package ari.superarilo.gui.home;

import ari.superarilo.Ari;
import ari.superarilo.dto.CustomInventoryHolder;
import ari.superarilo.entity.menu.home.HomeEditorGUI;
import ari.superarilo.entity.sql.PlayerHome;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.enumType.GuiType;
import ari.superarilo.gui.InitGui;
import ari.superarilo.tool.TextTool;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HomeEditor extends InitGui {

    private final HomeEditorGUI gui;
    private final PlayerHome currentHome;

    public HomeEditor( PlayerHome playerHome,Player player) {
        super(player);
        this.currentHome = playerHome;
        this.gui = Ari.instance.objectConvert.yamlConvertToObj(Ari.instance.configManager.getObject(FilePath.HomeEditor.getName()).saveToString(), HomeEditorGUI.class);
        this.inventory = Bukkit.createInventory(new CustomInventoryHolder(player, GuiType.EDITHOME, "EditorHomeGui"), this.gui.getRow() * 9, TextTool.setHEXColorText(this.gui.getTitle(), player));
    }

    @Override
    public void open() {
        super.open();
        this.renderMasks(this.gui.getMask());
        this.renderFunctionItems(this.gui.getFunctionItems());
    }
}
