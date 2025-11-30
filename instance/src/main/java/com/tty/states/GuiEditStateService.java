package com.tty.states;

import com.tty.Ari;
import com.tty.enumType.FilePath;
import com.tty.dto.state.PlayerEditGuiState;
import com.tty.lib.Log;
import com.tty.lib.dto.State;
import com.tty.lib.services.StateService;
import com.tty.lib.tool.ComponentUtils;
import com.tty.tool.ConfigUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class GuiEditStateService extends StateService {

    public GuiEditStateService(long rate, long c, boolean isAsync, JavaPlugin javaPlugin) {
        super(rate, c, isAsync, javaPlugin);
    }

    @Override
    protected boolean canAddState(State state) {
        return this.hasState(state.getOwner());
    }

    @Override
    protected void loopExecution(State state) {
        if (!(state instanceof PlayerEditGuiState s)) {
            state.setOver(true);
            return;
        }
        Player owner = (Player) s.getOwner();
        if (!owner.isOnline()) {
            state.setOver(true);
            return;
        }
        state.setPending(false);
        Log.debug("checking player %s edit gui %s. type %s", owner.getName(), s.getHolder().getType(), s.getFunctionType());
    }

    @Override
    protected void abortAddState(State state) {

    }

    @Override
    protected void passAddState(State state) {
        Player owner = (Player) state.getOwner();
        int i = Ari.instance.getConfig().getInt("server.gui-edit-timeout", 10);
        owner.showTitle(
                ComponentUtils.setPlayerTitle(
                        Ari.C_INSTANCE.getValue("base.on-edit.title", FilePath.LANG),
                        Ari.C_INSTANCE.getValue("base.on-edit.sub-title", FilePath.LANG),
                        1000,
                        i * 1000L,
                        1000));
    }

    @Override
    protected void onEarlyExit(State state) {
        Player owner = (Player) state.getOwner();
        Log.debug("player %s edit status finish.", owner.getName());
    }

    @Override
    protected void onFinished(State state) {
        Player owner = (Player) state.getOwner();
        owner.sendMessage(ConfigUtils.t("base.on-edit.timeout-cancel"));
        Log.debug("player %s edit status timeout.", owner.getName());
    }
}
