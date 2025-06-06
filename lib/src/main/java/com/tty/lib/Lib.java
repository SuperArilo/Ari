package com.tty.lib;

import com.tty.lib.scheduler.BukkitScheduler;
import com.tty.lib.scheduler.FoliaScheduler;
import org.bukkit.plugin.java.JavaPlugin;

public class Lib extends JavaPlugin {

    public static Scheduler Scheduler = ServerPlatform.isFolia() ? new FoliaScheduler():new BukkitScheduler();

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {

    }
}
