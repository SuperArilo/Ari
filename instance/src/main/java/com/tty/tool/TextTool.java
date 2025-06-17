package com.tty.tool;

import com.tty.enumType.FilePath;
import com.tty.lib.tool.FormatUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
public class TextTool {

    private static final Pattern GRADIENT_PATTERN = Pattern.compile("<#([A-Fa-f0-9]{6})>(.*?)</#([A-Fa-f0-9]{6})>");

    /**
     * 设置彩色文本格式
     * @param path 内容在yaml的路径
     * @param filePath yaml文件路径
     * @param player 需要PAPI变量时的玩家对象
     * @return 返回彩色文本
     */
    public static TextComponent setHEXColorText(String path, FilePath filePath, Player player) {
        String content = ConfigObjectUtils.getValue(path, filePath.getName(), String.class, "null");
        if (content == null) {
            Log.error(path + " path does not exist in the " + filePath.getName() + " file");
            Log.error(filePath.getName() + " path: " + filePath.getPath());
            return returnNoContentText();
        }
        return renderComponent(content, player);
    }

    /**
     * 设置彩色文本格式
     * @param path 内容在yaml的路径
     * @param filePath yaml文件路径
     * @return 返回彩色文本
     */
    public static TextComponent setHEXColorText(String path, FilePath filePath) {
        String content = ConfigObjectUtils.getValue(path, filePath.getName(), String.class, "null");
        if (content == null) {
            Log.error(path + " path does not exist in the " + filePath.getName() + " file");
            Log.error(filePath.getName() + " path: " + filePath.getPath());
            return returnNoContentText();
        }
        return renderComponent(content, null);
    }

    /**
     * 设置彩色文本格式
     * @param content 被设置的内容
     * @param player 需要PAPI变量时的玩家对象
     * @return 返回彩色文本
     */
    public static TextComponent setHEXColorText(String content, Player player) {
        if(content == null) return returnNoContentText();
        return renderComponent(content, player);
    }

    /**
     * 设置彩色文本格式
     * @param content 被设置的内容
     * @return 返回彩色文本
     */
    public static TextComponent setHEXColorText(String content) {
        if(content == null) return returnNoContentText();
        return renderComponent(content, null);
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
     * 将 Component 转成 String
     * @param component 被转对象
     * @return 返回String
     */
    public static String componentToString(Component component) {
        if(component instanceof TextComponent) {
            return ((TextComponent) component).content();
        }
        return component.toString();
    }

    /**
     * 设置MC的全屏通知效果(Title
     * @param title 显示的title文本
     * @param subTitle 显示的副标题文本
     * @param fadeIn 进入动画时间 毫秒
     * @param stay 停留时间 毫秒
     * @param fadeOut 退出动画时间 毫秒
     * @return 返沪一个为Player设置的Title对象
     */
    public static Title setPlayerTitle(@NotNull String title, @NotNull String subTitle, long fadeIn, long stay, long fadeOut) {
        return Title.title(
                setHEXColorText(title),
                setHEXColorText(subTitle),
                Title.Times.times(Duration.ofMillis(fadeIn), Duration.ofMillis(stay), Duration.ofMillis(fadeOut))
        );
    }
    @NotNull
    protected static TextComponent renderComponent(String content, Player player) {
        if (player != null && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            content = PlaceholderAPI.setPlaceholders(player, content);
        }
        content = ChatColor.translateAlternateColorCodes('&', content);
        Matcher matcher = GRADIENT_PATTERN.matcher(content);
        int lastEnd = 0;
        TextComponent.Builder builder = Component.text();

        while (matcher.find()) {
            int start = matcher.start();
            if (start > lastEnd) {
                builder.append(Component.text(content.substring(lastEnd, start)));
            }

            String startColorHex = matcher.group(1);
            String text = matcher.group(2);
            String endColorHex = matcher.group(3);

            builder.append(generateGradientText(text, startColorHex, endColorHex));

            lastEnd = matcher.end();
        }

        if (lastEnd < content.length()) {
            builder.append(Component.text(content.substring(lastEnd)));
        }

        return builder.build().decoration(TextDecoration.ITALIC, false);
    }
    /**
     * 生成渐变文本的 Component
     */
    private static Component generateGradientText(String text, String startColorHex, String endColorHex) {
        int[] startColor = hexToRgb(startColorHex);
        int[] endColor = hexToRgb(endColorHex);

        TextComponent.Builder gradientBuilder = Component.text();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            // 避免除以 0 的情况
            double ratio = (text.length() > 1)
                    ? (double) i / (text.length() - 1)
                    : 0.0;

            int r = (int) (startColor[0] + (endColor[0] - startColor[0]) * ratio);
            int g = (int) (startColor[1] + (endColor[1] - startColor[1]) * ratio);
            int b = (int) (startColor[2] + (endColor[2] - startColor[2]) * ratio);

            gradientBuilder.append(Component.text(c).color(TextColor.color(r, g, b)));
        }

        return gradientBuilder.build();
    }

    /**
     * 将十六进制颜色字符串转换为 RGB 数组
     */
    private static int[] hexToRgb(String hex) {
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        return new int[]{r, g, b};
    }


    public static TextComponent setClickEventText(String content, ClickEvent.Action action, String actionText) {
        return setHEXColorText(content).clickEvent(ClickEvent.clickEvent(action, actionText));
    }
    /**
     * 当出错时候返回到客户端的文本
     */
    protected static TextComponent returnNoContentText() {
        return Component.text("Warning: content is null, see in the console");
    }

}
