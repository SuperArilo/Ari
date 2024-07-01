package ari.superarilo.command.teleport;

import ari.superarilo.Ari;
import ari.superarilo.command.tool.CommandCheck;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.function.teleport.TeleportPrecondition;
import ari.superarilo.tool.ConfigFiles;
import ari.superarilo.tool.TextTool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TpaHere implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!CommandCheck.create().allCheck(commandSender, command, AriCommand.TPAHERE)) return false;
        if (!command.getName().equalsIgnoreCase(AriCommand.TPAHERE.getShow())) return false;

        //指令不全
        if (strings.length != 1 || strings[0].equals(commandSender.getName())) {
            commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpahere.fail", "null")));
            return true;
        }
        Player player = Ari.instance.getServer().getPlayerExact(strings[0]);
        if (player == null) {
            commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpahere.unable-player", "null")));
            return true;
        }
        TeleportPrecondition.create().preCheckStatus((Player) commandSender, player, AriCommand.TPAHERE);

        return true;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!command.getName().equalsIgnoreCase(AriCommand.TPA.getShow())) return List.of("");
        if (commandSender instanceof Player && commandSender.hasPermission(AriCommand.TPA.getPermission()) && strings.length == 1) {
            List<String> players = new ArrayList<>();
            Ari.instance.getServer().getOnlinePlayers().forEach(e -> {
                if(commandSender.getName().equals(e.getName())) return;
                players.add(e.getName());
            });
            return players;
        }
        return List.of("");
    }


}
