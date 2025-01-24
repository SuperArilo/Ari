package ari.superarilo.command.lists;

import ari.superarilo.Ari;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.function.CommandCheck;
import ari.superarilo.function.HomeManager;
import ari.superarilo.tool.TextTool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class deletehome implements TabExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        CommandCheck check = CommandCheck.create(commandSender, command, AriCommand.DELETEHOME);
        if (!check.isTheInstructionCorrect()) return false;
        if (check.allCheck()) {
            if(strings.length != 1 || !Ari.instance.formatUtil.checkIdName(strings[0])) {
                commandSender.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("command.delete.id-error", FilePath.Lang, String.class)));
                return true;
            }
            HomeManager.create((Player) commandSender).deleteHome(strings[0]);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(strings.length == 1) {
            Player player = (Player) commandSender;
            AriCommand deletehome = AriCommand.DELETEHOME;
            if(!command.getName().equalsIgnoreCase(deletehome.getShow())) return List.of();
            if(Ari.instance.permissionUtils.hasPermission(player, deletehome.getPermission())) {
                List<String> list = HomeManager.create(player).asyncGetHomeIdList();
                Collections.sort(list);
                return list;
            }
        }
        return List.of();
    }
}
