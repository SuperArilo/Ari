package ari.superarilo.command.function.impl;

import ari.superarilo.Ari;
import ari.superarilo.command.function.CommandWarp;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.function.HomeManager;
import ari.superarilo.function.WarpManager;
import ari.superarilo.gui.warp.WarpList;
import ari.superarilo.tool.TextTool;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CommandWarpImpl implements CommandWarp {

    private final CommandSender sender;

    public CommandWarpImpl(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public void warp() {
        new WarpList((Player) this.sender).open();
    }

    @Override
    public void setWarp(String warpId) {
        if(Ari.instance.formatUtil.checkIdName(warpId)) {
            WarpManager.create((Player) this.sender).createInstance(warpId);
        } else {
            this.sender.sendMessage(TextTool.setHEXColorText("command.setwarp.id-error", FilePath.Lang));
        }
    }

    @Override
    public void deleteWarp(String warpId) {
        if(Ari.instance.formatUtil.checkIdName(warpId)) {
            WarpManager.create((Player) this.sender).deleteInstance(warpId);
        } else {
            this.sender.sendMessage(TextTool.setHEXColorText("command.deletewarp.id-error", FilePath.Lang));
        }
    }

    @Override
    public List<String> getWarpList() {
        Player player = (Player) this.sender;
        if(Ari.instance.permissionUtils.hasPermission(player, AriCommand.DELETEWARP.getPermission())) {
            CompletableFuture<List<String>> future = WarpManager.create(player).asyncGetIdList();
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
