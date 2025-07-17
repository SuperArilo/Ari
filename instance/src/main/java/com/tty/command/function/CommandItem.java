package com.tty.command.function;

import com.tty.enumType.FilePath;
import com.tty.lib.enum_type.CommandAction;
import com.tty.lib.tool.ComponentUtils;
import com.tty.tool.ConfigUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CommandItem {

    private final Player player;
    private final ItemStack itemStack;

    public CommandItem(Player player, ItemStack itemStack) {
        this.player = player;
        this.itemStack = itemStack;
    }

    public void changeName(String newName) {
        if (!this.beforeCheck()) return;
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.displayName(ComponentUtils.text(newName));
        this.itemStack.setItemMeta(itemMeta);
    }

    public void changeLore(String action, String content) {
        CommandAction a;
        try {
            a = CommandAction.valueOf(action.toUpperCase());
        } catch (Exception e) {
            this.player.sendMessage(ComponentUtils.text(ConfigUtils.getValue("base.on-edit.input-error", FilePath.Lang)));
            return;
        }
        if (!this.beforeCheck()) return;
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        List<Component> lore = itemMeta.lore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        switch (a) {
            case ADD -> lore.add(ComponentUtils.text(content));
            case REMOVE -> {
                try {
                    int index = Integer.parseInt(content) - 1;
                    if (index < 0) {
                        return;
                    }
                    lore.remove(index);
                } catch (Exception e) {
                    this.player.sendMessage(ComponentUtils.text(ConfigUtils.getValue("base.on-edit.input-error", FilePath.Lang)));
                    return;
                }
            }
        }
        itemMeta.lore(lore);
        this.itemStack.setItemMeta(itemMeta);
    }

    private boolean beforeCheck() {
        if (this.itemStack == null || this.itemStack.isEmpty()) {
            this.player.sendMessage(ComponentUtils.text(ConfigUtils.getValue("base.on-player.hand-no-item", FilePath.Lang)));
            return false;
        }
        return true;
    }

}
