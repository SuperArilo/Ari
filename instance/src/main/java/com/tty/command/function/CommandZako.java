package com.tty.command.function;

import com.tty.Ari;
import com.tty.entity.sql.WhitelistInstance;
import com.tty.function.WhitelistManager;
import com.tty.lib.Lib;
import com.tty.lib.enum_type.CommandAction;
import com.tty.lib.tool.Log;
import com.tty.tool.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class CommandZako {

    private final CommandSender sender;

    public CommandZako(CommandSender sender) {
        this.sender = sender;
    }

    public void action(String action, String value) {
        CommandAction a;
        try {
            a = CommandAction.valueOf(action.toUpperCase());
        } catch (Exception e) {
            this.sender.sendMessage(ConfigUtils.t("base.on-edit.input-error"));
            return;
        }

        AtomicReference<UUID> uuid = new AtomicReference<>(null);
        try {
            uuid.set(UUID.fromString(value));
        } catch (Exception e) {
            Log.debug("zako not a uuid");
        }
        if (uuid.get() == null) {
            uuid.set(Bukkit.getOfflinePlayer(value).getUniqueId());
        }

        WhitelistInstance instance = new WhitelistInstance();
        instance.setPlayerUUID(uuid.toString());
        instance.setAddTime(System.currentTimeMillis());

        WhitelistManager manager = new WhitelistManager(true);
        switch (a) {
            case ADD -> manager.getInstance(uuid.toString()).thenAccept(i -> {
               if (i != null) {
                   this.sender.sendMessage(ConfigUtils.t("function.zako.player-exist"));
                   return;
               }
                manager.createInstance(instance).thenAccept(status -> {
                    this.sender.sendMessage(ConfigUtils.t("function.zako.add-" + (status ? "success":"failure")));
                    if (status) {
                        Lib.Scheduler.run(Ari.instance, n -> {
                            Player player = Bukkit.getPlayer(uuid.get());
                            if (player != null && player.isOnline()) {
                                player.sendMessage(ConfigUtils.t("function.zako.send-player"));
                            }
                        });
                    }
                }).exceptionally(n -> {
                    Log.error("add zako error", n);
                    this.sender.sendMessage(ConfigUtils.t("base.on-error"));
                    return null;
                });
            }).exceptionally(i -> {
                Log.error("query zako error", i);
                this.sender.sendMessage(ConfigUtils.t("base.on-error"));
                return null;
            });
            case REMOVE -> manager.deleteInstance(instance).thenAccept(status ->
                this.sender.sendMessage(ConfigUtils.t("function.zako.remove-" + (status ? "success":"failure"))))
                .exceptionally(i -> {
                    Log.error("remove zako error", i);
                    this.sender.sendMessage(ConfigUtils.t("base.on-error"));
                    return null;
                });
        }
    }

}
