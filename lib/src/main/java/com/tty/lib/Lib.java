package com.tty.lib;

import com.tty.lib.scheduler.BukkitScheduler;
import com.tty.lib.scheduler.FoliaScheduler;
import com.tty.lib.tool.Log;
import org.bukkit.plugin.java.JavaPlugin;

public class Lib extends JavaPlugin {

    public static Lib instance;
    public static Scheduler Scheduler = ServerPlatform.isFolia() ? new FoliaScheduler():new BukkitScheduler();

    @Override
    public void onLoad() {
        Log.initLogger(this.getLogger(), false);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }




}
