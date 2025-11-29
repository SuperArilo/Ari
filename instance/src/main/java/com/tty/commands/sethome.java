package com.tty.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.tty.Ari;
import com.tty.entity.sql.ServerHome;
import com.tty.enumType.FilePath;
import com.tty.function.HomeManager;
import com.tty.lib.Lib;
import com.tty.lib.Log;
import com.tty.lib.command.BaseCommand;
import com.tty.lib.command.SuperHandsomeCommand;
import com.tty.lib.dto.Page;
import com.tty.lib.tool.FormatUtils;
import com.tty.lib.tool.PermissionUtils;
import com.tty.lib.tool.PublicFunctionUtils;
import com.tty.tool.ConfigUtils;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class sethome extends BaseCommand<String> {

    public sethome() {
        super(false, StringArgumentType.string(), 2);
    }

    @Override
    public List<SuperHandsomeCommand> getSubCommands() {
        return List.of();
    }

    @Override
    public List<String> tabSuggestions(CommandSender sender, String[] args) {
        return List.of("<home id (string)>");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!this.isDisabledInGame(sender, Ari.C_INSTANCE.getObject(FilePath.HOME_CONFIG.name()))) return;

        String homeId = args[1];
        if(FormatUtils.checkIdName(homeId)) {
            Player player = (Player) sender;
            HomeManager homeManager = new HomeManager(player, true);
            homeManager.getList(Page.create(1, Integer.MAX_VALUE))
                .thenAccept(serverHomes -> {
                    if (serverHomes.size() + 1 > PermissionUtils.getMaxCountInPermission(player, "home")) {
                        sender.sendMessage(ConfigUtils.t("function.home.exceeds"));
                        return;
                    }
                    if (serverHomes.stream().anyMatch(c -> c.getHomeId().equals(homeId))) {
                        sender.sendMessage(ConfigUtils.t("function.home.exist", player));
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
                                .thenAccept(status -> sender.sendMessage(ConfigUtils.t(status ? "function.home.create-success":"base.save.on-error", player)))
                                .exceptionally(i -> {
                                    Log.error(i, "create home error");
                                    sender.sendMessage(ConfigUtils.t("base.on-error"));
                                    return null;
                                });
                    });
                }).exceptionally(i -> {
                    Log.error(i, "create home error");
                    sender.sendMessage(ConfigUtils.t("base.on-error"));
                    return null;
                });
        } else {
            sender.sendMessage(ConfigUtils.t("function.home.id-error"));
        }
    }

    @Override
    public String name() {
        return "sethome";
    }

    @Override
    public String permission() {
        return "ari.command.sethome";
    }
}
