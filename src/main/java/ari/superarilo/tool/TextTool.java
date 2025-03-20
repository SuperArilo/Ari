package ari.superarilo.tool;

import ari.superarilo.Ari;
import ari.superarilo.enumType.FilePath;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
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

    private static final Pattern TAG_PATTERN = Pattern.compile("<#\\w+>.*?</#\\w+>");

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
    public static TextComponent setNoColorText(String content) {
        return Component.text(content);
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
        if(content.contains("<#") && content.contains("</#")) {
            TextComponent.Builder builder = Component.text();
            //处理后的字符串文字
            hexadecimalStrings(content).forEach(e -> {
                e = ChatColor.translateAlternateColorCodes('&', e);
                //获取遍历的字符串前后的十六进制颜色字符串
                List<String> li = separateHexString(e);
                if (li.size() == 2) {
                    String be = e.replace("<" + li.get(0) + ">", "").replace("</" + li.get(1) + ">", "");
                    int length = be.length();
                    for (double i = 0;i < length; i++) {
                        double ratio = i / (length - 1);
                        builder.append(Component.text(be.charAt((int) i), HexColorMake(li.get(0), li.get(1), ratio)));
                    }
                } else {
                    builder.append(Component.text(e));
                }
            });
            return builder.build();
        } else {
            return Component.text(ChatColor.translateAlternateColorCodes('&', content));
        }
    }

    public static TextComponent setClickEventText(String content, ClickEvent.Action action, String actionText) {
        return setHEXColorText(content).clickEvent(ClickEvent.clickEvent(action, actionText));
    }

    //分离具有16进制标签的文本
    protected static List<String> hexadecimalStrings(String content) {
        List<String> parts = new ArrayList<>();
        Matcher matcher = TAG_PATTERN.matcher(content);

        int lastEnd = 0;
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            if (lastEnd != start) {
                // Add the text between tags
                parts.add(content.substring(lastEnd, start));
            }
            // Add the matched tag with content
            parts.add(content.substring(start, end));
            lastEnd = end;
        }
        // Add any trailing text after the last tag
        if (lastEnd != content.length()) {
            parts.add(content.substring(lastEnd));
        }

        return parts;
    }

    //获取具有十六进制的字符串的开始颜色和结束颜色
    protected static List<String> separateHexString(String content) {
        List<String> colors = new ArrayList<>();
        // 正则表达式匹配所有颜色代码
        String regex = "<#([A-Fa-f0-9]{6})>|</#([A-Fa-f0-9]{6})>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                // 开始标签的颜色代码
                colors.add("#" + matcher.group(1));
            } else if (matcher.group(2) != null) {
                // 结束标签的颜色代码
                colors.add("#" + matcher.group(2));
            }
        }
        return colors;
    }

    protected static TextColor HexColorMake(String startColor, String endColor, Double ratio) {
        TextColor start = TextColor.fromHexString(startColor);
        TextColor end = TextColor.fromHexString(endColor);
        if(start == null || end == null){
            return TextColor.color(0, 0, 0);
        }
        return TextColor.color(interpolate(start.value(), end.value(), ratio));
    }

    protected static int interpolate(int start, int end, double ratio) {

        int r = (int) ((start >> 16 & 0xFF) * (1 - ratio) + (end >> 16 & 0xFF) * ratio);
        int g = (int) ((start >> 8 & 0xFF) * (1 - ratio) + (end >> 8 & 0xFF) * ratio);
        int b = (int) ((start & 0xFF) * (1 - ratio) + (end & 0xFF) * ratio);
        return (r << 16) | (g << 8) | b;
    }

    /**
     * 当出错时候返回到客户端的文本
     */
    protected static TextComponent returnNoContentText() {
        return Component.text("Warning: content is null, see in the console");
    }

}
