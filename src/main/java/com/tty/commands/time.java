package com.tty.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.tty.Ari;
import com.tty.enumType.FilePath;
import com.tty.function.TimeManager;
import com.tty.lib.command.BaseCommand;
import com.tty.lib.command.SuperHandsomeCommand;
import com.tty.lib.enum_type.LangType;
import com.tty.lib.enum_type.TimePeriod;
import com.tty.lib.tool.ComponentUtils;
import com.tty.tool.ConfigUtils;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class time extends BaseCommand<String> {

    public time() {
        super(false, StringArgumentType.word(), 2);
    }

    @Override
    public List<SuperHandsomeCommand> getSubCommands() {
        return List.of();
    }

    @Override
    public List<String> tabSuggestions(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        for (TimePeriod value : TimePeriod.values()) {
            list.add(value.getDescription());
        }
        return list;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String timePeriod = args[1];
        TimePeriod period;
        try {
            period = TimePeriod.valueOf(timePeriod.toUpperCase());
        } catch (Exception e) {
            player.sendMessage(ConfigUtils.t("server.time.not-exist-period", LangType.PERIOD.getType(), timePeriod));
            return;
        }
        World world = player.getWorld();
        if (!world.isBedWorks()) {
            player.sendMessage(ConfigUtils.t("server.time.not-allowed-world"));
            return;
        }
        TimeManager.build(world).timeSet(period.getStart());
        String value = Ari.C_INSTANCE.getValue("server.time.tips", FilePath.LANG);
        if (value == null) {
            player.sendMessage("no content " + timePeriod + "in lang");
            return;
        }
        value = value.replace(LangType.TIME.getType(), Ari.C_INSTANCE.getValue("server.time.name." + period.getDescription(), FilePath.LANG));
        player.sendMessage(ComponentUtils.text(value));
    }

    @Override
    public String name() {
        return "time";
    }

    @Override
    public String permission() {
        return "ari.command.time";
    }
}
