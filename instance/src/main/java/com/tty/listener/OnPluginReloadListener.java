package com.tty.listener;

import com.tty.Ari;
import com.tty.command.function.CommandRtp;
import com.tty.dto.event.CustomPluginReloadEvent;
import com.tty.lib.tool.Log;
import com.tty.tool.ConfigUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OnPluginReloadListener implements Listener {

    @EventHandler
    public void pluginReload(CustomPluginReloadEvent event) {
        Ari.reloadAllConfig();
        Log.initLogger(Ari.instance.getLogger(), Ari.debug);
        if (Ari.debug) {
            Ari.instance.sqlInstance.reconnect();
        }
        Ari.instance.commandAlias.reloadAllAlias();
        CommandRtp.setRtpWorldConfig();
        event.getSender().sendMessage(ConfigUtils.t("function.reload.success"));
    }

}
