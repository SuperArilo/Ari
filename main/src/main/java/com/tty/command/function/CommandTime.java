package com.tty.command.function;
import com.tty.Ari;
import com.tty.enumType.FilePath;
import com.tty.enumType.LangType;
import com.tty.enumType.TimePeriod;
import com.tty.function.TimeManager;
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
            String replace = ((String) Ari.instance.configManager.getValue("server.time.not-exist-period", FilePath.Lang, String.class)).replace(LangType.PERIOD.getType(), timePeriod);
            this.player.sendMessage(TextTool.setHEXColorText(replace));
            return;
        }
        TimeManager.build(this.player.getWorld()).timeSet(period.getStart());
        String value = Ari.instance.configManager.getValue("server.time.tips", FilePath.Lang, String.class);
        if (value == null) {
            this.player.sendMessage("no content " + timePeriod + "in lang");
            return;
        }
        value = value.replace(LangType.TIME.getType(), Ari.instance.configManager.getValue("server.time.name." + period.getDescription(), FilePath.Lang, String.class));
        this.player.sendMessage(TextTool.setHEXColorText(value));
    }
}
