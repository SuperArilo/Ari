package ari.superarilo.tool;

import ari.superarilo.Ari;
import ari.superarilo.dto.AliasItem;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.enumType.FilePath;
import com.google.gson.reflect.TypeToken;
import org.bukkit.command.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class CommandAlias {

    private static final String localPath = "ari.superarilo.command.lists.";
    private Constructor<PluginCommand> constructor;
    private Map<String, AliasItem> alias;

    public CommandAlias() {
        try {
            this.constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            this.constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            Log.error("Error while constructing instruction: ", e);
            return;
        }
        this.init();
    }

    public void init() {
        YamlConfiguration aliasFile = Ari.instance.configManager.getObject(FilePath.CommandAlias.getName());
        this.alias = Ari.instance.objectConvert.yamlConvertToObj(aliasFile.saveToString(), new TypeToken<Map<String, AliasItem>>(){}.getType());
        this.registerAlias();
    }
    private void registerAlias() {
        Log.debug("----------register command ----------");
        long start = System.currentTimeMillis();
        CommandMap commandMap = Ari.instance.getServer().getCommandMap();
        this.alias.forEach((k, v) -> {
            if(!v.isEnable()) return;
            PluginCommand pluginCommand = this.build(k);
            if(pluginCommand == null) {
                Log.debug("register command [" + k + "] error");
                return;
            }
            pluginCommand.setPermission(AriCommand.valueOf(k.toUpperCase()).getPermission());
            pluginCommand.setName(k);
            pluginCommand.setLabel(k);
            //这里必须添加插件的名称
            boolean register = commandMap.register(Ari.instance.getName(), pluginCommand);
            Log.debug("register command [" + k + "] status: " + register);
        });
        Log.debug("register alias time: " + (System.currentTimeMillis() - start) + "ms");
        Log.debug("----------register command end ----------");
    }
    public void reloadAllAlias() {
        Log.debug("---------- unregister command ----------");
        CommandMap commandMap = Ari.instance.getServer().getCommandMap();
        Map<String, Command> knownCommands = commandMap.getKnownCommands();
        this.alias.forEach((k, v) -> {
            Command command = commandMap.getCommand(k);
            if(command == null) return;
            knownCommands.remove(Ari.instance.getName() + ":" + k);
            knownCommands.remove(k);
            boolean unregister = command.unregister(commandMap);
            Log.debug("unregister command [" + k + "] status: " + unregister);
        });
        this.init();
        Log.debug("---------- unregister command end ----------");
    }
    private PluginCommand build(String commandName) {
        PluginCommand pluginCommand;
        try {
            pluginCommand = this.constructor.newInstance(commandName, Ari.instance);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            Log.error("Error while constructing instruction: " + commandName, e);
            return null;
        }

        Class<?> executorClass;
        try {
            executorClass = Class.forName(localPath + commandName);
        } catch (ClassNotFoundException e) {
            Log.error("Error while constructing instruction. " + commandName + " class not found!", e);
            return null;
        }

        Object executorInstance;
        try {
            executorInstance = executorClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            Log.error("Error while constructing executor for instruction: " + commandName, e);
            return null;
        }

        if (executorInstance instanceof CommandExecutor) {
            pluginCommand.setExecutor((CommandExecutor) executorInstance);
        } else {
            Log.error("Executor class " + commandName + " does not implement CommandExecutor.");
            return null;
        }

        if (executorInstance instanceof TabCompleter) {
            pluginCommand.setTabCompleter((TabCompleter) executorInstance);
        } else {
            Log.error("Executor class " + commandName + " does not implement TabCompleter.");
            return null;
        }
        return pluginCommand;
    }
}
