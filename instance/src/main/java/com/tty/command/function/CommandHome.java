package com.tty.command.function;

import com.tty.enumType.AriCommand;
import com.tty.enumType.FilePath;
import com.tty.function.HomeManager;
import com.tty.gui.home.HomeList;
import com.tty.lib.tool.FormatUtils;
import com.tty.tool.Log;
import com.tty.lib.tool.PermissionUtils;
import com.tty.tool.TextTool;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CommandHome {

    private final CommandSender sender;

    public CommandHome(CommandSender sender) {
        this.sender = sender;
    }

    public void setHome(String homeId) {
        if(FormatUtils.checkIdName(homeId)) {
            HomeManager.create(((Player) this.sender).getUniqueId().toString()).createInstance(homeId);
        } else {
            this.sender.sendMessage(TextTool.setHEXColorText("function.home.id-error", FilePath.Lang));
        }
    }

    public void home() {
        new HomeList((Player) this.sender).open();
    }

    public void deleteHome(String homeId) {
        if(FormatUtils.checkIdName(homeId)) {
            HomeManager.create(((Player) this.sender).getUniqueId().toString()).deleteInstance(homeId);
        } else {
            this.sender.sendMessage(TextTool.setHEXColorText("function.home.not-found", FilePath.Lang));
        }
    }

    public List<String> getHomeList() {
        Player player = (Player) this.sender;
        if(PermissionUtils.hasPermission(player, AriCommand.DELETEHOME.getPermission())) {
            CompletableFuture<List<String>> future = HomeManager.create(player.getUniqueId().toString()).asyncGetIdList();
            try {
                List<String> strings = future.get(3, TimeUnit.SECONDS);
                Collections.sort(strings);
                return strings;
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                Log.error("query error", e);
            }
        }
        return List.of();
    }

}
