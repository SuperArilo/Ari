package com.tty.command.function;

import com.tty.Ari;
import com.tty.entity.sql.WhitelistInstance;
import com.tty.enumType.commands.Zako;
import com.tty.function.PlayerManager;
import com.tty.function.WhitelistManager;
import com.tty.lib.enum_type.LangType;
import com.tty.lib.services.ConfigDataService;
import com.tty.lib.tool.FormatUtils;
import com.tty.lib.tool.Log;
import com.tty.lib.tool.TimeFormatUtils;
import com.tty.tool.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class CommandZako {

    private final CommandSender sender;

    public CommandZako(CommandSender sender) {
        this.sender = sender;
    }

    public void action(String action, String value) {
        Zako a;
        try {
            a = Zako.valueOf(action.toUpperCase());
        } catch (Exception e) {
            this.sender.sendMessage(ConfigUtils.t("base.on-edit.input-error"));
            return;
        }

        AtomicReference<UUID> uuid = new AtomicReference<>(null);
        try {
            uuid.set(UUID.fromString(value));
        } catch (Exception e) {
            Log.debug("zako is not a uuid: " + uuid.get());
        }
        if (uuid.get() == null) {
            uuid.set(Bukkit.getOfflinePlayer(value).getUniqueId());
        }

        switch (a) {
            case ADD -> {
                WhitelistInstance instance = new WhitelistInstance();
                instance.setPlayerUUID(uuid.toString());
                instance.setAddTime(System.currentTimeMillis());

                WhitelistManager manager = new WhitelistManager(true);
                manager.getInstance(uuid.toString()).thenAccept(i -> {
                    if (i != null) {
                        this.sender.sendMessage(ConfigUtils.t("function.zako.player-exist"));
                        return;
                    }
                    manager.createInstance(instance).thenAccept(status ->
                                    this.sender.sendMessage(ConfigUtils.t("function.zako.add-" + (status ? "success":"failure"))))
                            .exceptionally(n -> {
                                Log.error("add zako error", n);
                                this.sender.sendMessage(ConfigUtils.t("base.on-error"));
                                return null;
                            });
                }).exceptionally(i -> {
                    Log.error("query zako error", i);
                    this.sender.sendMessage(ConfigUtils.t("base.on-error"));
                    return null;
                });
            }
            case REMOVE -> {
                WhitelistManager manager = new WhitelistManager(true);
                manager.getInstance(uuid.get().toString()).thenCompose(instance -> {
                    if (instance == null) {
                        return CompletableFuture.completedFuture(false);
                    }
                    return manager.deleteInstance(instance);
                }).thenAccept(status -> {
                    Objects.requireNonNull(Bukkit.getPlayer(uuid.get())).kick(ConfigUtils.t("base.on-player.data-changed"));
                    this.sender.sendMessage(ConfigUtils.t("function.zako.remove-" + (status ? "success":"failure")));
                }).exceptionally(i -> {
                    Log.error("remove zako error", i);
                    this.sender.sendMessage(ConfigUtils.t("base.on-error"));
                    return null;
                });
            }
            case INFO -> {
                PlayerManager manager = new PlayerManager(true);
                manager.getInstance(uuid.get().toString())
                        .thenAccept(instance -> {
                            if(instance == null) {
                                this.sender.sendMessage(ConfigUtils.t("function.zako.not-exist"));
                                return;
                            }
                            Map<LangType, String> map = new HashMap<>();
                            ConfigDataService service = Ari.instance.dataService;

                            String PATTERN_DATETIME = "yyyy" + service.getValue("base.time-format.year") + "MM" + service.getValue("base.time-format.month") + "dd" + service.getValue("base.time-format.day") + "HH" + service.getValue("base.time-format.hour") +"mm" + service.getValue("base.time-format.minute") +"ss" + service.getValue("base.time-format.second");

                            map.put(LangType.PLAYERNAME, instance.getPlayerName());
                            map.put(LangType.FIRSTLOGINSERVERTIME, TimeFormatUtils.format(instance.getFirstLoginTime(), PATTERN_DATETIME));
                            map.put(LangType.LASTLOGINSERVERTIME, TimeFormatUtils.format(instance.getLastLoginOffTime(), PATTERN_DATETIME));
                            map.put(LangType.TOTALONSERVER, TimeFormatUtils.format(instance.getTotalOnlineTime()));
                            Player player = Bukkit.getPlayer(UUID.fromString(instance.getPlayerUUID()));
                            map.put(LangType.PLAYERWORLD, player == null ? "":player.getWorld().getName());
                            map.put(LangType.PLAYERLOCATION, player == null ? "": FormatUtils.XYZText(player.getX(), player.getY(), player.getZ()));

                            this.sender.sendMessage(ConfigUtils.ts("server.player.info", map));
                        }).exceptionally(i -> {
                           Log.error("error", i);
                           return null;
                        });
            }
        }
    }

}
