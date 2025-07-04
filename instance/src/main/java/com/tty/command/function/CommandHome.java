package com.tty.command.function;

import com.tty.Ari;
import com.tty.entity.sql.ServerHome;
import com.tty.enumType.FilePath;
import com.tty.function.HomeManager;
import com.tty.gui.home.HomeList;
import com.tty.lib.Lib;
import com.tty.lib.dto.Page;
import com.tty.lib.tool.ComponentUtils;
import com.tty.lib.tool.FormatUtils;
import com.tty.lib.tool.Log;
import com.tty.lib.tool.PublicFunctionUtils;
import com.tty.tool.ConfigUtils;
import com.tty.tool.PermissionUtils;
import lombok.SneakyThrows;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandHome {

    private final CommandSender sender;

    public CommandHome(CommandSender sender) {
        this.sender = sender;
    }

    @SneakyThrows
    public void setHome(String homeId) {
        if(FormatUtils.checkIdName(homeId)) {
            Player player = (Player) this.sender;
            HomeManager homeManager = new HomeManager(player, true);
            homeManager.getList(Page.create(1, Integer.MAX_VALUE))
                .thenAccept(serverHomes -> {
                    if (serverHomes.size() + 1 > PermissionUtils.getMaxCountInPermission(player, "home")) {
                        this.sender.sendMessage(ComponentUtils.text(ConfigUtils.getValue("function.home.exceeds", FilePath.Lang)));
                        return;
                    }
                    if (serverHomes.stream().anyMatch(c -> c.getHomeId().equals(homeId))) {
                        this.sender.sendMessage(ComponentUtils.text(ConfigUtils.getValue("function.home.exist", FilePath.Lang), player));
                        return;
                    }


                    Lib.Scheduler.runAtRegion(Ari.instance, player.getLocation(), task -> {
                        ServerHome serverHome = new ServerHome();
                        serverHome.setHomeId(homeId);
                        serverHome.setHomeName(homeId);
                        serverHome.setPlayerUUID(player.getUniqueId().toString());
                        serverHome.setLocation(player.getLocation().toString());
                        serverHome.setShowMaterial(PublicFunctionUtils.checkIsItem(player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType()).name());

                        homeManager.createInstance(serverHome)
                                .thenAccept(status -> this.sender.sendMessage(ComponentUtils.text(ConfigUtils.getValue(status ? "function.home.create-success":"base.save.on-error", FilePath.Lang), player)))
                                .exceptionally(i -> {
                                    Log.error("create home error", i);
                                    this.sender.sendMessage(ComponentUtils.text(ConfigUtils.getValue("base.on-error", FilePath.Lang)));
                                    return null;
                                });
                    });
                }).exceptionally(i -> {
                    Log.error("create home error", i);
                    this.sender.sendMessage(ComponentUtils.text(ConfigUtils.getValue("base.on-error", FilePath.Lang)));
                    return null;
                });
        } else {
            this.sender.sendMessage(ComponentUtils.text(ConfigUtils.getValue("function.home.id-error", FilePath.Lang)));
        }
    }

    public void home() {
        new HomeList((Player) this.sender).open();
    }
}
