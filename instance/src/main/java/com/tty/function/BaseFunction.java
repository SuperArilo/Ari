package com.tty.function;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BaseFunction {

    /**
     * 检查材质是否是ITEM
     * @param material 被检查的材质
     * @return 返回一个正确的材质
     */
    public Material checkIsItem(Material material) {
        if(!material.isItem() || !material.isSolid()) {
            return Material.DIRT;
        }
        return material;
    }
    /**
     * 检查材质是否是 ItemStack
     * @param itemStack 被检查的item
     * @return 返回一个正确的item
     */
    public ItemStack checkIsItemStack(ItemStack itemStack) {
        if (itemStack == null) {
            itemStack = new ItemStack(Material.DIRT);
            itemStack.setItemMeta(itemStack.getItemMeta());
        }
        return itemStack;
    }
}
