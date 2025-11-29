package com.tty.tool;

import com.google.gson.reflect.TypeToken;
import com.tty.Ari;
import com.tty.enumType.FilePath;
import com.tty.lib.enum_type.LangType;
import com.tty.lib.tool.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;

import java.util.List;
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

    public static TextComponent t(String key, Player player) {
        return ComponentUtils.text(Ari.C_INSTANCE.getValue(key, FilePath.LANG), player);
    }

    public static TextComponent t(String key, String old, String rep_new) {
        return ComponentUtils.text(Ari.C_INSTANCE.getValue(key, FilePath.LANG).replace(old, rep_new));
    }

    public static TextComponent t(String key, String old, String rep_new, Player player) {
        return ComponentUtils.text(Ari.C_INSTANCE.getValue(key, FilePath.LANG).replace(old, rep_new), player);
    }

    public static TextComponent ts(String key, Map<LangType, String> map) {
        List<String> value = Ari.C_INSTANCE.getValue(key, FilePath.LANG, new TypeToken<List<String>>() {
        }.getType(), List.of());
        TextComponent.Builder builder = Component.text();
        value.forEach(line -> {
            for (Map.Entry<LangType, String> entry : map.entrySet()) {
                String type = entry.getKey().getType();
                if (line.contains(type)) {
                    builder.append(ComponentUtils.text(line.replace(type, entry.getValue())))
                            .appendNewline();
                }
            }
        });
        return builder.build();
    }

}
