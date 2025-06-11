package com.tty.command.function;

import com.tty.Ari;
import com.tty.function.TeleportThread;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSpawn {

    private final CommandSender sender;

    public CommandSpawn(CommandSender sender) {
        this.sender = sender;
    }

    public boolean set(Location location) {
        if (this.sender instanceof Player) {
            return ((Player) this.sender).getWorld().setSpawnLocation(location);
        }
        return false;
    }

    public void convey() {
        if (this.sender instanceof Player player) {
            TeleportThread.playerToLocation(
                    player,
                    player.getWorld().getSpawnLocation())
                    .teleport(Ari.instance.getConfig().getInt("server.spawn.teleport-delay", 3));
        }
    }

}
