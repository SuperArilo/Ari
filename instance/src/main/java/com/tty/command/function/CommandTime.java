package com.tty.command.function;
import com.tty.enumType.FilePath;
import com.tty.lib.enum_type.LangType;
import com.tty.lib.enum_type.TimePeriod;
import com.tty.function.TimeManager;
import com.tty.tool.ConfigObjectUtils;
import com.tty.tool.TextTool;
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
            String replace = ((String) ConfigObjectUtils.getValue("server.time.not-exist-period", FilePath.Lang.getName(), String.class)).replace(LangType.PERIOD.getType(), timePeriod);
            this.player.sendMessage(TextTool.setHEXColorText(replace));
            return;
        }
        TimeManager.build(this.player.getWorld()).timeSet(period.getStart());
        String value = ConfigObjectUtils.getValue("server.time.tips", FilePath.Lang.getName(), String.class);
        if (value == null) {
            this.player.sendMessage("no content " + timePeriod + "in lang");
            return;
        }
        value = value.replace(LangType.TIME.getType(), ConfigObjectUtils.getValue("server.time.name." + period.getDescription(), FilePath.Lang.getName(), String.class));
        this.player.sendMessage(TextTool.setHEXColorText(value));
    }
}
