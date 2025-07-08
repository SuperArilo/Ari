package com.tty.gui;

import com.tty.Ari;
import com.tty.dto.CustomInventoryHolder;
import com.tty.entity.menu.BaseMenu;
import com.tty.entity.menu.FunctionItems;
import com.tty.entity.menu.Mask;
import com.tty.lib.enum_type.FunctionType;
import com.tty.lib.tool.ComponentUtils;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public abstract class BaseInventory {

    public final BaseMenu baseInstance;
    protected final Player player;
    private Inventory inventory;

    private final NamespacedKey renderType = new NamespacedKey(Ari.instance, "type");

    public BaseInventory(BaseMenu instance, Player player) {
        this.baseInstance = instance;
        this.player = player;
    }

    public void open() {
        this.inventory = Bukkit.createInventory(this.createHolder(), this.baseInstance.getRow() * 9, ComponentUtils.text(this.baseInstance.getTitle(), player));
        this.player.openInventory(this.inventory);
        this.renderMasks();
        this.renderFunctionItems();
    }

    protected abstract Mask getMasks();

    protected abstract Map<String, FunctionItems> getFunctionItems();

    private void renderMasks() {
        Mask mask = this.getMasks();
        if (mask == null) {
            mask = this.baseInstance.getMask();
        }
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

    private void renderFunctionItems() {
        Map<String, FunctionItems> functionItems = this.getFunctionItems();
        if (functionItems == null || functionItems.isEmpty()) {
            functionItems = this.baseInstance.getFunctionItems();
        }
        functionItems.forEach((k, v) -> {
            ItemStack o = new ItemStack(Material.valueOf(v.getMaterial().toUpperCase()));
            ItemMeta mo = o.getItemMeta();
            mo.displayName(ComponentUtils.text(v.getName(), this.player));
            mo.lore(v.getLore().stream().map(i -> ComponentUtils.text(i, this.player)).toList());
            mo.getPersistentDataContainer().set(this.renderType, PersistentDataType.STRING, v.getType().name());
            o.setItemMeta(mo);
            for (Integer integer : v.getSlot()) {
                this.inventory.setItem(integer, o);
            }
        });
    }

    protected abstract CustomInventoryHolder createHolder();

    protected void clearItem(int index) {
        this.inventory.clear(index);
    }

    protected void setItem(int index, @NotNull ItemStack itemStack) {
        this.inventory.setItem(index, itemStack);
    }
}
