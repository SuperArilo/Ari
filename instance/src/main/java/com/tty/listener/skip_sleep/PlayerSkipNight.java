package com.tty.listener.skip_sleep;

import com.tty.Ari;
import com.tty.enumType.FilePath;
import com.tty.lib.enum_type.TimePeriod;
import com.tty.function.TimeManager;
import com.tty.lib.Lib;
import com.tty.lib.task.CancellableTask;
import com.tty.tool.ConfigObjectUtils;
import com.tty.tool.Log;
import com.tty.tool.TextTool;
import io.papermc.paper.event.player.PlayerDeepSleepEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class PlayerSkipNight implements Listener {

    private TimeManager timeManager;
    private CancellableTask titleScheduledTask;

    @EventHandler
    public void deepSleep(PlayerDeepSleepEvent event) {
        if (!this.getSkipNightEnable()) return;
        event.setCancelled(true);
        Lib.Scheduler.run(Ari.instance, i -> {
            World world = event.getPlayer().getWorld();
            boolean pc = this.playerCondition(world);
            //当服务器人数为1人时候或者所有人睡觉
            if(pc && world.getTime() < TimePeriod.WAKE_UP.getEnd()) {
                this.cancelTimeManager();
                world.setTime(TimePeriod.WAKE_UP.getEnd());
                world.setStorm(false);
                world.setThundering(false);
            } else {
                if(this.timeManager == null) {
                    this.createTask(world);
                } else {
                    this.timeManager.setAddTick(Math.min(this.timeManager.getAddTick() + this.getTickIncrement(), this.getMaxTickIncrement()));
                }
            }
        });
    }

    @EventHandler
    public void playerGetup(PlayerBedLeaveEvent event) {
        if (!this.getSkipNightEnable()) return;
        Lib.Scheduler.run(Ari.instance, i -> {
            World world = event.getPlayer().getWorld();
            if(world.getTime() >= TimePeriod.WAKE_UP.getEnd()) {
                this.cancelTimeManager();
                return;
            }
            if(this.timeManager != null) {
                if(this.getSleepPlayers(world) == 0) {
                    this.cancelTimeManager();
                } else {
                    this.timeManager.setAddTick(this.timeManager.getAddTick() - this.getTickIncrement());
                }
            } else {
                if (this.getSleepPlayers(world) == 0) return;
                if(!this.playerCondition(world)) {
                    this.createTask(world);
                }
            }
        });
    }

    @EventHandler
    public void whenServerShutDown(PluginDisableEvent event) {
        if (event.getPlugin().equals(Ari.instance)) {
            Log.debug("server is shutdown now");
            this.cancelTimeManager();
            Log.debug("cancel all skip night tasks");
            this.cancelTitleTask();
            Log.debug("canceled title task");
        }
    }

    /**
     * 取消和清空 timeManager
     */
    private void cancelTimeManager() {
        if (this.timeManager == null) return;
        this.timeManager.cancelTask();
        this.timeManager = null;
    }

    private void cancelTitleTask() {
        if (this.titleScheduledTask == null) return;
        this.titleScheduledTask.cancel();
        this.titleScheduledTask = null;
    }
    /**
     * 判断对应世界里玩家数量是否满足skip夜晚的条件
     * @param world 指定世界
     * @return true 代表满足原版跳过夜晚的条件，false 则是满足自定义skip夜晚的条件
     */
    private boolean playerCondition(@NotNull World world) {
        long sleepCount = world.getPlayers().stream().filter(LivingEntity::isSleeping).count();
        int playerCount = world.getPlayerCount();
        Log.debug("sleep player count: " + sleepCount);
        Log.debug("players: " + playerCount);
        return playerCount == 1 || playerCount == sleepCount;
    }

    private void createTask(@NotNull World world) {
        this.timeManager = TimeManager.build(world, 1L, this.getTickIncrement());
        List<Player> players = world.getPlayers();
        this.timeManager.timeAutomaticallyPasses(s -> {
            if (this.titleScheduledTask != null) return;
            this.titleScheduledTask = Lib.Scheduler.runAsyncDelayed(Ari.instance, i -> {
                players.forEach(instance -> {
                    if (!instance.isSleeping()) return;
                    long sleepPlayers = this.getSleepPlayers(world);
                    int playerCount = world.getPlayerCount();
                    if (sleepPlayers == playerCount) {
                        this.cancelTimeManager();
                        if (sleepPlayers == 1 && playerCount == 1) {
                            //当服务器存在两名玩家，其中一个玩家已经深度睡眠，但另一个玩家突然退出游戏。重新call一个深度睡眠Event
                            Bukkit.getPluginManager().callEvent(new PlayerDeepSleepEvent(instance));
                        }
                        return;
                    }
                    instance.getScheduler().run(Ari.instance, a -> {
                        if (this.timeManager != null) {
                            instance.showTitle(
                                    TextTool.setPlayerTitle(this.timeManager.tickToTime(s),
                                            ConfigObjectUtils.getValue("server.time.skip-to-night", FilePath.Lang.getName(), String.class),
                                            0L,
                                            1000L,
                                            1000L));
                        }
                    }, () -> {});
                });
                if (s >= TimePeriod.WAKE_UP.getEnd()) {
                    Lib.Scheduler.run(Ari.instance, p -> {
                        world.setStorm(false);
                        world.setThundering(false);
                    });
                }
                this.cancelTitleTask();
            }, 20L);
        });
    }

    private long getSleepPlayers(World world) {
        return world.getPlayers().stream().filter(LivingEntity::isSleeping).count();
    }

    private long getTickIncrement() {
        return Ari.instance.getConfig().getLong("server.skip-night.tick-increment", 5L);
    }

    private long getMaxTickIncrement() {
        return Ari.instance.getConfig().getLong("server.skip-night.max-tick-increment", 100L);
    }

    private boolean getSkipNightEnable() {
        return Ari.instance.getConfig().getBoolean("server.skip-night.enable", false);
    }
}
