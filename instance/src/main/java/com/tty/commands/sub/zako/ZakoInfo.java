package com.tty.commands.sub.zako;

import com.mojang.brigadier.arguments.ArgumentType;
import com.tty.Ari;
import com.tty.enumType.FilePath;
import com.tty.function.PlayerManager;
import com.tty.lib.Log;
import com.tty.lib.command.SuperHandsomeCommand;
import com.tty.lib.enum_type.LangType;
import com.tty.lib.services.ConfigDataService;
import com.tty.lib.tool.FormatUtils;
import com.tty.lib.tool.TimeFormatUtils;
import com.tty.tool.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ZakoInfo extends ZakoBase<String> {

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
        UUID uuid = this.parseUUID(value);
        if (uuid == null) return;

        PlayerManager manager = new PlayerManager(true);
        manager.getInstance(uuid.toString())
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
                map.put(LangType.PLAYERWORLD, player == null ? Ari.C_INSTANCE.getValue("base.no-record", FilePath.LANG):player.getWorld().getName());
                map.put(LangType.PLAYERLOCATION, player == null ? Ari.C_INSTANCE.getValue("base.no-record", FilePath.LANG): FormatUtils.XYZText(player.getX(), player.getY(), player.getZ()));

                sender.sendMessage(ConfigUtils.ts("server.player.info", map));
            }).exceptionally(i -> {
                Log.error(i, "error");
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
