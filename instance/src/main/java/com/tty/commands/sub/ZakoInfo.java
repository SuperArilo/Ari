package com.tty.commands.sub;

import com.mojang.brigadier.arguments.ArgumentType;
import com.tty.Ari;
import com.tty.enumType.FilePath;
import com.tty.function.PlayerManager;
import com.tty.lib.command.BaseCommand;
import com.tty.lib.command.SuperHandsomeCommand;
import com.tty.lib.enum_type.LangType;
import com.tty.lib.services.ConfigDataService;
import com.tty.lib.tool.FormatUtils;
import com.tty.lib.tool.Log;
import com.tty.lib.tool.TimeFormatUtils;
import com.tty.tool.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class ZakoInfo extends BaseCommand<String> {

    public ZakoInfo(boolean allowConsole, ArgumentType<String> type) {
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

        PlayerManager manager = new PlayerManager(true);
        manager.getInstance(uuid.get().toString())
            .thenAccept(instance -> {
                if(instance == null) {
                    sender.sendMessage(ConfigUtils.t("function.zako.not-exist"));
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
                map.put(LangType.PLAYERWORLD, player == null ? Ari.C_INSTANCE.getValue("base.no-record", FilePath.Lang):player.getWorld().getName());
                map.put(LangType.PLAYERLOCATION, player == null ? Ari.C_INSTANCE.getValue("base.no-record", FilePath.Lang): FormatUtils.XYZText(player.getX(), player.getY(), player.getZ()));

                sender.sendMessage(ConfigUtils.ts("server.player.info", map));
            }).exceptionally(i -> {
                Log.error("error", i);
                return null;
            });
    }

    @Override
    public String name() {
        return "info";
    }

    @Override
    public String permission() {
        return "ari.command.zako.info";
    }
}
