package com.tty.command.function;

import com.tty.enumType.FilePath;
import com.tty.function.TimeManager;
import com.tty.lib.enum_type.LangType;
import com.tty.lib.enum_type.TimePeriod;
import com.tty.lib.tool.ComponentUtils;
import com.tty.tool.ConfigUtils;
import org.bukkit.entity.Player;

public class CommandTime {

    private final Player player;

    public CommandTime(Player sender) {
        this.player = sender;
    }

    public void control(String timePeriod) {
        TimePeriod period;
        try {
            period = TimePeriod.valueOf(timePeriod.toUpperCase());
        } catch (Exception e) {
            String replace = ConfigUtils.getValue("server.time.not-exist-period", FilePath.Lang).replace(LangType.PERIOD.getType(), timePeriod);
            this.player.sendMessage(ComponentUtils.text(replace));
            return;
        }
        TimeManager.build(this.player.getWorld()).timeSet(period.getStart());
        String value = ConfigUtils.getValue("server.time.tips", FilePath.Lang);
        if (value == null) {
            this.player.sendMessage("no content " + timePeriod + "in lang");
            return;
        }
        value = value.replace(LangType.TIME.getType(), ConfigUtils.getValue("server.time.name." + period.getDescription(), FilePath.Lang));
        this.player.sendMessage(ComponentUtils.text(value));
    }
}
