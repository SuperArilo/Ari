package ari.superarilo.command.function;
import ari.superarilo.Ari;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.enumType.LangType;
import ari.superarilo.enumType.TimePeriod;
import ari.superarilo.function.TimeManager;
import ari.superarilo.tool.TextTool;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTime {

    private final Player player;

    public CommandTime(CommandSender sender) {
        this.player = (Player) sender;
    }

    public void control(TimePeriod timePeriod) {
        TimeManager.build(this.player.getWorld()).timeSet(timePeriod.getStart());
        String value = Ari.instance.configManager.getValue("server.time.tips", FilePath.Lang, String.class);
        value = value.replace(LangType.TIME.getType(), Ari.instance.configManager.getValue("server.time.name." + timePeriod.getDescription(), FilePath.Lang, String.class));
        this.player.sendMessage(TextTool.setHEXColorText(value));
    }
}
