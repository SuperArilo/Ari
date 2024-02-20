package ari.superarilo.command.teleport;

import ari.superarilo.SuperArilo;
import ari.superarilo.command.tool.CommandCheck;
import ari.superarilo.entity.TeleportStatus;
import ari.superarilo.enumType.Commands;
import ari.superarilo.enumType.KeyType;
import ari.superarilo.tool.ConfigFiles;
import ari.superarilo.tool.TeleportThread;
import ari.superarilo.tool.TextTool;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Tpa implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        CommandCheck check = CommandCheck.create();

        //判断指令是否匹配
        if (!check.isTheInstructionCorrect(command, Commands.TPA)) return false;
        //是否是玩家
        if(!check.isPlayer(commandSender, Commands.TPA)) return true;
        //是否具有权限
        if (!check.commandSenderHavePermission(commandSender, Commands.TPA)) return true;
        //是否指令指令参数不对或者不全
        if (strings.length != 1 || strings[0].equals(commandSender.getName())) {
            commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpa.fail", "null")));
            return true;
        }
        //判断指令参数获取的玩家是否存在
        Player player = SuperArilo.instance.getServer().getPlayerExact(strings[0]);
        if (player == null) {
            commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpa.unable-player", "null")));
            return true;
        }
        //判断上一个请求是否存在
        if(SuperArilo.getTeleportStatusList().stream().filter(obj ->
                obj.getPlayUUID().equals(((Player) commandSender).getUniqueId())
                        && obj.getBePlayerUUID().equals(player.getUniqueId())
                        && obj.getType().equals(TeleportThread.Type.PLAYER)).toList().isEmpty()) {
            commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpa.send-message", "null")));

            //开始向传送发起者和接收者发送消息
            player.sendMessage(
                    TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpa.get-message", "null").replace(KeyType.TPASENDER.getType(), commandSender.getName()))
                            .appendNewline()
                            .append(TextTool.setClickEventText("&a[同意]", ClickEvent.Action.RUN_COMMAND, "/ari tpaaccept " + commandSender.getName()))
                            .append(TextTool.setHEXColorText("&f或者"))
                            .append(TextTool.setClickEventText("&c[拒绝]", ClickEvent.Action.RUN_COMMAND, "/ari tparefuse " + commandSender.getName())));
            Bukkit.getAsyncScheduler().runNow(SuperArilo.instance, t -> {
                TeleportStatus status = new TeleportStatus();
                status.setType(TeleportThread.Type.PLAYER);
                status.setCommandType(Commands.TPA);
                status.setPlayUUID(((Player) commandSender).getUniqueId());
                status.setBePlayerUUID(player.getUniqueId());
                SuperArilo.addTeleportStatus(status);
                //设置定时任务来移除该玩家已经发送的请求状态
                Bukkit.getAsyncScheduler().runDelayed(SuperArilo.instance, i -> SuperArilo.getTeleportStatusList().removeIf(obj -> obj.getPlayUUID().equals(((Player) commandSender).getUniqueId()) && obj.getType().equals(TeleportThread.Type.PLAYER)), 10L, TimeUnit.SECONDS);
            });
        } else {
            commandSender.sendMessage(TextTool.setHEXColorText(ConfigFiles.configs.get("lang").getString("command.tpa.again","null").replace(KeyType.TPABESENDER.getType(), player.getName())));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!command.getName().equalsIgnoreCase(Commands.TPA.getShow())) return List.of("");
        if (commandSender instanceof Player && commandSender.hasPermission(Commands.TPA.getPermission()) && strings.length == 1) {
            List<String> players = new ArrayList<>();
            SuperArilo.instance.getServer().getOnlinePlayers().forEach(e -> {
                if(commandSender.getName().equals(e.getName())) return;
                players.add(e.getName());
            });
            return players;
        }
        return List.of("");
    }
}
