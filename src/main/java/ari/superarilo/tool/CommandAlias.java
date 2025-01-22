package ari.superarilo.tool;

import ari.superarilo.Ari;
import ari.superarilo.dto.AliasItem;
import ari.superarilo.enumType.FilePath;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;
import java.util.Map;

public class CommandAlias {
    private YamlConfiguration aliasFile;
    private Map alias;

    public CommandAlias() {
        this.init();
        System.out.println(this.alias);
    }

    public void init() {
        if(this.aliasFile == null) {
            this.aliasFile = Ari.instance.configManager.getObject(FilePath.CommandAlias.getName());
        }
        this.alias = Ari.instance.objectConvert.yamlConvertToObj(this.aliasFile.saveToString(), Map.class);
        this.registerAlias();
    }
    private void registerAlias() {
        this.alias.forEach((key, value) -> {
            Command command = Ari.instance.getCommand((String) key);
            if(command == null)  {
                Log.error("Failed to load alias: " + key);
                return;
            }
            command.setAliases((List<String>) value);
        });
    }
}
