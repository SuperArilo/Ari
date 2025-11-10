package com.tty.lib.tool;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComponentUtils {

    private static final Pattern GRADIENT_PATTERN = Pattern.compile("<#([A-Fa-f0-9]{6})>(.*?)</#([A-Fa-f0-9]{6})>");

    public static TextComponent text(String content) {
        if (content == null || content.equals("null")) {
            return returnNoContentText();
        }
        return renderComponent(content, null);
    }

    public static TextComponent text(String content, Player player) {
        if (content == null || content.equals("null")) {
            return returnNoContentText();
        }
        return renderComponent(content, player);
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
                text(title),
                text(subTitle),
                Title.Times.times(Duration.ofMillis(fadeIn), Duration.ofMillis(stay), Duration.ofMillis(fadeOut))
        );
    }

    public static TextComponent setClickEventText(String content, ClickEvent.Action action, String actionText) {
        return text(content).clickEvent(ClickEvent.clickEvent(action, actionText));
    }

    public static TextComponent setHoverText(String content, String showText) {
        return text(content).hoverEvent(HoverEvent.showText(text(showText)));
    }

    public static Component setHoverItem(ItemStack itemStack) {
        if (itemStack == null) return Component.empty();
        return itemStack.displayName().hoverEvent(itemStack.asHoverEvent(showItem -> showItem));
    }

    public static Component setEntityText(Entity entity) {
        return Component.text(entity.getName()).hoverEvent(entity.asHoverEvent());
    }

    protected static TextComponent renderComponent(String content, Player player) {
        if (player != null && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            content = PlaceholderAPI.setPlaceholders(player, content);
        }
        Matcher matcher = GRADIENT_PATTERN.matcher(ColorConverterLegacy.convert(content));
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
    protected static Component generateGradientText(String text, String startColorHex, String endColorHex) {
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
    protected static int[] hexToRgb(String hex) {
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        return new int[]{r, g, b};
    }

    /**
     * 当出错时候返回到客户端的文本
     */
    protected static TextComponent returnNoContentText() {
        return Component.text("Warning: content is null, see in the console");
    }
}
