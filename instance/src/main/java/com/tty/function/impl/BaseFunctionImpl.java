package com.tty.function.impl;

import com.tty.function.BaseFunction;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BaseFunctionImpl implements BaseFunction {
    @Override
    public Material checkIsItem(Material material) {
        if(!material.isItem() || material.equals(Material.WATER) || material.equals(Material.LAVA) || material.equals(Material.AIR)) {
            return Material.DIRT;
        }
        return material;
    }

    @Override
    public ItemStack checkIsItemStack(ItemStack itemStack) {
        if (itemStack == null) {
            itemStack = new ItemStack(Material.DIRT);
            itemStack.setItemMeta(itemStack.getItemMeta());
        }
        return itemStack;
    }
}
