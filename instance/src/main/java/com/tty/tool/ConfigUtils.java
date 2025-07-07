package com.tty.tool;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.tty.Ari;
import com.tty.enumType.FilePath;
import com.tty.lib.tool.Log;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.tty.lib.tool.FormatUtils.copySectionToYamlConfiguration;
import static com.tty.lib.tool.FormatUtils.yamlConvertToObj;

public class ConfigUtils {

    protected static final Map<String, YamlConfiguration> CONFIGS = new ConcurrentHashMap<>();

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
     * 根据指定的文件和路径获取指定的值
     * @param keyPath 值的路径
     * @param filePath 文件名字
     * @param type 值的类型
     * @return 返回指定类型
     */
    public static <T> T getValue(String keyPath, FilePath filePath, Type type, T defaultValue) {
        if (keyPath.isEmpty()) {
            Log.error("file path is empty");
            return defaultValue;
        }
        YamlConfiguration fileConfiguration = getObject(filePath.name());
        if (fileConfiguration == null) {
            Log.error("Config file not found: " + filePath.name());
            return defaultValue;
        }
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

    public static void setRtpWorldConfig() {

        Map<String, Object> value = getValue(
                "rtp.worlds",
                FilePath.FunctionConfig,
                new TypeToken<Map<String, Object>>(){}.getType(),
                null);
        YamlConfiguration function = getObject(FilePath.FunctionConfig.name());

        if(function.getKeys(false).isEmpty()) {
            Log.error("rtp initialization failed, Function file is null");
            return;
        }

        if (value == null) {
            value = new HashMap<>();
            for (World world : Bukkit.getWorlds()) {
                value.put(world.getName(), createWorldRtp());
            }
            function.set("rtp.worlds", value);
        } else {
            for (World world : Bukkit.getWorlds()) {
                if (value.containsKey(world.getName())) continue;
                value.put(world.getName(), createWorldRtp());
            }
            function.set("rtp.worlds", value);
        }
        setConfig(FilePath.FunctionConfig.name(), function);

        File file = new File(Ari.instance.getDataFolder(), FilePath.FunctionConfig.getPath());
        try {
            function.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, Object> createWorldRtp() {
        Map<String, Object> map = new HashMap<>();
        map.put("enable", true);
        map.put("min", 300);
        map.put("max", 1500);
        return map;
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
