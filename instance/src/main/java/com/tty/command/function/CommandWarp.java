package com.tty.command.function;

import com.tty.Ari;
import com.tty.entity.sql.ServerWarp;
import com.tty.function.WarpManager;
import com.tty.gui.warp.WarpList;
import com.tty.lib.Lib;
import com.tty.lib.tool.FormatUtils;
import com.tty.lib.tool.Log;
import com.tty.lib.tool.PublicFunctionUtils;
import com.tty.tool.ConfigUtils;
import com.tty.tool.PermissionUtils;
import lombok.SneakyThrows;
import org.bukkit.block.BlockFace;
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

    @SneakyThrows
    public void setWarp(String warpId) {
        if(FormatUtils.checkIdName(warpId)) {
            Player player = (Player) this.sender;
            WarpManager warpManager = new WarpManager(true);
            warpManager.getCountByPlayer(player.getUniqueId().toString())
                .thenAccept(serverWarps -> {
                    if (serverWarps.size() + 1 > PermissionUtils.getMaxCountInPermission(player, "warp")) {
                        this.sender.sendMessage(ConfigUtils.t("function.warp.exceeds"));
                        return;
                    }

                    if (serverWarps.stream().anyMatch(i -> i.getWarpId().equals(warpId))) {
                        this.sender.sendMessage(ConfigUtils.t("function.warp.exist", player));
                        return;
                    }
                    Lib.Scheduler.runAtRegion(Ari.instance, player.getLocation(), task -> {
                        ServerWarp serverWarp = new ServerWarp();
                        serverWarp.setWarpId(warpId);
                        serverWarp.setWarpName(warpId);
                        serverWarp.setCreateBy(player.getUniqueId().toString());
                        serverWarp.setLocation(player.getLocation().toString());
                        serverWarp.setShowMaterial(PublicFunctionUtils.checkIsItem(player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType()).name());

                        warpManager.createInstance(serverWarp)
                                .thenAccept(i -> this.sender.sendMessage(ConfigUtils.t(i ? "function.warp.create-success":"base.save.on-error")))
                                .exceptionally(i -> {
                                    Log.error("create warp error", i);
                                    this.sender.sendMessage(ConfigUtils.t("base.save.on-error"));
                                    return null;
                                });
                    });
                }).exceptionally(i -> {
                    Log.error("create warp error", i);
                    this.sender.sendMessage(ConfigUtils.t("base.on-error"));
                    return null;
                });
        } else {
            this.sender.sendMessage(ConfigUtils.t("function.warp.id-error"));
        }
    }
}
