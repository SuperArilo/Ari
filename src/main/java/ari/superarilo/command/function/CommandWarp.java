package ari.superarilo.command.function;

import ari.superarilo.Ari;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.function.WarpManager;
import ari.superarilo.gui.warp.WarpList;
import ari.superarilo.tool.TextTool;
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
