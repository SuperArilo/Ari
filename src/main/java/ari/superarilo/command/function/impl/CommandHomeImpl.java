package ari.superarilo.command.function.impl;

import ari.superarilo.Ari;
import ari.superarilo.command.function.CommandHome;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.function.HomeManager;
import ari.superarilo.gui.home.HomeList;
import ari.superarilo.tool.TextTool;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CommandHomeImpl implements CommandHome {

    private final CommandSender sender;

    public CommandHomeImpl(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public void setHome(String homeId) {
        if(Ari.instance.formatUtil.checkIdName(homeId)) {
            HomeManager.create((Player) this.sender).createInstance(homeId);
        } else {
            this.sender.sendMessage(TextTool.setHEXColorText("command.sethome.id-error", FilePath.Lang));
        }
    }

    @Override
    public void home() {
        new HomeList((Player) this.sender).open();
    }

    @Override
    public void deleteHome(String homeId) {
        if(Ari.instance.formatUtil.checkIdName(homeId)) {
            HomeManager.create((Player) this.sender).deleteInstance(homeId);
        } else {
            this.sender.sendMessage(TextTool.setHEXColorText("command.deletehome.id-error", FilePath.Lang));
        }
    }

    @Override
    public List<String> getHomeList() {
        Player player = (Player) this.sender;
        if(Ari.instance.permissionUtils.hasPermission(player, AriCommand.DELETEHOME.getPermission())) {
            CompletableFuture<List<String>> future = HomeManager.create(player).asyncGetIdList();
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
