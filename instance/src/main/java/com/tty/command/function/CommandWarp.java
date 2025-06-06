package com.tty.command.function;

import com.tty.Ari;
import com.tty.enumType.AriCommand;
import com.tty.enumType.FilePath;
import com.tty.function.WarpManager;
import com.tty.gui.warp.WarpList;
import com.tty.tool.TextTool;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CommandWarp {

    private final CommandSender sender;

    public CommandWarp(CommandSender sender) {
        this.sender = sender;
    }

    public void warp() {
        new WarpList((Player) this.sender).open();
    }

    public void setWarp(String warpId) {
        if(Ari.instance.formatUtils.checkIdName(warpId)) {
            WarpManager.create(((Player) this.sender).getUniqueId().toString()).createInstance(warpId);
        } else {
            this.sender.sendMessage(TextTool.setHEXColorText("function.warp.id-error", FilePath.Lang));
        }
    }

    public void deleteWarp(String warpId) {
        if(Ari.instance.formatUtils.checkIdName(warpId)) {
            WarpManager.create(((Player) this.sender).getUniqueId().toString()).deleteInstance(warpId);
        } else {
            this.sender.sendMessage(TextTool.setHEXColorText("function.warp.not-found", FilePath.Lang));
        }
    }

    public List<String> getWarpList() {
        Player player = (Player) this.sender;
        if(Ari.instance.permissionUtils.hasPermission(player, AriCommand.DELETEWARP.getPermission())) {
            CompletableFuture<List<String>> future = WarpManager.create(player.getUniqueId().toString()).asyncGetIdList();
            try {
                List<String> list = future.get();
                Collections.sort(list);
                return list;
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return List.of();
    }
}
