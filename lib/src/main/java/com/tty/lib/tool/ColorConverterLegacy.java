package com.tty.lib.tool;

import lombok.Getter;

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
     * 改进版本：支持 &21919810 这种情况
     * @param content 含有颜色代码的文本
     * @return 转换后的带十六进制颜色标签的文本
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

        // Step 1️⃣：先替换 & 为 §
        if (hasAmpersand) {
            content = content.replaceAll("(?i)&([0-9a-f])", "§$1");
        }

        // Step 2️⃣：修复颜色码紧挨着字符或数字的情况
        // 在颜色码后如果直接跟字母或数字，自动插入空格
        content = content.replaceAll("(?i)§([0-9a-f])(?=[A-Za-z0-9])", "§$1");

        String normalized = content;

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
                    // 输出之前累积的文字
                    if (!currentText.isEmpty()) {
                        appendColoredText(result, currentText, currentColor);
                        currentText = new StringBuilder();
                    }
                    currentColor = color.getHex();
                    i++; // 跳过颜色码字符
                    continue;
                }
            }
            currentText.append(c);
        }

        appendColoredText(result, currentText, currentColor);

        return result.toString();
    }

    private static void appendColoredText(StringBuilder result, StringBuilder text, String color) {
        if (text.isEmpty()) return;

        if (color != null) {
            result.append("<").append(color).append(">")
                    .append(text)
                    .append("</").append(color).append(">");
        } else {
            result.append(text);
        }
    }
}
