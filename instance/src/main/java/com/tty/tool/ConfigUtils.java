package com.tty.tool;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.tty.Ari;
import com.tty.enumType.FilePath;
import com.tty.lib.tool.ComponentUtils;
import com.tty.lib.tool.Log;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.tty.lib.tool.FormatUtils.copySectionToYamlConfiguration;
import static com.tty.lib.tool.FormatUtils.yamlConvertToObj;

public class ConfigUtils {

    protected static final Map<String, YamlConfiguration> CONFIGS = new ConcurrentHashMap<>();

    /**
     * 快捷访问 Lang
     * @param key 在 lang 中对应的 key 路径
     * @return 返回构建完成的 Component
     */
    public static TextComponent t(String key) {
        return ComponentUtils.text(getValue(key, FilePath.Lang));
    }

    public static TextComponent t(String key, Player player) {
        return ComponentUtils.text(getValue(key, FilePath.Lang), player);
    }

    public static TextComponent t(String key, String old, String rep_new) {
        return ComponentUtils.text(getValue(key, FilePath.Lang).replace(old, rep_new));
    }

    public static TextComponent t(String key, String old, String rep_new, Player player) {
        return ComponentUtils.text(getValue(key, FilePath.Lang).replace(old, rep_new), player);
    }

    /**
     *
     * 获取指定文件名的yaml配置文件对象
     * @param fileName 文件名
     * @return 返回 YamlConfiguration
     */
    public static YamlConfiguration getObject(String fileName) {
        return CONFIGS.get(fileName);
    }

    /**
     * 根据指定的文件和路径获取指定的值 String
     * @param keyPath 值的路径
     * @param filePath 文件名字
     * @return 返回字符串类型
     */
    public static String getValue(String keyPath, FilePath filePath) {
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
    public static <T> T getValue(String keyPath, FilePath filePath, Class<T> tClass) {
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
    public static <T> T getValue(String keyPath, FilePath filePath, Type type, T defaultValue) {
        if (checkPath(keyPath)) return defaultValue;

        YamlConfiguration fileConfiguration = checkConfiguration(filePath);
        if (fileConfiguration == null) return defaultValue;

        Object value = fileConfiguration.get(keyPath);
        if (value == null) {
            Log.error("Value not found for path: " + keyPath + " in file: " + filePath.name());
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
            Log.error("Failed to convert value at path: " + keyPath + " in file: " + filePath.name() + " to type: " + type.getTypeName(), e);
            return defaultValue;
        }
    }

    /**
     * 将特定对象写入指定的文件
     * @param keyPath key路径
     * @param filePath 文件路径
     * @param value 写入的值
     */
    public static void setValue(String keyPath, FilePath filePath, Object value) {
        YamlConfiguration configuration = checkConfiguration(filePath);
        if (configuration == null) throw new RuntimeException("Config file not found: " + filePath.name());
        configuration.set(keyPath, value);
        setConfig(filePath.name(), configuration);
        File file = new File(Ari.instance.getDataFolder(), filePath.getPath());
        try {
            configuration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 检查 key 的路径是否存在
     * @param path 路径
     * @return 返回布尔值
     */
    private static boolean checkPath(String path) {
        boolean empty = path.isEmpty();
        if (empty) {
            Log.error("file path is empty");
        }
        return empty;
    }

    /**
     * 检查根据 FilePath 的文件是否存在于内存中
     * @param filePath 文件名
     * @return 返回Yaml对象
     */
    private static YamlConfiguration checkConfiguration(FilePath filePath) {
        YamlConfiguration configuration = getObject((filePath.name()));
        if (configuration == null) {
            Log.error("Config file not found: " + filePath.name());
            return null;
        }
        return configuration;
    }

    public static void setConfigs(Map<String, YamlConfiguration> configs) {
        CONFIGS.putAll(configs);
    }

    public static void setConfig(String name, YamlConfiguration instance) {
        CONFIGS.put(name, instance);
    }

    public static void clearConfigs() {
        CONFIGS.clear();
    }
}
