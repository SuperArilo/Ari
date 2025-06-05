package com.tty.tool;

import com.tty.Ari;
import com.tty.enumType.FilePath;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.google.gson.Gson;
import java.lang.reflect.Type;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class ConfigManager {
    private Map<String, YamlConfiguration> configs = new ConcurrentHashMap<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .create();
    public ConfigManager() {
        this.reloadAllConfig();
    }
    public void reloadAllConfig() {
        Ari.instance.reloadConfig();
        boolean newDebugState = Ari.instance.getConfig().getBoolean("debug.enable", false);
        if (!Ari.debug && newDebugState) {
            Ari.instance.saveResource("config.yml", true);
            Ari.instance.reloadConfig();
            Ari.debug = true;
        } else {
            Ari.debug = newDebugState;
        }
        this.checkFiles();
        Log.debug(Level.INFO, "----------------");
        Log.debug(Level.INFO, "   " + this.getValue("debug.on-open", FilePath.Lang, String.class) + "   ");
        Log.debug(Level.INFO, "----------------");
    }
    protected void checkFiles() {
        this.configs = new ConcurrentHashMap<>();
        FileConfiguration pluginConfig = Ari.instance.getConfig();
        for (FilePath filePath : FilePath.values()) {
            String path = filePath.getPath();
            if(filePath.equals(FilePath.Lang)) {
                path = path.replace("[lang]", Ari.instance.getConfig().getString("lang", "cn"));
            }
            File file = new File(Ari.instance.getDataFolder(), path);
            if (!file.exists()) {
                Ari.instance.saveResource(path, true);
            } else if (pluginConfig.getBoolean("debug.enable", false)) {
                if (pluginConfig.getBoolean("debug.overwrite-file", false)) {
                    Ari.instance.saveResource(path, true);
                }
            }
            this.configs.put(filePath.getName(), YamlConfiguration.loadConfiguration(file));
        }
    }

    /**
     * 根据指定的文件和路径获取指定的值
     * @param valuePath 值的路径
     * @param filePath 文件对象Type
     * @param type 值的类型
     * @return 返回指定类型
     */
    public <T> T getValue(String valuePath, FilePath filePath, Type type) {
        String fileName = filePath.getName();
        YamlConfiguration fileConfiguration = this.getObject(fileName);
        if (fileConfiguration == null) {
            Log.error("Config file not found: " + fileName);
            return null;
        }
        Object value = fileConfiguration.get(valuePath);
        if (value == null) {
            Log.error("Value not found for path: " + valuePath + " in file: " + fileName);
            return null;
        }
        if (value instanceof MemorySection) {
            YamlConfiguration tempConfig = new YamlConfiguration();
            ObjectConvert.copySectionToYamlConfiguration((ConfigurationSection) value, tempConfig);
            return Ari.instance.objectConvert.yamlConvertToObj(tempConfig.saveToString(), type);
        }
        try {
            return this.gson.fromJson(this.gson.toJson(value), type);
        } catch (JsonSyntaxException e) {
            Log.error("Failed to convert value at path: " + valuePath + " in file: " + fileName + " to type: " + type.getTypeName(), e);
            return null;
        }
    }
    /**
     *
     * 获取指定文件名的yaml配置文件对象
     * @param fileName 文件名
     * @return 返回 YamlConfiguration
     */
    public YamlConfiguration getObject(String fileName) {
        return this.configs.get(fileName);
    }
}
