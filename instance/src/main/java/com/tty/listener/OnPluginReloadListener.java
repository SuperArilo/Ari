package com.tty.listener;

import com.tty.Ari;
import com.tty.dto.event.CustomPluginReloadEvent;
import com.tty.enumType.FilePath;
import com.tty.lib.tool.Log;
import com.tty.tool.ConfigObjectUtils;
import com.tty.tool.TextTool;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OnPluginReloadListener implements Listener {

    @EventHandler
    public void pluginReload(CustomPluginReloadEvent event) {
        Ari.reloadAllConfig();
        Log.initLogger(Ari.instance.getLogger(), Ari.debug);
        if (Ari.debug) {
            Ari.instance.SQLInstance.reconnect();
        }
        Ari.instance.commandAlias.reloadAllAlias();
        ConfigObjectUtils.setRtpWorldConfig();
        Object entity = event.getEntity();
        if (entity instanceof CommandSender commandSender) {
            commandSender.sendMessage(TextTool.setHEXColorText("function.reload.success", FilePath.Lang));
        }
    }

}
