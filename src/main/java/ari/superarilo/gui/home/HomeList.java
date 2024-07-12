package ari.superarilo.gui.home;

import ari.superarilo.Ari;
import ari.superarilo.dto.CustomInventoryHolder;
import ari.superarilo.entity.menu.home.HomeListGUI;
import ari.superarilo.entity.menu.home.RenderItem;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.enumType.GuiType;
import ari.superarilo.gui.InitGui;
import ari.superarilo.tool.TextTool;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;



import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class HomeList extends InitGui {
    private final Ari instance;
    private final HomeListGUI gui;
    private final YamlConfiguration config;
    private final List<String> itemLayout;


    public HomeList(Ari instance, Player player) {
        super(player);
        this.instance = instance;
        this.config = this.instance.getConfigFiles().getObject(FilePath.HomeList.getName());

        long startTime = System.currentTimeMillis();
        this.gui = instance.getGsonConvert().yamlConvertToObj(this.config.saveToString(), HomeListGUI.class);
        this.instance.getLogger().log(Level.INFO, "转换 Time: " + (System.currentTimeMillis() - startTime));

        this.itemLayout = this.parseLayout(gui.getLayout());
        this.inventory = Bukkit.createInventory(new CustomInventoryHolder(player, GuiType.HOMELIST), this.itemLayout.size(), TextTool.setHEXColorText("title", FilePath.HomeList, player));
    }

    public void open() {
        this.player.openInventory(this.inventory);

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < this.itemLayout.size(); i++) {
            RenderItem renderItem = this.gui.getItems().get(this.itemLayout.get(i));
            if (renderItem == null) continue;
            ItemStack itemStack = new ItemStack(Material.valueOf(renderItem.getMaterial().toUpperCase()));
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.displayName(TextTool.setHEXColorText(renderItem.getName()));
            itemMeta.lore(renderItem.getLore().stream().map(TextTool::setHEXColorText).collect(Collectors.toList()));
            itemStack.setItemMeta(itemMeta);
            this.inventory.setItem(i, itemStack);
        }
        this.instance.getLogger().log(Level.INFO, "Render Time: " + (System.currentTimeMillis() - startTime));
    }
}
