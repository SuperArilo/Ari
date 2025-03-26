package ari.superarilo.listener.player;

import ari.superarilo.Ari;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.function.PlayerManager;
import ari.superarilo.tool.TextTool;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class OnPlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            if(Ari.instance.getConfig().getBoolean("server.message.on-first-login")) {
                event.joinMessage(TextTool.setHEXColorText("server.message.on-first-login", FilePath.Lang, player));
            }
            PlayerManager.build(player).createInstance(player.getUniqueId().toString());
        } else {
            if(Ari.instance.getConfig().getBoolean("server.message.on-login")) {
                event.joinMessage(TextTool.setHEXColorText("server.message.on-login", FilePath.Lang, player));
            }
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if(Ari.instance.getConfig().getBoolean("server.message.on-leave")) {
            event.quitMessage(TextTool.setHEXColorText("server.message.on-leave", FilePath.Lang, player));
        }
    }
}
