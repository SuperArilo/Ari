package com.tty.listener.player;

import com.google.gson.reflect.TypeToken;
import com.tty.Ari;
import com.tty.dto.event.CustomPluginReloadEvent;
import com.tty.enumType.FilePath;
import com.tty.lib.enum_type.LangType;
import com.tty.lib.tool.ComponentUtils;
import com.tty.lib.tool.FormatUtils;
import com.tty.lib.tool.PermissionUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class CustomChatFormantListener implements Listener {

    private Map<String, String> groupsPattern = new HashMap<>();

    public CustomChatFormantListener() {
        this.groupsPattern = this.set();
    }

    @EventHandler
    public void playerSendMessage(AsyncChatEvent event) {
        if (!this.isEnable()) return;
        event.renderer((source, sourceDisplayName, msg, viewer) -> {
            String format = this.getPattern(source);
            format = format.replace(LangType.SOURCEDISPLAYNAME.getType(), source.getName())
                    .replace(LangType.CHATMESSAGE.getType(), FormatUtils.componentToString(msg));
            return ComponentUtils.text(format);
        });
    }
    @EventHandler
    public void whenPluginReload(CustomPluginReloadEvent event) {
        if (!this.isEnable()) return;
        this.groupsPattern = this.set();
    }

    private Map<String, String> set() {
        return Ari.C_INSTANCE
                .getValue(
                        "chat.groups-pattern",
                        FilePath.FUNCTION_CONFIG,
                        new TypeToken<Map<String, String>>(){}.getType(),
                        new HashMap<>());
    }

    private String getPattern(Player player) {
        AtomicReference<String> s = new AtomicReference<>("");
        this.groupsPattern.forEach((k, v) -> {
            if (!s.get().isEmpty()) return;
            if (PermissionUtils.getPlayerIsInGroup(player, k)) {
                s.set(v);
            }
        });
        if (s.get().isEmpty()) {
            s.set(this.groupsPattern.get("_default_"));
        }
        return s.get();
    }

    private boolean isEnable() {
        return Ari.C_INSTANCE.getValue("chat.enable", FilePath.FUNCTION_CONFIG, Boolean.class, false);
    }
}
