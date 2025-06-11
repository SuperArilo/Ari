package com.tty.command.lists;

import com.tty.command.function.CommandSpawn;
import com.tty.enumType.AriCommand;
import com.tty.enumType.FilePath;
import com.tty.function.CommandCheck;
import com.tty.function.impl.CommandCheckImpl;
import com.tty.lib.enum_type.LangType;
import com.tty.tool.ConfigObjectUtils;
import com.tty.tool.TextTool;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class setspawn implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        CommandCheckImpl commandCheck = CommandCheck.create(sender, command, AriCommand.SETSPAWN);
        if (!commandCheck.isTheInstructionCorrect()) return false;
        if (commandCheck.allCheck()) {
            Player player = (Player) sender;
            Location location = player.getLocation();
            boolean set = new CommandSpawn(sender).set(location);
            if (set) {
                String st = ConfigObjectUtils.getValue("function.spawn.set-success", FilePath.Lang.getName(), String.class, "null");
                st = st.replace(LangType.SPAWNLOCATION.getType(), TextTool.XYZText(location.getX(), location.y(), location.z()));
                sender.sendMessage(TextTool.setHEXColorText(st));
            } else {
                sender.sendMessage(TextTool.setHEXColorText("function.spawn.set-failure", FilePath.Lang));
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return List.of();
    }
}
