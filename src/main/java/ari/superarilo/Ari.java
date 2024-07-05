package ari.superarilo;

import ari.superarilo.entity.TpStatusValue;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.tool.ConfigFiles;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Ari extends JavaPlugin {
    public static Ari instance;
    //severe error
    public static Logger logger;

    private TpStatusValue tpStatusValue;
    private ConfigFiles configFiles;

    @Override
    public void onLoad() {
        instance = this;
        logger = instance.getLogger();
        this.configFiles = new ConfigFiles(this);
    }

    @Override
    public void onEnable() {
        this.registerCommands();
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

    public TpStatusValue getTpStatusValue() {
        return tpStatusValue;
    }

    public ConfigFiles getConfigFiles() {
        return configFiles;
    }
}
