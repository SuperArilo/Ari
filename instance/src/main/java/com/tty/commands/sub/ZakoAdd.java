package com.tty.commands.sub;

import com.mojang.brigadier.arguments.ArgumentType;
import com.tty.Ari;
import com.tty.entity.sql.WhitelistInstance;
import com.tty.enumType.FilePath;
import com.tty.function.WhitelistManager;
import com.tty.lib.command.BaseCommand;
import com.tty.lib.command.SuperHandsomeCommand;
import com.tty.lib.tool.Log;
import com.tty.tool.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class ZakoAdd extends BaseCommand<String> {

    public ZakoAdd(boolean allowConsole, ArgumentType<String> type) {
        super(allowConsole, type, 3);
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
        WhitelistInstance instance = new WhitelistInstance();
        instance.setPlayerUUID(uuid.toString());
        instance.setAddTime(System.currentTimeMillis());

        WhitelistManager manager = new WhitelistManager(true);
        manager.getInstance(uuid.toString()).thenAccept(i -> {
            if (i != null) {
                sender.sendMessage(ConfigUtils.t("function.zako.player-exist"));
                return;
            }
            manager.createInstance(instance).thenAccept(status ->
                            sender.sendMessage(ConfigUtils.t("function.zako.add-" + (status ? "success":"failure"))))
                    .exceptionally(n -> {
                        Log.error("add zako error", n);
                        sender.sendMessage(ConfigUtils.t("base.on-error"));
                        return null;
                    });
        }).exceptionally(i -> {
            Log.error("query zako error", i);
            sender.sendMessage(ConfigUtils.t("base.on-error"));
            return null;
        });
    }

    @Override
    public String name() {
        return "add";
    }

    @Override
    public String permission() {
        return "ari.command.zako.add";
    }
}
