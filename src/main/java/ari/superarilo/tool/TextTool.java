package ari.superarilo.tool;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
public class TextTool {

    public static TextComponent setHEXColorText(String content) {
        if(content.contains("&")) {
            return Component.text(ChatColor.translateAlternateColorCodes('&', content));
        } else if(content.contains("<#") && content.contains("</#")) {
            TextComponent.Builder builder = Component.text();
            //处理后的字符串文字
            hexadecimalStrings(content).forEach(e -> {
                //获取遍历的字符串前后的十六进制颜色字符串
                List<String> li = separateHexString(e);
                if (li.size() < 2) return;
                String be = e.replace("<" + li.get(0) + ">", "").replace("</" + li.get(1) + ">", "");

                int length = be.length();

                for (double i = 0;i < length; i++) {
                    double ratio = i / (length - 1);
                    builder.append(Component.text(be.charAt((int) i), HexColorMake(li.get(0), li.get(1), ratio)));
                }
            });
            return builder.build();
        } else {
            return Component.text(content);
        }

    }

    public static TextComponent setClickEventText(String content, ClickEvent.Action action, String actionText) {
        return setHEXColorText(content).clickEvent(ClickEvent.clickEvent(action, actionText));
    }

    //分离具有16进制标签的文本
    private static List<String> hexadecimalStrings(String content) {

        List<String> l = new ArrayList<>();

        Matcher matcher = Pattern.compile("<#.*?>.*?</#.*?>").matcher(content);

        while (matcher.find()) {
            l.add(matcher.group());
        }
        return l;
    }

    //获取具有十六进制的字符串的开始颜色和结束颜色
    private static List<String> separateHexString(String content) {
        List<String> i = new ArrayList<>();
        Matcher matcher = Pattern.compile("<#([^<>]+)>([^<>]+)</#([^<>]+)>").matcher(content);
        if (matcher.find()) {
            i.add("#" + matcher.group(1));
            i.add("#" + matcher.group(3)) ;
        }
        return i;
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
