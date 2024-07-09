package ari.superarilo.tool;

import ari.superarilo.enumType.FilePath;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class ConfigFiles {

    private Map<String, YamlConfiguration> configs = new ConcurrentHashMap<>();
    private final JavaPlugin instance;

    public ConfigFiles(JavaPlugin instance) {
        this.instance = instance;
        this.reloadAllConfig();
    }

    public void reloadAllConfig() {
        this.instance.saveDefaultConfig();
        this.instance.reloadConfig();
        this.checkFiles();
    }
    protected void checkFiles() {
        this.configs = new ConcurrentHashMap<>();
        for (FilePath filePath : FilePath.values()) {
            String path = filePath.getPath();
            File file = new File(this.instance.getDataFolder(), path);
            this.instance.saveResource(path, true);
//            if(!file.exists()) {
//                this.instance.saveResource(path, false);
//            }
            this.configs.put(filePath.getName(), YamlConfiguration.loadConfiguration(file));
        }
    }
    public <T> T getValue(String valuePath, FilePath filePath, Class<T> clazz) {
        String fileName = filePath.getName();
        YamlConfiguration fileConfiguration = this.configs.get(fileName);
        if (fileConfiguration == null) {
            this.instance.getLogger().log(Level.WARNING, "Config file not found: " + fileName);
            return null;
        }
        Object value = fileConfiguration.get(valuePath);
        if (value == null) {
            this.instance.getLogger().log(Level.WARNING, "Value not found for path: " + valuePath + " in file: " + fileName);
            return null;
        }
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        } else {
            this.instance.getLogger().log(Level.WARNING, "Value type mismatch for path: " + valuePath + " in file: " + fileName);
            return null;
        }
    }
    public YamlConfiguration getObject(String fileName) {
        return this.configs.get(fileName);
    }
}
