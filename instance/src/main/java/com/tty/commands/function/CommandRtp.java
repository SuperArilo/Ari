package com.tty.commands.function;

import com.google.gson.reflect.TypeToken;
import com.tty.Ari;
import com.tty.commands.check.TeleportCheck;
import com.tty.commands.rtp;
import com.tty.dto.rtp.RtpConfig;
import com.tty.enumType.FilePath;
import com.tty.function.Teleport;
import com.tty.lib.Lib;
import com.tty.lib.enum_type.LangType;
import com.tty.lib.task.CancellableTask;
import com.tty.lib.tool.ComponentUtils;
import com.tty.lib.tool.Log;
import com.tty.lib.tool.RandomGeneratorUtils;
import com.tty.lib.tool.SearchSafeLocation;
import com.tty.tool.ConfigUtils;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;


public class CommandRtp {

    private final Player sender;
    private final int initCount = Ari.C_INSTANCE.getValue("rtp.search-count", FilePath.FunctionConfig, Integer.class, 10);
    private int count = 0;
    private boolean isDone = false;
    private final World world;
    private final RtpConfig config;

    private CancellableTask task;

    public CommandRtp(Player sender) {
        this.sender = sender;
        this.world = sender.getWorld();

        Map<String, RtpConfig> value = Ari.C_INSTANCE.getValue(
                "rtp.worlds",
                FilePath.FunctionConfig,
                new TypeToken<Map<String, RtpConfig>>() {
                }.getType(),
                null);
        this.config = value.get(this.world.getName());
    }

    public void rtp() {
        if (this.config == null || !Ari.C_INSTANCE.getValue(
                "rtp.enable",
                FilePath.FunctionConfig,
                Boolean.class,
                false)) return;

        if (!this.config.isEnable()) {
            this.sender.sendMessage(ConfigUtils.t("function.rtp.world-disable"));
            return;
        }

        if (!(sender instanceof Player player)) return;

        if ((player.isSleeping() || player.isDeeplySleeping() || player.isFlying()) && !this.sender.isOp()) return;

        if (!TeleportCheck.preCheckStatus(
                player,
                null,
                Ari.C_INSTANCE.getValue("rtp.delay", FilePath.FunctionConfig, Integer.class, 3) * 20)
        ) return;

        this.task = Lib.Scheduler.runAsyncAtFixedRate(Ari.instance, i -> {
            if (this.count >= this.initCount) {
                this.sender.clearTitle();
                this.sender.sendMessage(ConfigUtils.t("function.rtp.search-failure"));
                i.cancel();
                return;
            }
            if (this.isDone) {
                i.cancel();
                return;
            }
            this.count++;
            this.search();
        }, 1L, 20L);
    }

    public void cancelRtp() {
        this.isDone = true;
        if(this.task == null) return;
        this.task.cancel();
        rtp.RTP_LIST.remove(this.sender);
        this.sender.clearTitle();
        this.sender.sendMessage(ConfigUtils.t("function.rtp.rtp-cancel"));
    }

    private void search() {
        if(this.isDone) return;
        this.sendCountTitle();

        int x = (int) Math.min(RandomGeneratorUtils.get((int) this.config.getMin(), (int) this.config.getMax()), this.world.getWorldBorder().getMaxSize());
        int z = (int) Math.min(RandomGeneratorUtils.get((int) this.config.getMin(), (int) this.config.getMax()), this.world.getWorldBorder().getMaxSize());

        SearchSafeLocation searchSafeLocation = new SearchSafeLocation(Ari.instance, this.world, x, z);

        searchSafeLocation.search().thenAccept(location -> {
            if (location == null) {
                return;
            }
            this.isDone = true;
            this.count = 0;
            this.sender.clearTitle();
            Lib.Scheduler.runAtEntity(Ari.instance, this.sender, b -> Teleport.create(this.sender, location, 0).teleport(), () -> Log.error("teleport error on " + this.sender.getName()));
        }).exceptionally(i -> {
            Log.error("search error", i);
            return null;
        });
    }

    private void sendCountTitle() {
        String sub = Ari.C_INSTANCE.getValue(
                "function.rtp.title-search-count",
                FilePath.Lang,
                String.class,
                "null");
        sub = sub.replace(LangType.RTPSEARCHCOUNT.getType(), String.valueOf(this.initCount - this.count));
        Title title = ComponentUtils.setPlayerTitle(
                Ari.C_INSTANCE.getValue("function.rtp.title-searching", FilePath.Lang, String.class, "null"),
                sub,
                0,
                1000L,
                1000L);
        this.sender.showTitle(title);
    }

    private static Map<String, Object> createWorldRtp() {
        Map<String, Object> map = new HashMap<>();
        map.put("enable", true);
        map.put("min", 300);
        map.put("max", 1500);
        return map;
    }

    public static void setRtpWorldConfig() {

        Map<String, Object> value = Ari.C_INSTANCE.getValue(
                "rtp.worlds",
                FilePath.FunctionConfig,
                new TypeToken<Map<String, Object>>(){}.getType(),
                null);

        if (value == null) {
            value = new HashMap<>();
            for (World world : Bukkit.getWorlds()) {
                value.put(world.getName(), createWorldRtp());
            }
        } else {
            for (World world : Bukkit.getWorlds()) {
                if (value.containsKey(world.getName())) continue;
                value.put(world.getName(), createWorldRtp());
            }
        }
        Ari.C_INSTANCE.setValue(Ari.instance,"rtp.worlds", FilePath.FunctionConfig, value);
    }

}
