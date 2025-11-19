package com.tty.tool;

import com.google.gson.reflect.TypeToken;
import com.tty.Ari;
import com.tty.dto.AliasItem;
import com.tty.enumType.FilePath;
import com.tty.lib.command.SuperHandsomeCommand;
import com.tty.lib.tool.FormatUtils;
import com.tty.lib.tool.Log;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Map;

public class CommandAlias {

    private static final String localPath = "com.tty.commands.";
    private Map<String, AliasItem> alias;

    public CommandAlias() {
        YamlConfiguration aliasFile = Ari.C_INSTANCE.getObject(FilePath.CommandAlias.name());
        this.alias = FormatUtils.yamlConvertToObj(aliasFile.saveToString(), new TypeToken<Map<String, AliasItem>>() {
        }.getType());
    }

    public void registerAlias() {
        Log.debug("----------register commands ----------");
        long start = System.currentTimeMillis();
        Ari.instance.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Commands commands = event.registrar();
            this.alias.forEach((k, v) -> {
                if (!v.isEnable()) return;
                Class<?> executorClass;
                try {
                    executorClass = Class.forName(localPath + k);
                } catch (ClassNotFoundException e) {
                    Log.error("Error while constructing instruction. " + k + " class not found!", e);
                    return;
                }
                Object executorInstance;
                try {
                    executorInstance = executorClass.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    Log.error("Error while constructing executor for instruction: " + k, e);
                    return;
                }
                if (executorInstance instanceof SuperHandsomeCommand cmd) {
                    commands.register(cmd.toBrigadier(), v.getUsage());
                    Log.debug("register command: " + k);
                }
            });
            Log.debug("register commands time: " + (System.currentTimeMillis() - start) + "ms");
        });
    }

    public void reloadAllAlias() {
        Log.debug("---------- unregister command ----------");
        YamlConfiguration aliasFile = Ari.C_INSTANCE.getObject(FilePath.CommandAlias.name());
        this.alias = FormatUtils.yamlConvertToObj(aliasFile.saveToString(), new TypeToken<Map<String, AliasItem>>() {
        }.getType());
        Log.debug("---------- unregister command end ----------");
    }
}
