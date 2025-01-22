package ari.superarilo.command.teleport;

import ari.superarilo.Ari;
import ari.superarilo.function.CommandCheck;
import ari.superarilo.function.impl.CommandCheckImpl;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.function.TeleportPrecondition;
import ari.superarilo.tool.TextTool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Tpa implements TabExecutor {
    
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        CommandCheckImpl check = CommandCheck.create();
        if (!check.isTheInstructionCorrect(command, AriCommand.TPA)) return false;
        if (check.allCheck(commandSender, AriCommand.TPA)) {
            //是否指令指令参数不对或者不全
            if (strings.length != 1 || strings[0].equals(commandSender.getName())) {
                commandSender.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("command.public.fail", FilePath.Lang, String.class)));
                return true;
            }
            //判断指令参数获取的玩家是否存在
            Player player = Ari.instance.getServer().getPlayerExact(strings[0]);
            if (player == null) {
                commandSender.sendMessage(TextTool.setHEXColorText(Ari.instance.configManager.getValue("teleport.unable-player", FilePath.Lang, String.class)));
                return true;
            }
            TeleportPrecondition.create().preCheckStatus((Player) commandSender, player, AriCommand.TPA);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!command.getName().equalsIgnoreCase(AriCommand.TPA.getShow())) return List.of("");
        if (commandSender instanceof Player && Ari.instance.permissionUtils.hasPermission(commandSender, AriCommand.TPA.getPermission()) && strings.length == 1) {
            List<String> players = new ArrayList<>();
            Ari.instance.getServer().getOnlinePlayers().forEach(e -> {
                if(commandSender.getName().equals(e.getName())) return;
                players.add(e.getName());
            });
            return players;
        }
        return List.of();
    }
}
