package com.tty.command.lists;

import com.tty.command.check.BaseCommandCheck;
import com.tty.command.function.CommandRtp;
import com.tty.enumType.AriCommand;
import com.tty.lib.tool.PublicFunctionUtils;
import com.tty.tool.ConfigUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class rtp extends BaseCommandCheck implements TabExecutor {

    public static final Map<Player, CommandRtp> RTP_LIST = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!this.isTheInstructionCorrect(command, AriCommand.RTP)) return false;
        if (this.quickCheck(commandSender, AriCommand.RTP)) {
            Player player = (Player) commandSender;
            if (strings.length == 0) {
                CommandRtp commandRtp = new CommandRtp(player);
                RTP_LIST.put(player, commandRtp);
                commandRtp.rtp();
            } else if (strings.length == 1 && strings[0].equals("cancel")){
                CommandRtp commandRtp = RTP_LIST.get(player);
                if (commandRtp == null) {
                    commandSender.sendMessage(ConfigUtils.t("function.rtp.no-rtp"));
                } else {
                    commandRtp.cancelRtp();
                }
            } else {
                commandSender.sendMessage(ConfigUtils.t("function.public.fail"));
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if(strings.length == 1) {
            return PublicFunctionUtils.filterByPrefix(new CommandRtp((Player) commandSender).getTabs(1), strings[0]);
        }
        return List.of();
    }
}
