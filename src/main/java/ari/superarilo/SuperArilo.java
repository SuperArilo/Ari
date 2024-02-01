package ari.superarilo;

import ari.superarilo.enumType.Commands;
import ari.superarilo.tool.ConfigFiles;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;

public class SuperArilo extends JavaPlugin {

    public static SuperArilo instance;
    public static Logger logger;

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        instance = this;
        logger = instance.getLogger();
        ConfigFiles.checkFiles();
        registerCommands();
    }
    @Override
    public void onDisable() {

    }

    public static void registerCommands() {
        for (Commands command : Commands.values()) {
            String showName = command.getShow();
            if (showName == null) return;
            PluginCommand pluginCommand = SuperArilo.instance.getCommand(showName);
            if(pluginCommand == null) return;
            pluginCommand.setExecutor(command.getCommandClass());
            if(command.getTabCompleteList() != null) {
                pluginCommand.setTabCompleter(command.getCommandClass());
            }
        }
    }
}
