package com.tty.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.tty.Ari;
import com.tty.entity.sql.ServerWarp;
import com.tty.enumType.FilePath;
import com.tty.function.WarpManager;
import com.tty.lib.Lib;
import com.tty.lib.Log;
import com.tty.lib.command.BaseCommand;
import com.tty.lib.command.SuperHandsomeCommand;
import com.tty.lib.tool.FormatUtils;
import com.tty.lib.tool.PermissionUtils;
import com.tty.lib.tool.PublicFunctionUtils;
import com.tty.tool.ConfigUtils;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class setwarp extends BaseCommand<String> {

    public setwarp() {
        super(false, StringArgumentType.string(), 2);
    }

    @Override
    public List<SuperHandsomeCommand> getSubCommands() {
        return List.of();
    }

    @Override
    public List<String> tabSuggestions(CommandSender sender, String[] args) {
        return List.of("<warp id (string)>");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!this.isDisabledInGame(sender, Ari.C_INSTANCE.getObject(FilePath.WARP_CONFIG.name()))) return;

        String warpId = args[1];
        Player player = (Player) sender;

        if(FormatUtils.checkIdName(warpId)) {
            WarpManager warpManager = new WarpManager(true);
            warpManager.getCountByPlayer(player.getUniqueId().toString())
                .thenAccept(serverWarps -> {
                    if (serverWarps.size() + 1 > PermissionUtils.getMaxCountInPermission(player, "warp")) {
                        player.sendMessage(ConfigUtils.t("function.warp.exceeds"));
                        return;
                    }

                    if (serverWarps.stream().anyMatch(i -> i.getWarpId().equals(warpId))) {
                        player.sendMessage(ConfigUtils.t("function.warp.exist", player));
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
                                .thenAccept(i -> player.sendMessage(ConfigUtils.t(i ? "function.warp.create-success":"base.save.on-error")))
                                .exceptionally(i -> {
                                    Log.error(i, "create warp error");
                                    player.sendMessage(ConfigUtils.t("base.save.on-error"));
                                    return null;
                                });
                    });
                }).exceptionally(i -> {
                    Log.error(i, "create warp error");
                    player.sendMessage(ConfigUtils.t("base.on-error"));
                    return null;
                });
        } else {
            player.sendMessage(ConfigUtils.t("function.warp.id-error"));
        }
    }

    @Override
    public String name() {
        return "setwarp";
    }

    @Override
    public String permission() {
        return "ari.command.setwarp";
    }
}
