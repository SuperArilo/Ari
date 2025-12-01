package com.tty.commands.sub.zako;

import com.mojang.brigadier.arguments.ArgumentType;
import com.tty.Ari;
import com.tty.enumType.FilePath;
import com.tty.lib.Log;
import com.tty.lib.command.BaseCommand;
import org.bukkit.Bukkit;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public abstract class ZakoBase<T> extends BaseCommand<T> {

    protected ZakoBase(boolean allowConsole, ArgumentType<T> type, int correctArgsLength) {
        super(allowConsole, type, correctArgsLength);
    }

    /**
     * 根据输入参数解析 UUID
     * @param value 玩家名字或 UUID
     * @return 玩家 UUID，如果不存在则返回 null
     */
    protected UUID parseUUID(String value) {
        AtomicReference<UUID> uuid = new AtomicReference<>(null);

        // 尝试直接解析 UUID
        try {
            uuid.set(UUID.fromString(value));
        } catch (Exception e) {
            Log.debug(e, "zako is not a uuid: %s", value);
        }

        // 如果不是 UUID，则尝试通过玩家名获取
        if (uuid.get() == null) {
            try {
                uuid.set(Bukkit.getOfflinePlayer(value).getUniqueId());
            } catch (Exception e) {
                Log.error(e, Ari.C_INSTANCE.getValue("function.zako.not-exist", FilePath.LANG));
                return null;
            }
        }

        return uuid.get();
    }
}
