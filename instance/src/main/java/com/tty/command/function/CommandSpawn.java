package com.tty.command.function;

import com.tty.Ari;
import com.tty.entity.sql.ServerSpawn;
import com.tty.enumType.FilePath;
import com.tty.function.SpawnManager;
import com.tty.function.Teleport;
import com.tty.gui.spawn.SpawnList;
import com.tty.lib.Lib;
import com.tty.lib.dto.Page;
import com.tty.lib.tool.ComponentUtils;
import com.tty.lib.tool.FormatUtils;
import com.tty.lib.tool.Log;
import com.tty.lib.tool.PublicFunctionUtils;
import com.tty.tool.ConfigUtils;
import com.tty.tool.PermissionUtils;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSpawn {

    private final CommandSender sender;
    private final SpawnManager manager = new SpawnManager(true);

    public CommandSpawn(CommandSender sender) {
        this.sender = sender;
    }

    public void set(String spawnId) {
        if (this.sender instanceof Player player) {
            this.manager.getList(Page.create(1, Integer.MAX_VALUE))
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
                        this.manager.createInstance(spawn)
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

    public void convey() {
        Player player = (Player) this.sender;
        this.manager.getList(Page.create(1, 1))
            .thenAccept(list -> {
                if (list.isEmpty()) {
                    this.sender.sendMessage(ComponentUtils.text(ConfigUtils.getValue("function.spawn.no-spawn", FilePath.Lang)));
                    return;
                }
                ServerSpawn serverSpawn = list.getFirst();
                String permission = serverSpawn.getPermission();
                if(permission != null && !permission.isEmpty()) {
                    boolean hasPermission = PermissionUtils.hasPermission(player, permission);
                    if (!hasPermission && !player.isOp()) {
                        player.sendMessage(ComponentUtils.text(ConfigUtils.getValue("function.spawn.no-permission-teleport", FilePath.Lang)));
                        return;
                    }
                }
                Teleport.create(player,
                                FormatUtils.parseLocation(serverSpawn.getLocation()),
                                ConfigUtils.getValue("main.teleport-delay", FilePath.SpawnConfig, Integer.class, 3))
                        .teleport();
            }).exceptionally(i -> {
               Log.error("teleport spawn error", i);
               this.sender.sendMessage(ComponentUtils.text(ConfigUtils.getValue("base.on-error", FilePath.Lang)));
                    return null;
            });
    }

}
