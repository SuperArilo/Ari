package ari.superarilo;

import ari.superarilo.entity.TpStatusValue;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.listener.home.EditHomeListener;
import ari.superarilo.listener.home.HomeListListener;
import ari.superarilo.papi.HomePAPI;
import ari.superarilo.tool.*;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Ari extends JavaPlugin {

    public static Ari instance;
    public static Boolean debug;
    public TpStatusValue tpStatusValue;

    public ConfigManager configManager;
    public ObjectConvert objectConvert;
    public SQLInstance SQLInstance;
    public NumberFormatUtil numberFormatUtil;

    @Override
    public void onLoad() {
        instance = this;
        Log.setLogger(this.getLogger());
        this.configManager = new ConfigManager();
        this.numberFormatUtil = new NumberFormatUtil();
        this.objectConvert = new ObjectConvert();
    }

    @Override
    public void onEnable() {
        this.registerCommands();
        this.registerListener();
        //PAPI
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
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
            if (showName == null) return;
            PluginCommand pluginCommand = Ari.instance.getCommand(showName);
            if(pluginCommand == null) return;
            pluginCommand.setExecutor(command.getCommandClass());
            pluginCommand.setTabCompleter(command.getCommandClass());
        }
    }
    protected void registerListener() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new HomeListListener(), this);
        pluginManager.registerEvents(new EditHomeListener(), this);
    }
}
