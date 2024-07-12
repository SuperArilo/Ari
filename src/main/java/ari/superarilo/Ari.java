package ari.superarilo;

import ari.superarilo.entity.TpStatusValue;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.listener.home.HomeListListener;
import ari.superarilo.papi.HomePAPI;
import ari.superarilo.tool.ConfigFiles;
import ari.superarilo.tool.SQLInstance;
import ari.superarilo.tool.ObjectConvert;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Ari extends JavaPlugin {

    public static Ari instance;
    public static Logger logger;
    public static Boolean debug;

    private TpStatusValue tpStatusValue;
    private ConfigFiles configFiles;
    private ObjectConvert objectConvert;

    private SQLInstance SQLInstance;

    @Override
    public void onLoad() {
        instance = this;
        logger = instance.getLogger();
        this.configFiles = new ConfigFiles(this);
        this.objectConvert = new ObjectConvert();

    }

    @Override
    public void onEnable() {
        this.registerCommands();
        this.registerListener();
        //PAPI
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) { //
            new HomePAPI(this).register();
        }
        //sql
        this.SQLInstance = new SQLInstance(this);


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
    }

    public TpStatusValue getTpStatusValue() {
        return tpStatusValue;
    }

    public ConfigFiles getConfigFiles() {
        return configFiles;
    }

    public ObjectConvert getGsonConvert() {
        return objectConvert;
    }

    public SQLInstance getSQLInstance() {
        return SQLInstance;
    }
}
