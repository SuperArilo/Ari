package com.tty.gui;

import com.tty.Ari;
import com.tty.entity.menu.FunctionItems;
import com.tty.entity.menu.Mask;
import com.tty.lib.enum_type.FunctionType;
import com.tty.lib.tool.ComponentUtils;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Map;

public abstract class BaseGui<I> {

    public final I instance;
    protected final Player player;
    protected Inventory inventory;

    private final NamespacedKey renderType = new NamespacedKey(Ari.instance, "type");

    public BaseGui(Player player, I instance) {
        this.player = player;
        this.instance = instance;
    }
    public void open() {
        this.player.openInventory(this.inventory);
        this.startRender();
    }

    private void startRender() {
        this.BaseRenderMasks(this.renderMasks());
        this.BaseRenderFunctionItems(this.renderFunctionItems());
    }

    protected void BaseRenderMasks(Mask mask) {
        if (mask == null) return;
        List<TextComponent> collect = mask.getLore().stream().map(i -> ComponentUtils.text(i, this.player)).toList();
        for (Integer i : mask.getSlot()) {
            ItemStack itemStack = new ItemStack(Material.valueOf(mask.getMaterial().toUpperCase()));
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.displayName(ComponentUtils.text(mask.getName(), this.player));
            itemMeta.getPersistentDataContainer().set(this.renderType, PersistentDataType.STRING, FunctionType.MASKICON.name());
            itemMeta.lore(collect);
            itemStack.setItemMeta(itemMeta);
            this.inventory.setItem(i, itemStack);
        }
    }
    protected void BaseRenderFunctionItems(Map<String, FunctionItems> functionItemMap) {
        if (functionItemMap == null) return;
        functionItemMap.forEach((k, v) -> {
            ItemStack o = new ItemStack(Material.valueOf(v.getMaterial().toUpperCase()));
            ItemMeta mo = o.getItemMeta();
            mo.displayName(ComponentUtils.text(v.getName(), this.player));
            mo.lore(v.getLore().stream().map(this::apply).toList());
            mo.getPersistentDataContainer().set(this.renderType, PersistentDataType.STRING, v.getType().name());
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

    /**
     * 指定位置更新当前的GUI
     */
    public void updateGui() {
        if(this.inventory == null) return;
        this.inventory.clear();
        this.startRender();
    }

    private TextComponent apply(String q) {
        return ComponentUtils.text(q, this.player);
    }
}
