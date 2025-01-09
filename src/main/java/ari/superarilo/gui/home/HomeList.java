package ari.superarilo.gui.home;

import ari.superarilo.Ari;
import ari.superarilo.dto.CustomInventoryHolder;
import ari.superarilo.entity.menu.FunctionItem;
import ari.superarilo.entity.menu.Mask;
import ari.superarilo.entity.menu.home.HomeListGUI;
import ari.superarilo.entity.sql.PlayerHome;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.enumType.FunctionType;
import ari.superarilo.enumType.GuiType;
import ari.superarilo.function.HomeManager;
import ari.superarilo.gui.BaseGui;
import ari.superarilo.tool.Log;
import ari.superarilo.tool.TextTool;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;


public class HomeList extends BaseGui {
    private final HomeListGUI gui;
    private final List<String> addLore = List.of("&7左击: &6传送", "&7右击: &6编辑");

    public HomeList(Player player) {
        super(player);
        this.gui = Ari.instance.objectConvert.yamlConvertToObj(Ari.instance.configManager.getObject(FilePath.HomeList.getName()).saveToString(), HomeListGUI.class);
        this.inventory = Bukkit.createInventory(new CustomInventoryHolder(player, GuiType.HOMELIST, "HomeGUI"), this.gui.getRow() * 9, TextTool.setHEXColorText(this.gui.getTitle(), player));
    }

    public void open() {
        super.open();
        Log.debug(Level.INFO, "start render home list");
        long start = System.currentTimeMillis();
        List<Integer> dataSlot = this.gui.getDataSlot();
        List<PlayerHome> playerHomes = HomeManager.create(this.player).asyncGetHomeList();
        for (int i = 0; i < playerHomes.size(); i++) {
            PlayerHome ph = playerHomes.get(i);
            ItemStack itemStack = new ItemStack(Material.valueOf(ph.getShowMaterial().toUpperCase()));
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.displayName(TextTool.setHEXColorText(ph.getHomeName(), this.player));
            List<TextComponent> textComponents = new ArrayList<>();
            textComponents.add(TextTool.setHEXColorText("&2ID: " + "&6" + ph.getHomeId(), this.player));
            textComponents.add(TextTool.setHEXColorText(TextTool.XYZText(ph.getX(), ph.getY(), ph.getZ())));
            textComponents.add(TextTool.setHEXColorText("&2世界: " + "&6" + ph.getWorld(), this.player));
            textComponents.addAll(this.addLore.stream().map(k -> TextTool.setHEXColorText(k, this.player)).toList());
            itemMeta.lore(textComponents);
            itemMeta.getPersistentDataContainer().set(new NamespacedKey(Ari.instance, "home_id"), PersistentDataType.STRING, ph.getHomeId());
            itemMeta.getPersistentDataContainer().set(new NamespacedKey(Ari.instance, "type"), PersistentDataType.STRING, FunctionType.DATA.name());
            itemStack.setItemMeta(itemMeta);
            this.inventory.setItem(dataSlot.get(i), itemStack);
        }
        Log.debug(Level.INFO, "render time: " + (System.currentTimeMillis() - start) + "ms");
    }

    @Override
    protected Mask getMask() {
        return this.gui.getMask();
    }

    @Override
    protected Map<String, FunctionItem> getFunctionItems() {
        return this.gui.getFunctionItems();
    }
}
