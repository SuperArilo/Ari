package com.tty.states;

import com.tty.Ari;
import com.tty.entity.state.State;
import com.tty.entity.state.teleport.PreEntityToEntityState;
import com.tty.enumType.FilePath;
import com.tty.lib.enum_type.LangType;
import com.tty.lib.tool.ComponentUtils;
import com.tty.lib.tool.Log;
import com.tty.tool.ConfigUtils;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PreTeleportStateMachine extends StateMachine {

    public PreTeleportStateMachine(long rate, long c, boolean isAsync, JavaPlugin javaPlugin) {
        super(rate, c, isAsync, javaPlugin);
    }

    @Override
    public boolean condition(State state) {
        if (!(state instanceof PreEntityToEntityState toPlayerState)) return false;

        Entity owner = toPlayerState.getOwner();
        Entity target = toPlayerState.getTarget();

        // 基本合法性检查
        if (target instanceof Player p && !p.isOnline()) return false;

        if (target == null) {
            owner.sendMessage(ConfigUtils.t("teleport.unable-player"));
            return false;
        }

        if (target.getName().equals(owner.getName())) {
            owner.sendMessage(ConfigUtils.t("function.public.fail"));
            return false;
        }

        Log.debug("checking player " + owner.getName() + " -> " + target.getName() + " request");
        return true;
    }

    @Override
    public void abortAddState(State state) {
        if (!(state instanceof PreEntityToEntityState toPlayerState)) return;
        Entity owner = toPlayerState.getOwner();
        Entity target = toPlayerState.getTarget();
        owner.sendMessage(ConfigUtils.t("function.tpa.again", LangType.TPABESENDER.getType(), target.getName()));
    }

    @Override
    public void passAddState(State state) {
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
    public void onEarlyExit(State state) {
    }

    @Override
    public void onFinished(State state) {
        if (state instanceof PreEntityToEntityState preEntityToEntityState) {
            Log.debug("player " + preEntityToEntityState.getOwner().getName() + " send to " + preEntityToEntityState.getTarget().getName() + " teleport request expired");
        }

    }

    @Override
    protected boolean canAddState(State state) {
        if (!(state instanceof PreEntityToEntityState toPlayerState)) return false;
        Entity owner = toPlayerState.getOwner();
        Entity target = toPlayerState.getTarget();

        //判断当前实体是否在传送冷却中
        if (!Ari.instance.stateMachineManager.get(CoolDownStateMachine.class).getStates(owner).isEmpty()) {
            owner.sendMessage(ConfigUtils.t("teleport.cooling"));
            return false;
        }

        //判断当前发起玩家是否在传送状态中
        if (!Ari.instance.stateMachineManager.get(TeleportStateMachine.class).getStates(owner).isEmpty()) {
            owner.sendMessage(ConfigUtils.t("teleport.has-teleport"));
            return false;
        }

        // 检查是否存在相同目标的状态（排除自己）
        return this.getStates(owner).stream()
                .filter(i -> i instanceof PreEntityToEntityState)
                .filter(i -> i != state)
                .noneMatch(i -> ((PreEntityToEntityState)i).getTarget().equals(target));
    }
}
