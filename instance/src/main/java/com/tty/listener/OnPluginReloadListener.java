package com.tty.listener;

import com.tty.Ari;
import com.tty.commands.function.CommandRtp;
import com.tty.dto.event.CustomPluginReloadEvent;
import com.tty.lib.tool.Log;
import com.tty.tool.ConfigUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OnPluginReloadListener implements Listener {

    @EventHandler
    public void pluginReload(CustomPluginReloadEvent event) {
        Ari.reloadAllConfig();
        Log.initLogger(Ari.instance.getLogger(), Ari.DEBUG);
        if (Ari.DEBUG) {
            Ari.instance.sqlInstance.reconnect();
        }
        CommandRtp.setRtpWorldConfig();
        Ari.instance.playerSave.reload();
        event.getSender().sendMessage(ConfigUtils.t("function.reload.success"));
    }

}
