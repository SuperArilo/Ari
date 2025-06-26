package com.tty.listener.player;

import com.tty.Ari;
import com.tty.dto.BaseAction;
import com.tty.dto.action.PlayerRide;
import com.tty.dto.action.PlayerSit;
import com.tty.enumType.FilePath;
import com.tty.tool.ConfigObjectUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.Material;
import org.bukkit.event.server.PluginDisableEvent;

public class PlayerActionListener implements Listener {

    @EventHandler
    public void onPlayInteract(PlayerInteractEvent event) {
        //未开启
        if (!this.isEnable()) return;
        //动作不匹配
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        //右手不为空
        if (event.getItem() != null) return;
        //判断被点击的方块是否存在
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;
        //判断发起动作的玩家是否有骑乘实体
        Player player = event.getPlayer();
        if (player.getVehicle() != null) return;

        PlayerSit sit = new PlayerSit(player, clickedBlock);
        if (!sit.check()) return;
        sit.action();
    }
    //玩家相互骑乘
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        //未开启
        if (!this.isEnable()) return;
        //发起骑乘的玩家
        Player player = event.getPlayer();
        //右手必须为空才能骑乘
        if (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) return;
        //被点击的实体必须属于玩家
        if(!(event.getRightClicked() instanceof Player clickedPlayer)) return;

        PlayerRide ride = new PlayerRide(player, clickedPlayer);
        if (!ride.check()) return;
        ride.action();
    }
    @EventHandler
    public void onServerShutdown(PluginDisableEvent event) {
        if (!this.isEnable()) return;
        if (event.getPlugin() instanceof Ari) {
            BaseAction.PLAYER_ACTION_MAP.forEach((k, v) -> v.cancel());
        }
    }
    private boolean isEnable() {
        return ConfigObjectUtils.getValue("action.sit.enable", FilePath.FunctionConfig.getName(), Boolean.class, false);
    }
}
