package ari.superarilo;

import ari.superarilo.entity.TeleportStatus;
import ari.superarilo.enumType.Commands;
import ari.superarilo.tool.ConfigFiles;
import ari.superarilo.tool.TeleportThread;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class SuperArilo extends JavaPlugin {
    public static SuperArilo instance;
    //severe error
    public static Logger logger;

    private static final List<TeleportStatus> teleportStatusList = new ArrayList<>();

    @Override
    public void onLoad() {
        instance = this;
        logger = instance.getLogger();
        ConfigFiles.reloadAllConfig();
    }

    @Override
    public void onEnable() {
        this.registerCommands();
    }
    @Override
    public void onDisable() {
    }

    private void registerCommands() {
        for (Commands command : Commands.values()) {
            String showName = command.getShow();
            if (showName == null) return;
            PluginCommand pluginCommand = SuperArilo.instance.getCommand(showName);
            if(pluginCommand == null) return;
            pluginCommand.setExecutor(command.getCommandClass());
            if(command.getShow() != null) {
                pluginCommand.setTabCompleter(command.getCommandClass());
            }
        }
    }


    public synchronized static void addTeleportStatus(TeleportStatus status) {
        teleportStatusList.add(status);
    }
    public synchronized static void deleteAddTeleportStatus(UUID uuid, TeleportThread.Type type) {
        teleportStatusList.removeIf(obj -> obj.getPlayUUID().equals(uuid) && obj.getType().equals(type));
    }
    public static List<TeleportStatus> getTeleportStatusList() {
        return teleportStatusList;
    }
}
