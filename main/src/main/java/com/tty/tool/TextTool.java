package com.tty.tool;

import com.tty.Ari;
import com.tty.enumType.FilePath;
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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
public class TextTool {

    private static final Pattern START_TAG_PATTERN = Pattern.compile("<#([A-Fa-f0-9]{6})>"); // 开始标签
    private static final Pattern END_TAG_PATTERN = Pattern.compile("</#([A-Fa-f0-9]{6})>"); // 结束标签

    /**
     * 设置彩色文本格式
     * @param path 内容在yaml的路径
     * @param filePath yaml文件路径
     * @param player 需要PAPI变量时的玩家对象
     * @return 返回彩色文本
     */
    public static TextComponent setHEXColorText(String path, FilePath filePath, Player player) {
        String content = Ari.instance.configManager.getValue(path, filePath, String.class);
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
        String content = Ari.instance.configManager.getValue(path, filePath, String.class);
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
        return "&2x: &6" + Ari.instance.formatUtils.formatTwoDecimalPlaces(x) +
                " &2y: &6" + Ari.instance.formatUtils.formatTwoDecimalPlaces(y) +
                " &2z: &6" + Ari.instance.formatUtils.formatTwoDecimalPlaces(z);
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
        if (content.contains("<#") && content.contains("</#")) {
            TextComponent.Builder builder = Component.text();
            hexadecimalStrings(content).forEach(part -> {
                if (part.startsWith("<#")) {
                    List<String> colors = separateHexString(part);
                    String startColor = colors.get(0);
                    String endColor = colors.get(1);
                    String text = part
                            .replaceFirst("<#" + startColor.substring(1) + ">", "")
                            .replaceFirst("</#" + endColor.substring(1) + ">", "");
                    int length = text.length();
                    for (int i = 0; i < length; i++) {
                        double ratio = i / (double) (length - 1);
                        TextColor color = HexColorMake(startColor, endColor, ratio);
                        builder.append(Component.text(text.charAt(i), color));
                    }
                } else {
                    builder.append(Component.text(ChatColor.translateAlternateColorCodes('&', part)));
                }
            });
            return builder.decoration(TextDecoration.ITALIC, false).build();
        } else {
            return Component.text(ChatColor.translateAlternateColorCodes('&', content))
                    .decoration(TextDecoration.ITALIC, false);
        }
    }

    //分离具有16进制标签的文本
    protected static List<String> hexadecimalStrings(String content) {
        List<String> parts = new ArrayList<>();
        Matcher startMatcher = START_TAG_PATTERN.matcher(content);
        Matcher endMatcher = END_TAG_PATTERN.matcher(content);

        int lastEnd = 0;

        while (startMatcher.find() && endMatcher.find(startMatcher.end())) {
            int start = startMatcher.start();
            int endEnd = endMatcher.end();
            if (lastEnd < start) {
                parts.add(content.substring(lastEnd, start));
            }
            String segment = content.substring(start, endEnd);
            parts.add(segment);
            lastEnd = endEnd;
            startMatcher.region(endEnd, content.length());
            endMatcher.region(endEnd, content.length());
        }
        if (lastEnd < content.length()) {
            parts.add(content.substring(lastEnd));
        }

        return parts;
    }

    //获取具有十六进制的字符串的开始颜色和结束颜色
    protected static List<String> separateHexString(String content) {
        List<String> colors = new ArrayList<>();
        Matcher startMatcher = START_TAG_PATTERN.matcher(content);
        if (startMatcher.find()) {
            colors.add("#" + startMatcher.group(1));
        }
        Matcher endMatcher = END_TAG_PATTERN.matcher(content);
        if (endMatcher.find()) {
            colors.add("#" + endMatcher.group(1));
        }
        if (colors.size() == 2) {
            return colors;
        } else {
            return List.of("#FFFFFF", "#FFFFFF");
        }
    }

    protected static TextColor HexColorMake(String startColor, String endColor, double ratio) {
        TextColor start = TextColor.fromHexString(startColor);
        TextColor end = TextColor.fromHexString(endColor);
        if (start == null || end == null) {
            return TextColor.color(255, 255, 255);
        }
        int startRgb = start.value();
        int endRgb = end.value();
        int rgb = interpolate(startRgb, endRgb, ratio);
        return TextColor.color(rgb);
    }

    private static int interpolate(int start, int end, double ratio) {
        int r = (int) (( (start >> 16) & 0xFF ) * (1 - ratio) + ( (end >> 16) & 0xFF ) * ratio);
        int g = (int) (( (start >> 8) & 0xFF ) * (1 - ratio) + ( (end >> 8) & 0xFF ) * ratio);
        int b = (int) ( (start & 0xFF ) * (1 - ratio) + (end & 0xFF ) * ratio );
        return (r << 16) | (g << 8) | b;
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
