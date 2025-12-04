package com.tty.tool;

import com.tty.Ari;
import com.tty.enumType.FilePath;
import com.tty.lib.tool.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;

import java.util.Map;

public class ConfigUtils {

    /**
     * 快捷访问 LANG
     * @param key 在 lang 中对应的 key 路径
     * @return 返回构建完成的 Component
     */
    public static TextComponent t(String key) {
        return ComponentUtils.text(Ari.C_INSTANCE.getValue(key, FilePath.LANG));
    }

    public static TextComponent t(String key, Map<String, Component> componentMap) {
        return ComponentUtils.text(Ari.C_INSTANCE.getValue(key, FilePath.LANG), componentMap);
    }

    public static TextComponent t(String key, Player player) {
        return ComponentUtils.text(Ari.C_INSTANCE.getValue(key, FilePath.LANG), player);
    }

}
