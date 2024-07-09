package ari.superarilo.gui.home;

import ari.superarilo.Ari;
import ari.superarilo.dto.CustomInventoryHolder;
import ari.superarilo.dto.ItemsV;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.gui.InitGui;
import ari.superarilo.tool.TextTool;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class HomeList extends InitGui {
    private final Ari instance;
    private final FileConfiguration config;
    private final List<String> rawLayout;

    public HomeList(Ari instance, Player player) {
        super(player);
        this.instance = instance;
        this.config = this.instance.getConfigFiles().getObject(FilePath.HomeList.getName());

        this.rawLayout = this.parseLayout(this.config.getStringList("layout"));
        this.inventory = Bukkit.createInventory(new CustomInventoryHolder(player, "114514"), this.rawLayout.size(), TextTool.setHEXColorText("title", FilePath.HomeList));
    }

    public void open() {
        this.player.openInventory(this.inventory);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < this.rawLayout.size(); i++) {
            ConfigurationSection section = this.config.getConfigurationSection("items." + this.rawLayout.get(i));
            if (section == null) continue;
            ItemsV itemsV = this.instance.getGsonConvert().mapConvertTo(section.getValues(false), ItemsV.class);
            if (itemsV == null) continue;
            ItemStack itemStack = new ItemStack(Material.valueOf(itemsV.getMaterial().toUpperCase()));
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.displayName(TextTool.setHEXColorText(itemsV.getName()));
            itemMeta.lore(itemsV.getLore().stream().map(TextTool::setHEXColorText).collect(Collectors.toList()));
            itemStack.setItemMeta(itemMeta);
            this.inventory.setItem(i, itemStack);
        }
        this.instance.getLogger().log(Level.INFO, "Render Time: " + (System.currentTimeMillis() - startTime));
    }
}
