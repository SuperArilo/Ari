package ari.superarilo.tool;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextTool {

    public static TextComponent setHEXColorText(String content) {
        if(content.contains("&")) {
            return Component.text(ChatColor.translateAlternateColorCodes('&', content));
        } else if(content.contains("<#") && content.contains("/>")) {
            Matcher matcher = Pattern.compile("(<#[a-z]+>.*?<#[a-z]+/>)|([^<]+)").matcher(content);
            while (matcher.find()) {
                System.out.println(matcher.group());
            }
            return Component.text("114514");
        } else {
            return Component.text(content);
        }
    }


    public static TextComponent setGradientText(@NotNull String content, String startColor, String endColor) {
        int length = content.length();
        if (length == 0 || startColor == null || startColor.equals("") || endColor == null || endColor.equals("")) return Component.text(content);
        TextComponent.Builder builder = Component.text();
        for (double i = 0;i < length; i++) {
            double ratio = i / (length - 1);
            TextColor color = HexColorMake(startColor, endColor, ratio);
            builder.append(Component.text(content.charAt((int) i), color));
        }
        return builder.build();
    }

    private static TextColor HexColorMake(String startColor, String endColor, Double ratio) {
        TextColor start = TextColor.fromHexString(startColor);
        TextColor end = TextColor.fromHexString(endColor);
        if(start == null || end == null){
            return TextColor.color(0, 0, 0);
        }
        return TextColor.color(interpolate(start.value(), end.value(), ratio));
    }

    private static int interpolate(int start, int end, double ratio) {

        int r = (int) ((start >> 16 & 0xFF) * (1 - ratio) + (end >> 16 & 0xFF) * ratio);
        int g = (int) ((start >> 8 & 0xFF) * (1 - ratio) + (end >> 8 & 0xFF) * ratio);
        int b = (int) ((start & 0xFF) * (1 - ratio) + (end & 0xFF) * ratio);
        return (r << 16) | (g << 8) | b;
    }
}
