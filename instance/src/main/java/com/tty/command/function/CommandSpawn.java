package com.tty.command.function;

import com.tty.Ari;
import com.tty.enumType.FilePath;
import com.tty.lib.enum_type.LangType;
import com.tty.lib.tool.ComponentUtils;
import com.tty.lib.tool.FormatUtils;
import com.tty.tool.ConfigUtils;
import com.tty.function.Teleport;
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
                String st = ConfigUtils.getValue("function.spawn.set-success", FilePath.Lang);
                st = st.replace(LangType.SPAWNLOCATION.getType(), FormatUtils.XYZText(location.getX(), location.y(), location.z()));
                sender.sendMessage(ComponentUtils.text(st));
            } else {
                sender.sendMessage(ComponentUtils.text(ConfigUtils.getValue("function.spawn.set-failure", FilePath.Lang)));
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
