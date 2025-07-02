package com.tty.command.function;

import com.tty.Ari;
import com.tty.entity.sql.ServerWarp;
import com.tty.enumType.FilePath;
import com.tty.function.WarpManager;
import com.tty.gui.warp.WarpList;
import com.tty.lib.Lib;
import com.tty.lib.tool.FormatUtils;
import com.tty.lib.tool.Log;
import com.tty.lib.tool.PublicFunctionUtils;
import com.tty.tool.PermissionUtils;
import com.tty.tool.TextTool;
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
            WarpManager warpManager = new WarpManager();
            warpManager.asyncGetCountByPlayer(player.getUniqueId().toString())
                .thenAccept(serverWarps -> {
                    if (serverWarps.size() + 1 > PermissionUtils.getMaxCountInPermission(player, "warp")) {
                        this.sender.sendMessage(TextTool.setHEXColorText("function.warp.exceeds", FilePath.Lang));
                        return;
                    }

                    if (serverWarps.stream().anyMatch(i -> i.getWarpId().equals(warpId))) {
                        this.sender.sendMessage(TextTool.setHEXColorText("function.warp.exist", FilePath.Lang, player));
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
                                .thenAccept(i -> this.sender.sendMessage(TextTool.setHEXColorText(i ? "function.warp.create-success":"base.save.on-error", FilePath.Lang)))
                                .exceptionally(i -> {
                                    this.sender.sendMessage(TextTool.setHEXColorText("base.save.on-error", FilePath.Lang));
                                    return null;
                                });
                    });
                }).exceptionally(i -> {
                    Log.error("create warp error", i);
                    this.sender.sendMessage(TextTool.setHEXColorText("base.on-error", FilePath.Lang));
                    return null;
                });
        } else {
            this.sender.sendMessage(TextTool.setHEXColorText("function.warp.id-error", FilePath.Lang));
        }
    }
}
