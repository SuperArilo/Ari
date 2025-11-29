package com.tty.lib.tool;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.tty.lib.Log;
import com.tty.lib.enum_type.FilePathEnum;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.tty.lib.tool.FormatUtils.copySectionToYamlConfiguration;
import static com.tty.lib.tool.FormatUtils.yamlConvertToObj;

public class ConfigInstance {

    protected final Map<String, YamlConfiguration> CONFIGS = new ConcurrentHashMap<>();

    /**
     * 根据指定的文件和路径获取指定的值 String
     * @param keyPath 值的路径
     * @param filePath 文件名字
     * @return 返回字符串类型
     */
    public <E extends Enum<E> & FilePathEnum> String getValue(String keyPath, E filePath) {
        return getValue(keyPath, filePath, String.class, "null");
    }

    /**
     * 根据指定的文件和路径获取指定的对象
     * @param keyPath key 路径
     * @param filePath 文件路径
     * @param tClass 类型
     * @return 返回 T
     * @param <T> 指定的类型
     */
    public <T, E extends Enum<E> & FilePathEnum> T getValue(String keyPath, E filePath, Class<T> tClass) {
        if(checkPath(keyPath)) return null;
        YamlConfiguration configuration = checkConfiguration(filePath);
        if (configuration == null) return null;
        return configuration.getObject(keyPath, tClass);
    }

    /**
     * 根据指定的文件和路径获取指定的值
     * @param keyPath 值的路径
     * @param filePath 文件名字
     * @param type 值的类型
     * @return 返回指定类型
     */
    public <T, E extends Enum<E> & FilePathEnum> T getValue(String keyPath, E filePath, Type type, T defaultValue) {
        if (checkPath(keyPath)) return defaultValue;

        YamlConfiguration fileConfiguration = checkConfiguration(filePath);
        if (fileConfiguration == null) return defaultValue;

        Object value = fileConfiguration.get(keyPath);
        if (value == null) {
            Log.error("Value not found for path: %s in file: %s", keyPath, filePath.name());
            return defaultValue;
        }
        if (value instanceof MemorySection) {
            YamlConfiguration tempConfig = new YamlConfiguration();
            copySectionToYamlConfiguration((ConfigurationSection) value, tempConfig);
            return yamlConvertToObj(tempConfig.saveToString(), type);
        }
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        try {
            return gson.fromJson(gson.toJsonTree(value), type);
        } catch (JsonSyntaxException e) {
            Log.error(e, "Failed to convert value at path: %s in file: %s to type: %s", keyPath, filePath.name(), type.getTypeName());
            return defaultValue;
        }
    }

    /**
     *
     * 获取指定文件名的yaml配置文件对象
     * @param fileName 文件名
     * @return 返回 YamlConfiguration
     */
    public YamlConfiguration getObject(String fileName) {
        return CONFIGS.get(fileName);
    }

    /**
     * 检查 key 的路径是否存在
     * @param path 路径
     * @return 返回布尔值
     */
    private  boolean checkPath(String path) {
        boolean empty = path.isEmpty();
        if (empty) {
            Log.error("file path %s is empty", path);
        }
        return empty;
    }

    /**
     * 将特定对象写入指定的文件
     * @param keyPath key路径
     * @param filePath 文件路径
     * @param value 写入的值
     */
    public <T extends Enum<T> & FilePathEnum> void setValue(JavaPlugin plugin, String keyPath, T filePath, Object value) {
        YamlConfiguration configuration = this.checkConfiguration(filePath);
        if (configuration == null) throw new RuntimeException("Config file not found: " + filePath.name());
        configuration.set(keyPath, value);
        setConfig(filePath.name(), configuration);
        File file = new File(plugin.getDataFolder(), filePath.getPath());
        try {
            configuration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 检查根据 FilePath 的文件是否存在于内存中
     * @param filePath 文件名
     * @return 返回Yaml对象
     */
    private <T extends Enum<T> & FilePathEnum> YamlConfiguration checkConfiguration(T filePath) {
        YamlConfiguration configuration = this.getObject((filePath.name()));
        if (configuration == null) {
            Log.error("Config file not found: %s", filePath.name());
            return null;
        }
        return configuration;
    }

    public void setConfigs(Map<String, YamlConfiguration> configs) {
        CONFIGS.putAll(configs);
    }

    public void setConfig(String name, YamlConfiguration instance) {
        CONFIGS.put(name, instance);
    }

    public void clearConfigs() {
        CONFIGS.clear();
    }
}
