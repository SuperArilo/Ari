package com.tty.lib.tool;
import com.google.gson.Gson;
import com.tty.lib.Log;
import com.tty.lib.enum_type.FunctionType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatUtils {

    private static final ThreadLocal<DecimalFormat> DECIMAL_FORMAT =
            ThreadLocal.withInitial(() -> new DecimalFormat("#.00"));

    private static final String ID_NAME_REGEX = "^[a-zA-Z0-9_]+$";
    private static final String NAME_REGEX = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$";
    private static final String PERMISSION_NODE_REGEX = "^[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)*$";
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\[(\\w+)]");

    /**
     * 格式化数字保留两位小数
     * @param number 需要格式化的数字（支持所有Number子类）
     * @return 格式化后的字符串，输入为null时返回"0.00"
     */
    public static String formatTwoDecimalPlaces(Number number) {
        if (number == null) return "0.00";
        return DECIMAL_FORMAT.get().format(number);
    }

    /**
     * 检查ID名称合法性（字母数字下划线）
     * @param content 待检查字符串
     * @return 空值或不符合格式返回false
     */
    public static boolean checkIdName(String content) {
        return content != null && content.matches(ID_NAME_REGEX);
    }

    /**
     * 检查名称合法性（支持中文字符）
     * @param content 待检查字符串
     * @return 空值或不符合格式返回false
     */
    public static boolean checkName(String content) {
        return content != null && content.matches(NAME_REGEX);
    }

    /**
     * 验证Minecraft权限节点格式
     * @param node 权限节点字符串
     * @return 空值或不符合格式返回false
     */
    public static boolean isValidPermissionNode(String node) {
        return node != null && node.matches(PERMISSION_NODE_REGEX);
    }

    /**
     * 将 Component 转成 String
     * @param component 被转对象
     * @return 返回String
     */
    public static String componentToString(Component component) {
        if (component == null) return "";
        if(component instanceof TextComponent) {
            return ((TextComponent) component).content();
        }
        return component.toString();
    }

    /**
     * 返回 基础格式化的文本坐标
     * @param x x轴
     * @param y y轴
     * @param z z轴
     * @return 返回基础格式化的文本坐标
     */
    public static String XYZText(Double x, Double y, Double z) {
        return "&2x: &6" + FormatUtils.formatTwoDecimalPlaces(x) +
                " &2y: &6" + FormatUtils.formatTwoDecimalPlaces(y) +
                " &2z: &6" + FormatUtils.formatTwoDecimalPlaces(z);
    }

    /**
     * 将 string 的 Location 转换成 Location对象
     * @param locString string
     * @return Location 对象
     */
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

    /**
     * 将 yaml 的字符串转换成指定类型
     * @param raw yaml 字符串
     * @param type 转换的目标类型
     * @return 返回 的 type 类型
     */
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

    /**
     * 检查GUI里的 function icon 是否合法
     * @param rawType function icon 的字符串
     * @return 返回指定的 FunctionType
     */
    public static FunctionType ItemNBT_TypeCheck(String rawType) {
        if(rawType == null) return null;
        FunctionType type;
        try {
            type = FunctionType.valueOf(rawType.toUpperCase());
            return type;
        } catch (Exception e) {
            Log.debug(e, "Function type %s error", rawType);
            return null;
        }
    }

    /**
     * 提取文本中所有匹配 LangType 枚举的占位符
     * 只返回枚举名称，不包含其他文本
     */
    public static List<String> extractLangPlaceholders(String text) {
        List<String> result = new ArrayList<>();
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        while (matcher.find()) {
            result.add(matcher.group(0));
        }
        return result;
    }

}
