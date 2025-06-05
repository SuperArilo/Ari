package com.tty.command.function;

import com.tty.Ari;
import com.tty.enumType.AriCommand;
import com.tty.enumType.FilePath;
import com.tty.function.HomeManager;
import com.tty.gui.home.HomeList;
import com.tty.tool.TextTool;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CommandHome {

    private final CommandSender sender;

    public CommandHome(CommandSender sender) {
        this.sender = sender;
    }

    public void setHome(String homeId) {
        if(Ari.instance.formatUtils.checkIdName(homeId)) {
            HomeManager.create(((Player) this.sender).getUniqueId().toString()).createInstance(homeId);
        } else {
            this.sender.sendMessage(TextTool.setHEXColorText("function.home.id-error", FilePath.Lang));
        }
    }

    public void home() {
        new HomeList((Player) this.sender).open();
    }

    public void deleteHome(String homeId) {
        if(Ari.instance.formatUtils.checkIdName(homeId)) {
            HomeManager.create(((Player) this.sender).getUniqueId().toString()).deleteInstance(homeId);
        } else {
            this.sender.sendMessage(TextTool.setHEXColorText("function.home.not-found", FilePath.Lang));
        }
    }

    public List<String> getHomeList() {
        Player player = (Player) this.sender;
        if(Ari.instance.permissionUtils.hasPermission(player, AriCommand.DELETEHOME.getPermission())) {
            CompletableFuture<List<String>> future = HomeManager.create(player.getUniqueId().toString()).asyncGetIdList();
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
