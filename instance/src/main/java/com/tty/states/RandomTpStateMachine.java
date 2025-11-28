package com.tty.states;

import com.google.gson.reflect.TypeToken;
import com.tty.Ari;
import com.tty.dto.rtp.RtpConfig;
import com.tty.entity.state.State;
import com.tty.entity.state.teleport.EntityToLocationState;
import com.tty.entity.state.teleport.RandomTpState;
import com.tty.enumType.FilePath;
import com.tty.enumType.TeleportType;
import com.tty.lib.enum_type.LangType;
import com.tty.lib.tool.ComponentUtils;
import com.tty.lib.tool.PublicFunctionUtils;
import com.tty.lib.tool.SearchSafeLocation;
import com.tty.tool.ConfigUtils;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RandomTpStateMachine extends StateMachine {

    private final SearchSafeLocation searchSafeLocation = new SearchSafeLocation(Ari.instance);

    public RandomTpStateMachine(long rate, long c, boolean isAsync, JavaPlugin javaPlugin) {
        super(rate, c, isAsync, javaPlugin);
    }

    @Override
    protected boolean canAddState(State state) {
        Entity owner = state.getOwner();
        if (state instanceof RandomTpState s) {
            RtpConfig rtpConfig = this.rtpConfig(s.getWorld().getName());
            if (rtpConfig != null && Ari.C_INSTANCE.getValue("rtp.enable", FilePath.FunctionConfig, Boolean.class, false)) {
                if (!rtpConfig.isEnable()) {
                    s.getOwner().sendMessage(ConfigUtils.t("function.rtp.world-disable"));
                    return false;
                }

                StateMachineManager manager = Ari.instance.stateMachineManager;

                //判断当前实体是否在传送冷却中
                if (!manager.get(CoolDownStateMachine.class).getStates(owner).isEmpty()) {
                    owner.sendMessage(ConfigUtils.t("teleport.cooling"));
                    return false;
                }

                //判断当前发起玩家是否在传送状态中
                if (!manager.get(TeleportStateMachine.class).getStates(owner).isEmpty() ||
                    !this.getStates(owner).isEmpty() ||
                    !manager.get(PreTeleportStateMachine.class).getStates(owner).isEmpty()) {
                    owner.sendMessage(ConfigUtils.t("teleport.has-teleport"));
                    return false;
                }

                return true;
            }
        }
        return false;
    }

    @Override
    protected void condition(State state) {
        if (!(state instanceof RandomTpState s)) {
            state.setOver(true);
            return;
        }
        Player owner = (Player) state.getOwner();
        if (!owner.isOnline()
                || owner.isSleeping()
                || owner.isDeeplySleeping()
                || owner.isFlying()
                || owner.isGliding()
                || owner.isInsideVehicle()
                || (!owner.isOp() && owner.getGameMode() != GameMode.SURVIVAL)) {
            s.setOver(true);
            return;
        }

        this.sendCountTitle(owner, s);
        this.search(s);
    }

    @Override
    protected void abortAddState(State state) {

    }

    @Override
    protected void passAddState(State state) {

    }

    @Override
    protected void onEarlyExit(State state) {
        Entity owner = state.getOwner();
        owner.clearTitle();
        owner.sendMessage(ConfigUtils.t("function.rtp.location-found"));
        if (state instanceof RandomTpState s) {
            Ari.instance.stateMachineManager
                    .get(TeleportStateMachine.class)
                    .addState(new EntityToLocationState(
                            owner,
                            TeleportType.getDelayTime(TeleportType.RTP),
                            s.getTrueLocation(),
                            TeleportType.RTP));
        }
    }

    @Override
    protected void onFinished(State state) {
        Entity owner = state.getOwner();
        owner.clearTitle();
        owner.sendMessage(ConfigUtils.t("function.rtp.search-failure"));
    }

    private void search(RandomTpState state) {
        World world = state.getWorld();
        RtpConfig rtpConfig = this.rtpConfig(world.getName());

        int x = (int) Math.min(PublicFunctionUtils.randomGenerator((int) rtpConfig.getMin(), (int) rtpConfig.getMax()), world.getWorldBorder().getMaxSize());
        int z = (int) Math.min(PublicFunctionUtils.randomGenerator((int) rtpConfig.getMin(), (int) rtpConfig.getMax()), world.getWorldBorder().getMaxSize());

        if (state.getTrueLocation() == null) {
            this.searchSafeLocation.search(world, x, z)
                    .orTimeout(1, TimeUnit.SECONDS)
                    .whenComplete((location, ex) -> {
                        if (location == null)  return;
                        state.setTrueLocation(location);
                    });
        }
    }

    private void sendCountTitle(Player player, RandomTpState state) {
        String sub = Ari.C_INSTANCE.getValue(
                "function.rtp.title-search-count",
                FilePath.Lang,
                String.class,
                "null");
        sub = sub.replace(LangType.RTPSEARCHCOUNT.getType(), String.valueOf(state.getMax_count() - state.getCount()));
        Title title = ComponentUtils.setPlayerTitle(
                Ari.C_INSTANCE.getValue("function.rtp.title-searching", FilePath.Lang, String.class, "null"),
                sub,
                0,
                1000L,
                1000L);
        if (player.isOnline()) player.showTitle(title);
    }

    private RtpConfig rtpConfig(String worldName) {
        Map<String, RtpConfig> value = Ari.C_INSTANCE.getValue("rtp.worlds", FilePath.FunctionConfig, new TypeToken<Map<String, RtpConfig>>() {}.getType(), null);
        return value.get(worldName);
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
