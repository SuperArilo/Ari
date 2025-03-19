package ari.superarilo.function.impl;

import ari.superarilo.Ari;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.enumType.TeleportType;
import ari.superarilo.function.TeleportThread;
import ari.superarilo.tool.Log;
import ari.superarilo.tool.TextTool;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import net.kyori.adventure.sound.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class TeleportThreadImpl implements TeleportThread {

    private final TextComponent teleportSuccess = TextTool.setHEXColorText("teleport.success", FilePath.Lang);

    //保存的玩家上一个传送位置
    public static final Map<UUID, Location> lastLocation = new HashMap<>();
    //被传送玩家
    private final Player player;
    //目标玩家
    private final Player targetPlayer;
    //目标地址
    private final Location targetLocation;
    //玩家初始位置
    private final Location initLocation;
    //玩家开始传送时的生命值
    private final double initHealth;
    //传送类型
    private final TeleportType type;

    public TeleportThreadImpl(Player player, Location location) {
        this.player = player;
        this.initLocation = player.getLocation();
        this.initHealth = player.getHealth();
        this.targetLocation = location;
        this.type = TeleportType.POINT;

        this.targetPlayer = null;
    }
    public TeleportThreadImpl(Player player, Player targetPlayer) {
        this.player = player;
        this.targetPlayer = targetPlayer;
        this.initLocation = player.getLocation();
        this.initHealth = player.getHealth();
        this.type = TeleportType.PLAYER;

        this.targetLocation = null;
    }

    @Override
    public void teleport(int delay) {
        //设置传送冷却时间
        AtomicInteger timerIndex = new AtomicInteger();
        if (this.player.isOp()) {
            timerIndex.set(1);
        } else {
            timerIndex.set(delay);
            this.player.sendMessage(TextTool.setHEXColorText("teleport.ing", FilePath.Lang));
        }
        Bukkit.getAsyncScheduler().runAtFixedRate(Ari.instance, t -> {
            //在任务里获取现在玩家的状态
            Player threadPlayer = Ari.instance.getServer().getPlayer(this.player.getUniqueId());
            if (threadPlayer == null) {
                t.cancel();
                return;
            }
            //判断玩家是否在传送过程中移动或者受伤
            if (this.hasMoved(threadPlayer) || this.hasLostHealth(threadPlayer)) {
                t.cancel();
                threadPlayer.sendMessage(TextTool.setHEXColorText("teleport.break", FilePath.Lang));
                return;
            }
            timerIndex.decrementAndGet();
            Log.debug(Level.INFO, "start time: " + timerIndex.get() + "s");
            //传送时间到达
            if (timerIndex.get() == 0) {
                t.cancel();
                switch (this.type) {
                    case POINT -> {
                        if(this.targetLocation == null) return;
                        threadPlayer.getScheduler().run(Ari.instance, i -> {
                            threadPlayer.teleportAsync(this.targetLocation);
                            threadPlayer.playSound(Sound.sound(org.bukkit.Sound.ENTITY_ENDER_EYE_DEATH, SoundCategory.PLAYERS, 1.0f, 1.0f));
                            threadPlayer.sendMessage(this.teleportSuccess);
                        }, () -> Log.error("teleport error! type: " + TeleportType.POINT.name()));
                    }
                    case PLAYER -> threadPlayer.getScheduler().run(Ari.instance, i -> {
                        threadPlayer.teleportAsync(this.targetPlayer.getLocation());
                        threadPlayer.playSound(Sound.sound(org.bukkit.Sound.ENTITY_ENDER_EYE_DEATH, SoundCategory.PLAYERS, 1.0f, 1.0f));
                        threadPlayer.sendMessage(this.teleportSuccess);
                    }, () -> Log.error("teleport error! type: " + TeleportType.PLAYER.name()));
                }
                Bukkit.getRegionScheduler().run(
                        Ari.instance, this.initLocation,
                        i -> Bukkit.getPluginManager().callEvent(
                                new PlayerTeleportEvent(
                                        threadPlayer,
                                        this.initLocation,
                                        this.targetLocation,
                                        PlayerTeleportEvent.TeleportCause.PLUGIN)));
            }
        }, 0, 1L, TimeUnit.SECONDS);
    }
    /**
     * 检查是否受伤
     * @param player 被检查的玩家
     */
    protected boolean hasLostHealth(Player player) {
        return player.getHealth() < this.initHealth;
    }
    /**
     * 检查是否移动
     * @param p 被检查的玩家
     */
    protected boolean hasMoved(Player p) {
        Location currentLocation = p.getLocation();
        return makePositive(this.initLocation.getX() - currentLocation.getX()) + makePositive(this.initLocation.getY() - currentLocation.getY()) + makePositive(this.initLocation.getZ() - currentLocation.getZ()) > 0.1;
    }

    protected double makePositive(double d) {
        if (d < 0) {
            d = d * -1D;
        }
        return d;
    }
}
