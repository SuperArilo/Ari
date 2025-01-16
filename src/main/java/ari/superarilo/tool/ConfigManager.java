package ari.superarilo.tool;

import ari.superarilo.Ari;
import ari.superarilo.enumType.FilePath;
import com.google.gson.JsonSyntaxException;
import org.bukkit.configuration.file.YamlConfiguration;
import com.google.gson.Gson;
import java.lang.reflect.Type;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class ConfigManager {
    private Map<String, YamlConfiguration> configs = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();
    public ConfigManager() {
        this.reloadAllConfig();
    }
    public void reloadAllConfig() {
        //config.yml
        Ari.instance.saveDefaultConfig();
        Ari.instance.reloadConfig();
        Ari.debug = Ari.instance.getConfig().getBoolean("debug.enable", false);
        Log.debug(Level.INFO, "----------------");
        Log.debug(Level.INFO, "   调试模式开启   ");
        Log.debug(Level.INFO, "----------------");
        this.checkFiles();
    }
    protected void checkFiles() {
        this.configs = new ConcurrentHashMap<>();
        for (FilePath filePath : FilePath.values()) {
            String path = filePath.getPath();
            File file = new File(Ari.instance.getDataFolder(), path);
            if(!file.exists() || Ari.instance.getConfig().getBoolean("debug.overwrite-file", false)) {
                Ari.instance.saveResource(path, true);
            }
            this.configs.put(filePath.getName(), YamlConfiguration.loadConfiguration(file));
        }
    }
    public <T> T getValue(String valuePath, FilePath filePath, Type type) {
        String fileName = filePath.getName();
        YamlConfiguration fileConfiguration = this.configs.get(fileName);
        if (fileConfiguration == null) {
            Log.error("Config file not found: " + fileName);
            return null;
        }
        Object value = fileConfiguration.get(valuePath);
        if (value == null) {
            Log.error("Value not found for path: " + valuePath + " in file: " + fileName);
            return null;
        }
        try {
            return this.gson.fromJson(this.gson.toJsonTree(value), type);
        } catch (JsonSyntaxException e) {
            Log.error("Failed to convert value at path: " + valuePath + " in file: " + fileName + " to type: " + type.getTypeName(), e);
            return null;
        }
    }
    public YamlConfiguration getObject(String fileName) {
        return this.configs.get(fileName);
    }
}
