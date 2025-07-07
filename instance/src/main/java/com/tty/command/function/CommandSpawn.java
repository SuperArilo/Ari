package com.tty.command.function;

import com.tty.Ari;
import com.tty.entity.sql.ServerSpawn;
import com.tty.enumType.FilePath;
import com.tty.function.SpawnManager;
import com.tty.gui.spawn.SpawnList;
import com.tty.lib.Lib;
import com.tty.lib.dto.Page;
import com.tty.lib.tool.ComponentUtils;
import com.tty.lib.tool.Log;
import com.tty.lib.tool.PublicFunctionUtils;
import com.tty.tool.ConfigUtils;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSpawn {

    private final CommandSender sender;

    public CommandSpawn(CommandSender sender) {
        this.sender = sender;
    }

    public void set(String spawnId) {
        if (this.sender instanceof Player player) {
            SpawnManager manager = new SpawnManager(true);
            manager.getList(Page.create(1, Integer.MAX_VALUE))
                .thenAccept(list -> {
                    if (list.stream().anyMatch(c -> c.getSpawnId().equals(spawnId))) {
                        this.sender.sendMessage(ComponentUtils.text(ConfigUtils.getValue("function.spawn.exist", FilePath.Lang)));
                        return;
                    }
                    Location location = player.getLocation();
                    Lib.Scheduler.runAtRegion(Ari.instance, location, n -> {
                        ServerSpawn spawn = new ServerSpawn();
                        spawn.setSpawnId(spawnId);
                        spawn.setSpawnName(spawnId);
                        spawn.setCreateBy(player.getUniqueId().toString());
                        spawn.setLocation(location.toString());
                        spawn.setWorld(player.getWorld().getName());
                        spawn.setShowMaterial(PublicFunctionUtils.checkIsItem(location.getBlock().getRelative(BlockFace.DOWN).getType()).name());
                        manager.createInstance(spawn)
                                .thenAccept(status -> this.sender.sendMessage(ComponentUtils.text(ConfigUtils.getValue(status ? "function.spawn.create-success":"base.on-error", FilePath.Lang))))
                                .exceptionally(i -> {
                                    Log.error("create spawn error", i);
                                    this.sender.sendMessage(ComponentUtils.text(ConfigUtils.getValue("base.on-error", FilePath.Lang)));
                                    return null;
                                });
                    });
                }).exceptionally(i -> {
                    Log.error("create spawn error", i);
                    this.sender.sendMessage(ComponentUtils.text(ConfigUtils.getValue("base.on-error", FilePath.Lang)));
                    return null;
                });
        }
    }

    public void openList() {
        new SpawnList((Player) this.sender).open();
    }

    public void openEditor() {

    }
}
