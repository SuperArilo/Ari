package com.tty.command.function;

import com.tty.Ari;
import com.tty.enumType.FilePath;
import com.tty.function.Teleport;
import com.tty.lib.tool.Log;
import com.tty.tool.ConfigUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;


public class CommandSpawn {

    private final Player sender;

    public CommandSpawn(Player sender) {
        this.sender = sender;
    }

    public void set() {
        Location location = this.sender.getLocation();
        Ari.C_INSTANCE.setValue(Ari.instance, "main.location", FilePath.SpawnConfig, location);
        this.sender.sendMessage(ConfigUtils.t("function.spawn.create-success"));
    }

    public void convey() {
        Location value = Ari.C_INSTANCE.getValue("main.location", FilePath.SpawnConfig, Location.class);
        if(value == null) {
            Log.debug("location null");
            this.sender.sendMessage(ConfigUtils.t("function.spawn.no-spawn"));
            return;
        }
        Teleport.create(this.sender, value, Ari.C_INSTANCE.getValue("main.teleport-delay", FilePath.SpawnConfig, Integer.class, 3)).teleport();
    }

}
