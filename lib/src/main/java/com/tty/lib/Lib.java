package com.tty.lib;

import com.tty.lib.scheduler.BukkitScheduler;
import com.tty.lib.scheduler.FoliaScheduler;
import com.tty.lib.tool.EconomyUtils;
import com.tty.lib.tool.Log;
import com.tty.lib.tool.PermissionUtils;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Lib extends JavaPlugin {

    public static Scheduler Scheduler = ServerPlatform.isFolia() ? new FoliaScheduler():new BukkitScheduler();

    @Override
    public void onLoad() {
        Log.initLogger(this.getLogger(), false);
    }

    @Override
    public void onEnable() {
        this.loadVault();
        this.loadPermission();
    }

    @Override
    public void onDisable() {

    }

    private void loadVault() {
        if(Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            RegisteredServiceProvider<Economy> provider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            if (provider != null) {
                EconomyUtils.setInstance(provider.getProvider());
                Log.info("Loaded economy plugin: " + provider.getProvider().getName());
            } else {
                Log.warning("No economy plugin is loaded");
            }
        } else {
            Log.warning("no vault, no Economy");
        }
    }

    private void loadPermission() {
        if(Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            RegisteredServiceProvider<Permission> registration = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
            if(registration != null) {
                Permission provider = registration.getProvider();
                PermissionUtils.setInstance(provider);
                Log.info("Loaded permission plugin: " + provider.getName());
            } else {
                Log.error("No permission plugin is loaded");
            }
        } else {
            Log.warning("no vault, use default");
        }
    }
}
