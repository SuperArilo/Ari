package com.tty.states;

import com.tty.Ari;
import com.tty.entity.state.State;
import com.tty.entity.state.teleport.PlayerToPlayerState;
import com.tty.enumType.FilePath;
import com.tty.lib.enum_type.LangType;
import com.tty.lib.tool.ComponentUtils;
import com.tty.lib.tool.Log;
import com.tty.tool.ConfigUtils;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PreTeleportStateMachine extends StateMachine {

    public PreTeleportStateMachine(long rate, long c, boolean isAsync, JavaPlugin javaPlugin) {
        super(rate, c, isAsync, javaPlugin);
    }

    @Override
    public boolean condition(State state) {
        if (!(state instanceof PlayerToPlayerState toPlayerState)) return false;

        Player owner = toPlayerState.getOwner();
        Player target = toPlayerState.getTarget();

        // 基本合法性检查
        if (!owner.isOnline()) return false;

        if (target == null) {
            owner.sendMessage(ConfigUtils.t("teleport.unable-player"));
            return false;
        }

        if (target.getName().equals(owner.getName())) {
            owner.sendMessage(ConfigUtils.t("function.public.fail"));
            return false;
        }

        if (!Ari.instance.teleportingStateMachine.getStates(owner).isEmpty()) {
            owner.sendMessage(ConfigUtils.t("teleport.has-teleport"));
            return false;
        }
        // 检查是否存在相同目标的状态（排除自己）
        boolean hasDuplicate = this.getStates(owner).stream()
                .filter(i -> i instanceof PlayerToPlayerState)
                .filter(i -> i != state)
                .anyMatch(i -> ((PlayerToPlayerState)i).getTarget().equals(target));

        if (hasDuplicate) {
            owner.sendMessage(ConfigUtils.t("function.tpa.again", LangType.TPABESENDER.getType(), target.getName()));
            return false;
        }

        // 日志
        Log.debug("PreTeleportStateMachine: checking player " + owner.getName() + " -> " + target.getName() + " request");
        return true;
    }

    @Override
    public void onFail(State state) {
    }

    @Override
    public void onSuccess(State state) {
    }

    @Override
    public void addState(State state) {
        if (!this.condition(state)) return;

        super.addState(state);

        // 消息只在第一次添加状态时发送
        if (state instanceof PlayerToPlayerState toPlayerState) {
            Player owner = toPlayerState.getOwner();
            Player target = toPlayerState.getTarget();

            owner.sendMessage(ConfigUtils.t("function.tpa.send-message"));

            String message = Ari.C_INSTANCE.getValue(
                    "function.tpa." + (toPlayerState.getCommand().equals("tpa") ? "to-message" : "here-message"),
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
}
