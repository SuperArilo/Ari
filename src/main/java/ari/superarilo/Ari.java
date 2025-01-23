package ari.superarilo;

import ari.superarilo.entity.TpStatusValue;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.listener.command.PreCommandListener;
import ari.superarilo.listener.home.EditHomeListener;
import ari.superarilo.listener.home.HomeListListener;
import ari.superarilo.papi.HomePAPI;
import ari.superarilo.tool.*;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class Ari extends JavaPlugin {

    public static Ari instance;
    public static Boolean debug;
    public TpStatusValue tpStatusValue;
    public final PluginManager pluginManager = Bukkit.getPluginManager();
    public ConfigManager configManager;
    public ObjectConvert objectConvert;
    public SQLInstance SQLInstance;
    public FormatUtil formatUtil;
    public PermissionUtils permissionUtils;
    public CommandAlias commandAlias;
    @Override
    public void onLoad() {
        instance = this;
        Log.setLogger(this.getLogger());
        this.configManager = new ConfigManager();
        this.formatUtil = new FormatUtil();
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
        if (this.pluginManager.isPluginEnabled("PlaceholderAPI")) {
            new HomePAPI().register();
        }
        //sql
        this.SQLInstance = new SQLInstance();
        this.tpStatusValue = new TpStatusValue();
    }
    @Override
    public void onDisable() {
    }

    protected void registerCommands() {
        for (AriCommand command : AriCommand.values()) {
            String showName = command.getShow();
            PluginCommand pluginCommand = this.getCommand(showName);
            if(pluginCommand == null) continue;
            TabExecutor commandClass = command.getCommandClass();
            pluginCommand.setExecutor(commandClass);
            pluginCommand.setTabCompleter(commandClass);
        }
    }
    protected void registerListener() {
        this.pluginManager.registerEvents(new HomeListListener(), this);
        this.pluginManager.registerEvents(new EditHomeListener(), this);
    }
}
