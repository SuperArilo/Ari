package com.tty;

import com.tty.entity.TpStatusValue;
import com.tty.enumType.AriCommand;
import com.tty.enumType.FilePath;
import com.tty.function.PlayerTabManager;
import com.tty.lib.ServerPlatform;
import com.tty.lib.tool.Log;
import com.tty.listener.OnPluginReloadListener;
import com.tty.listener.PlayerListener;
import com.tty.listener.home.EditHomeListener;
import com.tty.listener.home.HomeListListener;
import com.tty.listener.player.OnPlayerListener;
import com.tty.listener.skip_sleep.PlayerSkipNight;
import com.tty.listener.teleport.RecordLastLocationListener;
import com.tty.listener.warp.EditWarpListener;
import com.tty.listener.warp.WarpListListener;
import com.tty.papi.HomePAPI;
import com.tty.tool.*;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;


public class Ari extends JavaPlugin {

    public static Ari instance;
    public static Boolean debug = false;
    public TpStatusValue tpStatusValue;
    public ConfigManager configManager;
    public ObjectConvert objectConvert;
    public SQLInstance SQLInstance;
    public FormatUtils formatUtils;
    public PermissionUtils permissionUtils;
    public EconomyUtils economyUtils;
    public CommandAlias commandAlias;
    @Override
    public void onLoad() {
        instance = this;
        this.configManager = new ConfigManager();
        this.formatUtils = new FormatUtils();
        this.objectConvert = new ObjectConvert();
        Log.initLogger(this.getLogger(), Ari.debug);
        Log.debug(Level.INFO, "----------------");
        Log.debug(Level.INFO, "   " + this.configManager.getValue("debug.on-open", FilePath.Lang, String.class) + "   ");
        Log.debug(Level.INFO, "----------------");
    }

    @Override
    public void onEnable() {
        this.registerCommands();
        this.registerListener();

        this.commandAlias = new CommandAlias();
        //group
        this.permissionUtils = new PermissionUtils();
        //PAPI
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new HomePAPI().register();
        }
        //sql
        this.SQLInstance = new SQLInstance();
        //em
        this.economyUtils = new EconomyUtils();
        this.tpStatusValue = new TpStatusValue();

        if (ServerPlatform.isFolia()) {
            Log.debug("running folia");
        } else {
            Log.debug("running bukkit");
        }
    }
    @Override
    public void onDisable() {
    }

    private void registerCommands() {
        PluginCommand pluginCommand = this.getCommand(AriCommand.ARI.getShow());
        TabExecutor commandClass = AriCommand.ARI.getCommandClass();
        if (pluginCommand != null) {
            pluginCommand.setExecutor(commandClass);
            pluginCommand.setTabCompleter(commandClass);
        }
    }
    private void registerListener() {
        Bukkit.getPluginManager().registerEvents(new HomeListListener(), this);
        Bukkit.getPluginManager().registerEvents(new EditHomeListener(), this);
        Bukkit.getPluginManager().registerEvents(new RecordLastLocationListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new WarpListListener(), this);
        Bukkit.getPluginManager().registerEvents(new EditWarpListener(), this);
        Bukkit.getPluginManager().registerEvents(new OnPlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerSkipNight(), this);
        Bukkit.getPluginManager().registerEvents(new OnPluginReloadListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerTabManager(), this);
    }
}
