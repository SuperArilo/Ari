package com.tty.lib.tool;

import com.tty.lib.Lib;
import com.tty.lib.enum_type.FilePath;
import net.kyori.adventure.text.TextComponent;

public class LibConfigUtils {

    /**
     * 快捷访问 Lang
     * @param key 在 lang 中对应的 key 路径
     * @return 返回构建完成的 Component
     */
    public static TextComponent t(String key) {
        return ComponentUtils.text(Lib.C_INSTANCE.getValue(key, FilePath.Lang));
    }


}
