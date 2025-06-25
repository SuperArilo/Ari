package com.tty.command.function;

import com.tty.enumType.FilePath;
import com.tty.function.WarpManager;
import com.tty.gui.warp.WarpList;
import com.tty.lib.tool.FormatUtils;
import com.tty.lib.tool.Log;
import com.tty.tool.TextTool;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandWarp {

    private final CommandSender sender;

    public CommandWarp(CommandSender sender) {
        this.sender = sender;
    }

    public void warp() {
        new WarpList((Player) this.sender).open();
    }

    public void setWarp(String warpId) {
        if(FormatUtils.checkIdName(warpId)) {
            WarpManager.create(((Player) this.sender))
                    .createInstance(warpId).thenAccept(i -> {
                        if (i) {
                            this.sender.sendMessage(TextTool.setHEXColorText("function.warp.create-success", FilePath.Lang));
                        }
                    }).exceptionally(i -> {
                        Log.error("save warp error", i);
                        return null;
                    });
        } else {
            this.sender.sendMessage(TextTool.setHEXColorText("function.warp.id-error", FilePath.Lang));
        }
    }

    public void deleteWarp(String warpId) {
        if(FormatUtils.checkIdName(warpId)) {
            WarpManager
                    .create(((Player) this.sender))
                    .deleteInstance(warpId)
                    .thenAccept(status -> this.sender.sendMessage(TextTool.setHEXColorText("function.warp." + (status ? "delete-success":"not-found"), FilePath.Lang)));
        } else {
            this.sender.sendMessage(TextTool.setHEXColorText("function.warp.not-found", FilePath.Lang));
        }
    }
}
