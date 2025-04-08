package ari.superarilo.listener;

import ari.superarilo.Ari;
import ari.superarilo.dto.event.CustomPluginReloadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OnPluginReloadListener implements Listener {
    @EventHandler
    public void onReload(CustomPluginReloadEvent event) {
        Ari.instance.playerTabManager.reload();
    }
}
