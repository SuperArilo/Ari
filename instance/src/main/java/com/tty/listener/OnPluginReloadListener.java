package com.tty.listener;

import com.tty.Ari;
import com.tty.dto.event.CustomPluginReloadEvent;
import com.tty.enumType.FilePath;
import com.tty.lib.tool.ComponentUtils;
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
        ConfigUtils.setRtpWorldConfig();
        event.getSender().sendMessage(ComponentUtils.text(ConfigUtils.getValue("function.reload.success", FilePath.Lang)));
    }

}
