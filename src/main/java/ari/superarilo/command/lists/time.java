package ari.superarilo.command.lists;

import ari.superarilo.Ari;
import ari.superarilo.command.function.CommandTime;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.enumType.LangType;
import ari.superarilo.enumType.TimePeriod;
import ari.superarilo.function.CommandCheck;
import ari.superarilo.tool.TextTool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class time implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        CommandCheck check = CommandCheck.create(commandSender, command,AriCommand.TIME);
        if (!check.isTheInstructionCorrect()) return false;
        if (check.allCheck() && strings.length == 1) {
            TimePeriod timePeriod;
            String period = strings[0].toUpperCase();
            try {
                timePeriod = TimePeriod.valueOf(period);
            } catch (Exception e) {
                String replace = ((String) Ari.instance.configManager.getValue("server.time.not-exist-period", FilePath.Lang, String.class)).replace(LangType.PERIOD.getType(), period);
                commandSender.sendMessage(TextTool.setHEXColorText(replace));
                return true;
            }
            new CommandTime(commandSender).control(timePeriod);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!command.getName().equalsIgnoreCase(AriCommand.TIME.getShow())) return List.of();
        List<String> list = new ArrayList<>();
        for (TimePeriod timePeriod : TimePeriod.values()) {
            list.add(timePeriod.getDescription());
        }
        return list;
    }
}
