package ari.superarilo;

import ari.superarilo.entity.TpStatusValue;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.listener.PlayerListener;
import ari.superarilo.listener.home.EditHomeListener;
import ari.superarilo.listener.home.HomeListListener;
import ari.superarilo.listener.player.OnPlayerListener;
import ari.superarilo.listener.skipSleep.PlayerSkipNight;
import ari.superarilo.listener.teleport.RecordLastLocationListener;
import ari.superarilo.listener.warp.EditWarpListener;
import ari.superarilo.listener.warp.WarpListListener;
import ari.superarilo.papi.HomePAPI;
import ari.superarilo.tool.*;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;


public class Ari extends JavaPlugin {

    public static Ari instance;
    public static Boolean debug;
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
        Log.setLogger(this.getLogger());
        this.configManager = new ConfigManager();
        this.formatUtils = new FormatUtils();
        this.objectConvert = new ObjectConvert();
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
    }
}
