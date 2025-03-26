package ari.superarilo.gui;

import ari.superarilo.Ari;
import ari.superarilo.entity.menu.FunctionItems;
import ari.superarilo.entity.menu.Mask;
import ari.superarilo.enumType.FunctionType;
import ari.superarilo.tool.TextTool;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
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

public abstract class BaseGui {

    protected final Player player;
    protected Inventory inventory;

    public BaseGui(Player player) {
        this.player = player;
    }
    public void open() {
        Bukkit.getRegionScheduler().run(Ari.instance, this.player.getLocation(), e -> {
            this.player.openInventory(this.inventory);
            Bukkit.getAsyncScheduler().runNow(Ari.instance, i -> {
                this.BaseRenderMasks(this.renderMasks());
                this.BaseRenderFunctionItems(this.renderFunctionItems());
            });
        });
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
    protected void BaseRenderMasks(Mask mask) {
        List<TextComponent> collect = mask.getLore().stream().map(i -> TextTool.setHEXColorText(i, this.player)).toList();
        for (Integer i : mask.getSlot()) {
            ItemStack itemStack = new ItemStack(Material.valueOf(mask.getMaterial().toUpperCase()));
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.displayName(TextTool.setHEXColorText(mask.getName(), this.player));
            itemMeta.getPersistentDataContainer().set(new NamespacedKey(Ari.instance, "type"), PersistentDataType.STRING, FunctionType.MASKICON.name());
            itemMeta.lore(collect);
            itemStack.setItemMeta(itemMeta);
            this.inventory.setItem(i, itemStack);
        }
    }
    protected void BaseRenderFunctionItems(Map<String, FunctionItems> functionItemMap) {
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

    /**
     * 渲染gui的mask-item方法
     * @return Mask类
     */
    protected abstract Mask renderMasks();

    /**
     * 渲染gui中带功能的item
     * @return Map<String, FunctionItems>类
     */
    protected abstract Map<String, FunctionItems> renderFunctionItems();

}
