package ari.superarilo.listener.player;

import ari.superarilo.Ari;
import ari.superarilo.entity.sql.ServerPlayer;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.function.PlayerManager;
import ari.superarilo.tool.Log;
import ari.superarilo.tool.TextTool;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;


public class OnPlayerListener implements Listener {
    /**
     * 记录玩家进入服务器的时间戳
     */
    private static final Map<UUID, Long> playerLoginTimes = new HashMap<>();
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
        playerLoginTimes.put(player.getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if(Ari.instance.getConfig().getBoolean("server.message.on-leave")) {
            event.quitMessage(TextTool.setHEXColorText("server.message.on-leave", FilePath.Lang, player));
        }

        PlayerManager manager = PlayerManager.build(player);
        Bukkit.getAsyncScheduler().runNow(Ari.instance, i -> {
            try {
                ServerPlayer serverPlayer = manager.asyncGetInstance(player.getUniqueId().toString()).get();
                long l = System.currentTimeMillis();
                if(serverPlayer == null) {
                    Log.warning("player: " + player.getUniqueId() + " data is null, exiting");
                    return;
                }
                serverPlayer.setLastLoginOffTime(l);
                serverPlayer.setTotalOnlineTime(serverPlayer.getTotalOnlineTime() + l - playerLoginTimes.get(player.getUniqueId()));
                manager.modify(serverPlayer);
                playerLoginTimes.remove(player.getUniqueId());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
