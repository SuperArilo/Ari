package com.tty.commands.sub;

import com.mojang.brigadier.arguments.ArgumentType;
import com.tty.Ari;
import com.tty.enumType.FilePath;
import com.tty.function.WhitelistManager;
import com.tty.lib.Lib;
import com.tty.lib.command.BaseCommand;
import com.tty.lib.command.SuperHandsomeCommand;
import com.tty.lib.tool.Log;
import com.tty.tool.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class ZakoRemove extends BaseCommand<String> {

    public ZakoRemove(boolean allowConsole, ArgumentType<String> type) {
        super(allowConsole, type);
    }

    @Override
    public List<SuperHandsomeCommand> getSubCommands() {
        return List.of();
    }

    @Override
    public List<String> tabSuggestions(CommandSender sender, String[] args) {
        return List.of("<name or uuid (string)>");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String value = args[2];
        AtomicReference<UUID> uuid = new AtomicReference<>(null);
        try {
            uuid.set(UUID.fromString(value));
        } catch (Exception e) {
            Log.debug("zako is not a uuid: " + uuid.get());
        }
        if (uuid.get() == null) {
            try {
                uuid.set(Bukkit.getOfflinePlayer(value).getUniqueId());
            } catch (Exception e) {
                Log.error(Ari.C_INSTANCE.getValue("function.zako.not-exist", FilePath.Lang));
                return;
            }
        }
        WhitelistManager manager = new WhitelistManager(true);
        manager.getInstance(uuid.get().toString()).thenCompose(instance -> {
            if (instance == null) {
                return CompletableFuture.completedFuture(false);
            }
            return manager.deleteInstance(instance);
        }).thenAccept(status -> {
            Player player = Bukkit.getPlayer(uuid.get());
            if(player != null) {
                Lib.Scheduler.runAtEntity(Ari.instance, player, (i)->player.kick(ConfigUtils.t("base.on-player.data-changed")), () -> player.sendMessage(ConfigUtils.t("on-error")));
            }
            sender.sendMessage(ConfigUtils.t("function.zako.remove-" + (status ? "success":"failure")));
        }).exceptionally(i -> {
            Log.error("remove zako error", i);
            sender.sendMessage(ConfigUtils.t("base.on-error"));
            return null;
        });
    }

    @Override
    public String name() {
        return "remove";
    }

    @Override
    public String permission() {
        return "ari.command.zako.remove";
    }
}
