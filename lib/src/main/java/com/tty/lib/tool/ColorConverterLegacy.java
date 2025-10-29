package com.tty.lib.tool;

import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

public class ColorConverterLegacy {

    @Getter
    public enum MinecraftColor {
        BLACK('0', "#000000"),
        DARK_BLUE('1', "#0000AA"),
        DARK_GREEN('2', "#00AA00"),
        DARK_AQUA('3', "#00AAAA"),
        DARK_RED('4', "#AA0000"),
        DARK_PURPLE('5', "#AA00AA"),
        GOLD('6', "#FFAA00"),
        GRAY('7', "#AAAAAA"),
        DARK_GRAY('8', "#555555"),
        BLUE('9', "#5555FF"),
        GREEN('a', "#55FF55"),
        AQUA('b', "#55FFFF"),
        RED('c', "#FF5555"),
        LIGHT_PURPLE('d', "#FF55FF"),
        YELLOW('e', "#FFFF55"),
        WHITE('f', "#FFFFFF");

        private final char code;
        private final String hex;

        private static final Map<Character, MinecraftColor> BY_CODE = new HashMap<>();

        static {
            for (MinecraftColor color : values()) {
                BY_CODE.put(color.code, color);
            }
        }

        MinecraftColor(char code, String hex) {
            this.code = code;
            this.hex = hex;
        }

        public static MinecraftColor getByCode(char code) {
            return BY_CODE.get(Character.toLowerCase(code));
        }

    }

    /**
     * 高性能版本：提前检查是否包含颜色代码
     * @param content 包含 § 或 & 颜色代码的原始文本
     * @return 转换为16进制格式的文本
     */
    public static String convert(String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }

        boolean hasAmpersand = content.indexOf('&') >= 0;
        boolean hasSection = content.indexOf('§') >= 0;

        if (!hasAmpersand && !hasSection) {
            return content;
        }

        String normalized;
        if (hasAmpersand) {
            normalized = ChatColor.translateAlternateColorCodes('&', content);
        } else {
            normalized = content;
        }

        if (normalized.indexOf('§') < 0) {
            return normalized;
        }

        StringBuilder result = new StringBuilder();
        StringBuilder currentText = new StringBuilder();
        String currentColor = null;

        for (int i = 0; i < normalized.length(); i++) {
            char c = normalized.charAt(i);
            if (c == '§' && i + 1 < normalized.length()) {
                char colorCode = normalized.charAt(i + 1);
                MinecraftColor color = MinecraftColor.getByCode(colorCode);
                if (color != null) {
                    if (!currentText.isEmpty()) {
                        appendColoredText(result, currentText, currentColor);
                        currentText = new StringBuilder();
                    }
                    currentColor = color.getHex();
                    i++;
                } else {
                    currentText.append(c);
                }
            } else {
                currentText.append(c);
            }
        }
        appendColoredText(result, currentText, currentColor);

        return result.toString();
    }

    private static void appendColoredText(StringBuilder result, StringBuilder text, String color) {
        if (text.isEmpty()) return;

        if (color != null) {
            result.append("<").append(color).append(">")
                    .append(text).append("</").append(color).append(">");
        } else {
            result.append(text);
        }
    }
}
