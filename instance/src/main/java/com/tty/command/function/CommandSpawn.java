package com.tty.command.function;

import com.tty.Ari;
import com.tty.enumType.FilePath;
import com.tty.lib.enum_type.LangType;
import com.tty.tool.ConfigObjectUtils;
import com.tty.function.Teleport;
import com.tty.tool.TextTool;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSpawn {

    private final CommandSender sender;

    public CommandSpawn(CommandSender sender) {
        this.sender = sender;
    }

    public void set(Location location) {
        if (this.sender instanceof Player) {
            boolean b = ((Player) this.sender).getWorld().setSpawnLocation(location);
            if (b) {
                String st = ConfigObjectUtils.getValue("function.spawn.set-success", FilePath.Lang.getName(), String.class, "null");
                st = st.replace(LangType.SPAWNLOCATION.getType(), TextTool.XYZText(location.getX(), location.y(), location.z()));
                sender.sendMessage(TextTool.setHEXColorText(st));
            } else {
                sender.sendMessage(TextTool.setHEXColorText("function.spawn.set-failure", FilePath.Lang));
            }
        }
    }

    public void convey() {
        if (this.sender instanceof Player player) {
            Teleport.create(
                    player,
                    player.getWorld().getSpawnLocation(),
                    Ari.instance.getConfig().getInt("server.spawn.teleport-delay", 3))
                    .teleport();
        }
    }

}
