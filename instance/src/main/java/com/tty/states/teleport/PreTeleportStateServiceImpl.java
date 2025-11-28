package com.tty.states.teleport;

import com.tty.Ari;
import com.tty.lib.dto.State;
import com.tty.entity.state.teleport.PreEntityToEntityState;
import com.tty.enumType.FilePath;
import com.tty.lib.enum_type.LangType;
import com.tty.lib.services.impl.StateServiceImpl;
import com.tty.lib.tool.ComponentUtils;
import com.tty.lib.tool.Log;
import com.tty.states.CoolDownStateServiceImpl;
import com.tty.tool.ConfigUtils;
import com.tty.tool.StateMachineManager;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PreTeleportStateServiceImpl extends StateServiceImpl {

    public PreTeleportStateServiceImpl(long rate, long c, boolean isAsync, JavaPlugin javaPlugin) {
        super(rate, c, isAsync, javaPlugin);
    }

    @Override
    protected void condition(State state) {
        if (!(state instanceof PreEntityToEntityState toPlayerState)) {
            state.setOver(true);
            return;
        }

        Entity owner = toPlayerState.getOwner();
        Entity target = toPlayerState.getTarget();

        // 基本合法性检查
        if (target instanceof Player p && !p.isOnline()) {
            state.setOver(true);
            return;
        }

        if (target == null) {
            owner.sendMessage(ConfigUtils.t("teleport.unable-player"));
            state.setOver(true);
            return;
        }

        if (target.getName().equals(owner.getName())) {
            owner.sendMessage(ConfigUtils.t("function.public.fail"));
            state.setOver(true);
            return;
        }

        Log.debug("checking player " + owner.getName() + " -> " + target.getName() + " request");
    }

    @Override
    protected void abortAddState(State state) {
    }

    @Override
    protected void passAddState(State state) {
        if (state instanceof PreEntityToEntityState toEntityState) {
            Entity owner = toEntityState.getOwner();
            Entity target = toEntityState.getTarget();

            owner.sendMessage(ConfigUtils.t("function.tpa.send-message"));

            String message = Ari.C_INSTANCE.getValue(
                    "function.tpa." + (toEntityState.getCommand().equals("tpa") ? "to-message" : "here-message"),
                    FilePath.Lang
            );

            target.sendMessage(
                    ComponentUtils.text(message.replace(LangType.TPASENDER.getType(), owner.getName()))
                            .appendNewline()
                            .append(ComponentUtils.setClickEventText(
                                    Ari.C_INSTANCE.getValue("function.public.agree", FilePath.Lang),
                                    ClickEvent.Action.RUN_COMMAND,
                                    "/ari tpaaccept " + owner.getName()))
                            .append(ConfigUtils.t("function.public.center"))
                            .append(ComponentUtils.setClickEventText(
                                    Ari.C_INSTANCE.getValue("function.public.refuse", FilePath.Lang),
                                    ClickEvent.Action.RUN_COMMAND,
                                    "/ari tparefuse " + owner.getName()))
            );
        }
    }

    @Override
    protected void onEarlyExit(State state) {
    }

    @Override
    protected void onFinished(State state) {
        if (state instanceof PreEntityToEntityState preEntityToEntityState) {
            Log.debug("player " + preEntityToEntityState.getOwner().getName() + " send to " + preEntityToEntityState.getTarget().getName() + " teleport request expired");
        }

    }

    @Override
    protected boolean canAddState(State state) {
        if (!(state instanceof PreEntityToEntityState toPlayerState)) return false;
        Entity owner = toPlayerState.getOwner();
        Entity target = toPlayerState.getTarget();
        StateMachineManager manager = Ari.instance.stateMachineManager;
        //判断当前实体是否在传送冷却中
        if (!manager.get(CoolDownStateServiceImpl.class).getStates(owner).isEmpty()) {
            owner.sendMessage(ConfigUtils.t("teleport.cooling"));
            return false;
        }

        //检查是否已经发过请求了
        if (!this.getStates(owner).isEmpty()) {
            owner.sendMessage(ConfigUtils.t("function.tpa.again", LangType.TPABESENDER.getType(), target.getName()));
            return false;
        }

        //判断当前发起玩家是否在传送状态中或者是否正在进行 rtp 传送
        if (!manager.get(TeleportStateServiceImpl.class).getStates(owner).isEmpty() ||
                !manager.get(RandomTpStateServiceImpl.class).getStates(owner).isEmpty()) {
            owner.sendMessage(ConfigUtils.t("teleport.has-teleport"));
            return false;
        }

        return true;
    }
}
