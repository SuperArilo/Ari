package ari.superarilo.gui;

import ari.superarilo.Ari;
import ari.superarilo.entity.menu.FunctionItem;
import ari.superarilo.entity.menu.Mask;
import ari.superarilo.tool.TextTool;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InitGui {

    protected final Player player;
    protected Inventory inventory;

    public InitGui(Player player) {
        this.player = player;
    }
    public void open() {
        this.player.openInventory(this.inventory);
    }

    protected List<String> parseLayout(List<String> layout) {
        List<String> result = new ArrayList<>(layout.size() * 9);
        StringBuilder bracketContent = null;
        for (String line : layout) {
            boolean inBrackets = false;
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                if (c == '(') {
                    if (!inBrackets) {
                        inBrackets = true;
                        bracketContent = new StringBuilder();
                    }
                } else if (c == ')') {
                    if (inBrackets) {
                        inBrackets = false;
                        result.add(bracketContent.toString());
                        bracketContent = null;
                    }
                } else if (inBrackets) {
                    bracketContent.append(c);
                } else {
                    result.add(String.valueOf(c));
                }
            }
            if (inBrackets) {
                bracketContent = null;
            }
        }
        return result;
    }
    protected void renderMasks(Mask mask) {
        this.player.getScheduler().run(Ari.instance, o -> {
            List<TextComponent> collect = mask.getLore().stream().map(i -> TextTool.setHEXColorText(i, this.player)).toList();
            for (Integer i : mask.getSlot()) {
                ItemStack itemStack = new ItemStack(Material.valueOf(mask.getMaterial().toUpperCase()));
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.displayName(TextTool.setHEXColorText(mask.getName(), this.player));
                itemMeta.lore(collect);
                itemStack.setItemMeta(itemMeta);
                this.inventory.setItem(i, itemStack);
            }
        }, () -> this.inventory.close());
    }
    protected void renderFunctionItems(Map<String, FunctionItem> functionItemMap) {
        functionItemMap.forEach((k, v) -> {
            ItemStack o = new ItemStack(Material.valueOf(v.getMaterial().toUpperCase()));
            ItemMeta mo = o.getItemMeta();
            mo.displayName(TextTool.setHEXColorText(v.getName(), this.player));
            mo.lore(v.getLore().stream().map(q -> TextTool.setHEXColorText(q, this.player)).toList());
            mo.getPersistentDataContainer().set(new NamespacedKey(Ari.instance, "type"), PersistentDataType.STRING, v.getType().name());
            o.setItemMeta(mo);
            for (Integer integer : v.getSlot()) {
                this.inventory.setItem(integer, o);
            }
        });
    }
}
