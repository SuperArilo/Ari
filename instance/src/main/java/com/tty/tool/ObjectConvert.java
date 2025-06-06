package com.tty.tool;

import com.tty.enumType.FunctionType;
import com.google.gson.Gson;
import com.tty.lib.tool.Log;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ObjectConvert {
    private final Gson gson = new Gson();
    private final Yaml yaml;
    public ObjectConvert() {
        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setAllowRecursiveKeys(true);
        loaderOptions.setAllowDuplicateKeys(false);
        this.yaml = new Yaml(loaderOptions);
    }

    public <T> T yamlConvertToObj(String raw, Type type) {
        Object intermediateObj = yaml.load(raw);
        if (intermediateObj instanceof Map || intermediateObj instanceof List) {
            return gson.fromJson(gson.toJson(intermediateObj), type);
        }
        return gson.fromJson(gson.toJsonTree(intermediateObj), type);
    }
    public FunctionType ItemNBT_TypeCheck(String rawType) {
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
    public Location parseLocation(String locString) {
        if (locString == null || locString.isEmpty()) {
            throw new IllegalArgumentException("Location string is null or empty!");
        }
        // 去除外层包装 "Location{" 和 "}"
        if (locString.startsWith("Location{") && locString.endsWith("}")) {
            locString = locString.substring("Location{".length(), locString.length() - 1);
        }

        // 解析 world 部分
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
        // 如果格式为 CraftWorld{name=world}，提取真正的世界名
        if (worldPart.startsWith("CraftWorld{") && worldPart.contains("name=")) {
            int nameStart = worldPart.indexOf("name=") + 5;
            int nameEnd = worldPart.indexOf('}', nameStart);
            worldPart = worldPart.substring(nameStart, nameEnd);
        }

        // 解析 x
        int xIndex = locString.indexOf("x=", worldEnd);
        int xStart = xIndex + 2;
        int xEnd = locString.indexOf(',', xStart);
        double x = Double.parseDouble(locString.substring(xStart, xEnd).trim());

        // 解析 y
        int yIndex = locString.indexOf("y=", xEnd);
        int yStart = yIndex + 2;
        int yEnd = locString.indexOf(',', yStart);
        double y = Double.parseDouble(locString.substring(yStart, yEnd).trim());

        // 解析 z
        int zIndex = locString.indexOf("z=", yEnd);
        int zStart = zIndex + 2;
        int zEnd = locString.indexOf(',', zStart);
        double z = Double.parseDouble(locString.substring(zStart, zEnd).trim());

        // 解析 pitch
        int pitchIndex = locString.indexOf("pitch=", zEnd);
        int pitchStart = pitchIndex + 6;
        int pitchEnd = locString.indexOf(',', pitchStart);
        float pitch;
        if (pitchEnd > 0) {
            pitch = Float.parseFloat(locString.substring(pitchStart, pitchEnd).trim());
        } else {
            // 这里 pitch 的位置在最后，则 yaw 信息丢失，需抛异常
            throw new IllegalArgumentException("Incomplete location string, missing yaw: " + locString);
        }

        // 解析 yaw
        int yawIndex = locString.indexOf("yaw=", pitchEnd);
        if (yawIndex < 0) {
            throw new IllegalArgumentException("Missing yaw in location string: " + locString);
        }
        int yawStart = yawIndex + 4;
        // yaw 位于字符串末尾，trim 后不会带有 '}'
        float yaw = Float.parseFloat(locString.substring(yawStart).trim());

        // 获取 world 对象（注意：在 Folia 中需要确保在主线程调用）
        World world = Bukkit.getWorld(worldPart);
        if (world == null) {
            throw new IllegalArgumentException("World not found: " + worldPart);
        }
        return new Location(world, x, y, z, yaw, pitch);
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
                // 创建新节点并递归复制
                ConfigurationSection newSection = target.createSection(key);
                copySectionToYamlConfiguration((ConfigurationSection) value, newSection);
            } else {
                // 直接设置值
                target.set(key, value);
            }
        }
    }
}
