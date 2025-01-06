package ari.superarilo.tool;

import ari.superarilo.Ari;
import ari.superarilo.enumType.FilePath;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class TeleportThread {

    private final ConfigFiles config = Ari.instance.getConfigFiles();

    private final Type type;
    private final Player player;
    private Player targetPlayer;
    private final Location initialLocation;
    private final Location targetLocation;
    private final double initialHealth;
    public enum Type {
        POINT,
        PLAYER,
        BACK,
        DBACK,
        RANDOM;
    }

    //定点传送
    public TeleportThread(Player player, Location targetLocation, Type type){
        this.player = player;
        this.targetLocation = targetLocation;
        this.type = type;
        this.initialLocation = player.getLocation();
        this.initialHealth = player.getHealth();
    }

    //玩家之间传送
    public TeleportThread(Player player, Player targetPlayer, Type type){
        this.player = player;
        this.targetPlayer = targetPlayer;
        this.targetLocation = null;
        this.type = type;
        this.initialLocation = player.getLocation();
        this.initialHealth = player.getHealth();
    }

    //开始传送
    public void teleport() {
        //设置传送冷却时间
        final int[] timerIndex;
        if (this.player.isOp()) {
            timerIndex = new int[]{1};
        } else {
            timerIndex = new int[]{Ari.instance.getConfig().getInt("Teleport.delay", 1)};
            this.player.sendMessage(TextTool.setHEXColorText(this.config.getValue("teleport.ing", FilePath.Lang, String.class)));
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
                threadPlayer.sendMessage(TextTool.setHEXColorText(this.config.getValue("teleport.break", FilePath.Lang, String.class)));
                return;
            }
            timerIndex[0]--;
            Log.debug(Level.INFO, "start time: " + timerIndex[0] + "s");
            //传送时间到达
            if (timerIndex[0] == 0) {
                t.cancel();
                Log.debug(Level.INFO, "传送执行");
                switch (this.type) {
                    case POINT:
                        Bukkit.getRegionScheduler().run(Ari.instance, this.targetLocation, i -> {
                           threadPlayer.teleportAsync(this.targetLocation);
                           threadPlayer.playEffect(this.targetLocation, Effect.ANVIL_BREAK, null);
                           threadPlayer.sendMessage(TextTool.setHEXColorText(this.config.getValue("teleport.success", FilePath.Lang, String.class)));
                        });
                        break;
                    case PLAYER:
                        Bukkit.getRegionScheduler().run(Ari.instance, threadPlayer.getLocation(), (i) -> {
                            threadPlayer.teleportAsync(this.targetPlayer.getLocation());
                            threadPlayer.playEffect(this.targetPlayer.getLocation(), Effect.ANVIL_USE, null);
                            threadPlayer.sendMessage(TextTool.setHEXColorText(this.config.getValue("teleport.success", FilePath.Lang, String.class)));
                        });
                        break;
                    case BACK:
                        break;
                    case DBACK:
                        break;
                }
            }
        }, 0, 1L, TimeUnit.SECONDS);
    }

    //设置之前存在的位置
    private void setBeforeLocation(Location location) {

    }

    //检查是否受伤
    private boolean hasLostHealth(Player p) {
        return p.getHealth() < initialHealth;
    }

    //检查是否移动
    private boolean hasMoved(Player p) {
        Location currentLocation = p.getLocation();
        return makePositive(initialLocation.getX() - currentLocation.getX()) + makePositive(initialLocation.getY() - currentLocation.getY()) + makePositive(initialLocation.getZ() - currentLocation.getZ()) > 0.1;
    }

    private double makePositive(double d) {
        if (d < 0) {
            d = d * -1D;
        }
        return d;
    }
}
