package com.tty.tool;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.tty.Ari;
import com.tty.enumType.FilePath;
import com.tty.lib.enum_type.FunctionType;
import com.tty.lib.tool.Log;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

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
        YamlConfiguration fileConfiguration = getObject(filePath.getName());
        if (fileConfiguration == null) {
            Log.error("Config file not found: " + filePath.getName());
            return defaultValue;
        }
        Object value = fileConfiguration.get(keyPath);
        if (value == null) {
            Log.error("Value not found for path: " + keyPath + " in file: " + filePath.getName());
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
            Log.error("Failed to convert value at path: " + keyPath + " in file: " + filePath.getName() + " to type: " + type.getTypeName(), e);
            return defaultValue;
        }
    }

    /**
     * 将 MemorySection 转成 YamlConfiguration
     * @param source MemorySection
     * @param target YamlConfiguration
     */
    public static void copySectionToYamlConfiguration(ConfigurationSection source, ConfigurationSection target) {
        Map<String, Object> values = source.getValues(false);

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof ConfigurationSection) {
                ConfigurationSection newSection = target.createSection(key);
                copySectionToYamlConfiguration((ConfigurationSection) value, newSection);
            } else {
                target.set(key, value);
            }
        }
    }

    public static <T> T yamlConvertToObj(String raw, Type type) {
        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setAllowRecursiveKeys(true);
        loaderOptions.setAllowDuplicateKeys(false);
        Gson gson = new Gson();
        Object intermediateObj = new Yaml(loaderOptions).load(raw);
        if (intermediateObj instanceof Map || intermediateObj instanceof List) {
            return gson.fromJson(gson.toJson(intermediateObj), type);
        }
        return gson.fromJson(gson.toJsonTree(intermediateObj), type);
    }
    public static FunctionType ItemNBT_TypeCheck(String rawType) {
        if(rawType == null) return null;
        FunctionType type;
        try {
            type = FunctionType.valueOf(rawType.toUpperCase());
            return type;
        } catch (Exception e) {
            Log.debug(Level.INFO, "Function type error", e);
            return null;
        }
    }

    public static Location parseLocation(String locString) {
        if (locString == null || locString.isEmpty()) {
            throw new IllegalArgumentException("Location string is null or empty!");
        }
        if (locString.startsWith("Location{") && locString.endsWith("}")) {
            locString = locString.substring("Location{".length(), locString.length() - 1);
        }
        int worldIndex = locString.indexOf("world=");
        if (worldIndex < 0) {
            throw new IllegalArgumentException("Missing 'world' in location string: " + locString);
        }
        int worldStart = worldIndex + 6;
        int worldEnd = locString.indexOf(',', worldStart);
        if (worldEnd < 0) {
            throw new IllegalArgumentException("Invalid world format: " + locString);
        }
        String worldPart = locString.substring(worldStart, worldEnd).trim();
        if (worldPart.startsWith("CraftWorld{") && worldPart.contains("name=")) {
            int nameStart = worldPart.indexOf("name=") + 5;
            int nameEnd = worldPart.indexOf('}', nameStart);
            worldPart = worldPart.substring(nameStart, nameEnd);
        }

        int xIndex = locString.indexOf("x=", worldEnd);
        int xStart = xIndex + 2;
        int xEnd = locString.indexOf(',', xStart);
        double x = Double.parseDouble(locString.substring(xStart, xEnd).trim());

        int yIndex = locString.indexOf("y=", xEnd);
        int yStart = yIndex + 2;
        int yEnd = locString.indexOf(',', yStart);
        double y = Double.parseDouble(locString.substring(yStart, yEnd).trim());

        int zIndex = locString.indexOf("z=", yEnd);
        int zStart = zIndex + 2;
        int zEnd = locString.indexOf(',', zStart);
        double z = Double.parseDouble(locString.substring(zStart, zEnd).trim());

        int pitchIndex = locString.indexOf("pitch=", zEnd);
        int pitchStart = pitchIndex + 6;
        int pitchEnd = locString.indexOf(',', pitchStart);
        float pitch;
        if (pitchEnd > 0) {
            pitch = Float.parseFloat(locString.substring(pitchStart, pitchEnd).trim());
        } else {
            throw new IllegalArgumentException("Incomplete location string, missing yaw: " + locString);
        }

        int yawIndex = locString.indexOf("yaw=", pitchEnd);
        if (yawIndex < 0) {
            throw new IllegalArgumentException("Missing yaw in location string: " + locString);
        }
        int yawStart = yawIndex + 4;
        float yaw = Float.parseFloat(locString.substring(yawStart).trim());

        World world = Bukkit.getWorld(worldPart);
        if (world == null) {
            throw new IllegalArgumentException("World not found: " + worldPart);
        }
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static void setRtpWorldConfig() {

        Map<String, Object> value = getValue(
                "rtp.worlds",
                FilePath.FunctionConfig,
                new TypeToken<Map<String, Object>>(){}.getType(),
                null);
        YamlConfiguration function = getObject(FilePath.FunctionConfig.getName());

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
        setConfig(FilePath.FunctionConfig.getName(), function);

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
