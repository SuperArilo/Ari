package com.tty.command.function;

import com.tty.enumType.FilePath;
import com.tty.lib.tool.ComponentUtils;
import com.tty.tool.ConfigUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandItem {

    private final Player player;
    private final ItemStack itemStack;

    public CommandItem(Player player, ItemStack itemStack) {
        this.player = player;
        this.itemStack = itemStack;
    }

    public void changeName(String newName) {
        if (this.itemStack == null || this.itemStack.isEmpty()) {
            this.player.sendMessage(ComponentUtils.text(ConfigUtils.getValue("base.on-player.hand-no-item", FilePath.Lang)));
            return;
        }
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.displayName(ComponentUtils.text(newName));
        this.itemStack.setItemMeta(itemMeta);
    }

}
