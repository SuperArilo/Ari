package com.tty.command.function;

import com.tty.enumType.FilePath;
import com.tty.function.HomeManager;
import com.tty.gui.home.HomeList;
import com.tty.lib.tool.FormatUtils;
import com.tty.tool.TextTool;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHome {

    private final CommandSender sender;

    public CommandHome(CommandSender sender) {
        this.sender = sender;
    }

    public void setHome(String homeId) {
        if(FormatUtils.checkIdName(homeId)) {
            HomeManager.create(((Player) this.sender)).createInstance(homeId);
        } else {
            this.sender.sendMessage(TextTool.setHEXColorText("function.home.id-error", FilePath.Lang));
        }
    }

    public void home() {
        new HomeList((Player) this.sender).open();
    }

    public void deleteHome(String homeId) {
        if(FormatUtils.checkIdName(homeId)) {
            HomeManager
                    .create(((Player) this.sender)).
                    deleteInstance(homeId)
                    .thenAccept(status -> this.sender.sendMessage(TextTool.setHEXColorText("function.home." + (status ? "delete-success":"not-found"), FilePath.Lang)));
        } else {
            this.sender.sendMessage(TextTool.setHEXColorText("function.home.not-found", FilePath.Lang));
        }
    }

}
