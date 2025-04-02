package ari.superarilo.listener.skipSleep;

import ari.superarilo.Ari;
import ari.superarilo.enumType.TimePeriod;
import ari.superarilo.function.TimeManager;
import ari.superarilo.tool.Log;
import ari.superarilo.tool.TextTool;
import io.papermc.paper.event.player.PlayerDeepSleepEvent;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class PlayerSkipNight implements Listener {

    private TimeManager timeManager;
    private ScheduledTask titleTask;

    @EventHandler
    public void deepSleep(PlayerDeepSleepEvent event) {
        if (!this.getSkipNightEnable()) return;
        event.setCancelled(true);
        Bukkit.getGlobalRegionScheduler().run(Ari.instance, i -> {
            World world = event.getPlayer().getWorld();
            boolean pc = this.playerCondition(world);

            //当服务器人数为1人时候或者所有人睡觉
            if(pc) {
                this.cancelTimeManager();
                this.cancelTitleTask();
                world.setTime(TimePeriod.WAKEUP.getEnd());
                world.setStorm(false);
                world.setThundering(false);
            } else {
                if(this.timeManager == null) {
                    this.createTask(world);
                } else {
                    this.timeManager.setAddTick(this.timeManager.getAddTick() + this.getTickIncrement());
                }
            }
        });
    }

    @EventHandler
    public void playerGetup(PlayerBedLeaveEvent event) {
        if (!this.getSkipNightEnable()) return;
        Bukkit.getGlobalRegionScheduler().run(Ari.instance, i -> {
            World world = event.getPlayer().getWorld();
            if(world.getTime() >= TimePeriod.WAKEUP.getEnd()) {
                this.cancelTimeManager();
                this.cancelTitleTask();
                return;
            }
            if(this.timeManager != null) {
                if(this.getSleepPlayers(world) == 0) {
                    this.cancelTimeManager();
                    this.cancelTitleTask();
                } else {
                    this.timeManager.setAddTick(this.timeManager.getAddTick() - this.getTickIncrement());
                }
            } else {
                this.cancelTitleTask();
                if (this.getSleepPlayers(world) == 0) return;
                if(!this.playerCondition(world)) {
                    this.createTask(world);
                }
            }
        });
    }

    @EventHandler
    public void whenServerShutDown(PluginDisableEvent event) {
        Log.debug("server is shutdown now");
        this.cancelTitleTask();
        this.cancelTimeManager();
        Log.debug("cancel all skip night tasks");
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
        if(this.titleTask != null) {
            this.titleTask.cancel();
            this.titleTask = null;
        }
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
        this.timeManager.timeAutomaticallyPasses(s -> {
            if (this.titleTask != null) return;
            this.titleTask = Bukkit.getAsyncScheduler()
                    .runDelayed(Ari.instance, i -> world.getPlayers().forEach(instance -> Bukkit.getGlobalRegionScheduler().run(Ari.instance, task -> {
                        if (world.getPlayers().stream().filter(LivingEntity::isSleeping).count() == world.getPlayerCount()) {
                            this.cancelTimeManager();
                            this.cancelTitleTask();
                            return;
                        }
                        if (!instance.isSleeping() || this.timeManager == null) return;
                        Title title = Title.title(
                                TextTool.setHEXColorText(this.timeManager.tickToTime(s)),
                                TextTool.setHEXColorText(""),
                                Title.Times.times(Duration.ZERO, Duration.ofSeconds(1), Duration.ofSeconds(1))
                        );
                        instance.showTitle(title);
                        this.titleTask.cancel();
                        this.titleTask = null;
                    })), 1, TimeUnit.SECONDS);
            if (s >= TimePeriod.WAKEUP.getEnd()) {
                world.setStorm(false);
                world.setThundering(false);
            }
        });
    }

    private long getSleepPlayers(World world) {
        return world.getPlayers().stream().filter(LivingEntity::isSleeping).count();
    }

    private long getTickIncrement() {
        return Ari.instance.getConfig().getLong("server.skip-night.tick-increment", 5L);
    }

    private boolean getSkipNightEnable() {
        return Ari.instance.getConfig().getBoolean("server.skip-night.enable", false);
    }
}
