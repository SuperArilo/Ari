package ari.superarilo.command.function;
import ari.superarilo.Ari;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.enumType.LangType;
import ari.superarilo.enumType.TimePeriod;
import ari.superarilo.function.TimeManager;
import ari.superarilo.tool.TextTool;
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
        value = value.replace(LangType.TIME.getType(), Ari.instance.configManager.getValue("server.time.name." + period.getDescription(), FilePath.Lang, String.class));
        this.player.sendMessage(TextTool.setHEXColorText(value));
    }
}
