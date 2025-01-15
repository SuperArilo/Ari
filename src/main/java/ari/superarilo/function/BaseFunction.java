package ari.superarilo.function;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface BaseFunction {
    /**
     * 检查材质是否是ITEM
     * @param material 被检查的材质
     * @return 返回一个正确的材质
     */
    Material checkIsItem(Material material);
    /**
     * 检查材质是否是 ItemStack
     * @param itemStack 被检查的item
     * @return 返回一个正确的item
     */
    ItemStack checkIsItemStack(ItemStack itemStack);
}
