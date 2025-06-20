package com.tty.listener.player;

import com.google.gson.reflect.TypeToken;
import com.tty.dto.PlayerAction;
import com.tty.enumType.FilePath;
import com.tty.tool.ConfigObjectUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.util.BoundingBox;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerActionListener implements Listener {

    public static final Map<Player, PlayerAction> PLAYER_SIT_ACTION_MAP = new HashMap<>();

    @EventHandler
    public void onPlayInteract(PlayerInteractEvent event) {
        //未开启
        if (!this.isEnable()) return;
        //动作不匹配
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        //右手不为空
        if (event.getItem() != null) return;

        Player player = event.getPlayer();
        Block clicked = event.getClickedBlock();
        //已经存在 return
        if (PLAYER_SIT_ACTION_MAP.containsKey(player)) {
            event.setCancelled(true);
            return;
        }

        if (clicked == null || player.getVehicle() != null || !this.isCanSit(clicked)) return;

        PlayerAction playerAction = new PlayerAction(player);
        playerAction.sit(this.calculateSeatLocation(clicked, player));
        PLAYER_SIT_ACTION_MAP.put(player, playerAction);

    }

    //玩家相互骑乘
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        //未开启
        if (!this.isEnable()) return;
        //发起骑乘的玩家
        Player player = event.getPlayer();
        //被点击的玩家
        Entity clickedPlayer = event.getRightClicked();

        if(!(clickedPlayer instanceof Player)) return;

        //被点击的玩家有玩家骑乘
        if (!clickedPlayer.getPassengers().isEmpty()) return;

        //右手必须为空才能骑乘
        if (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) return;

        clickedPlayer.addPassenger(player);
    }
    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        //未开启
        if (!this.isEnable()) return;
        if (!event.isSneaking()) return;
        Player player = event.getPlayer();
        List<Entity> passengers = player.getPassengers();
        if (!passengers.isEmpty()) {
            player.eject();
            for (Entity passenger : passengers) {
                passenger.eject();
            }
        }
    }

    //取消玩家攻击
    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (!this.isEnable()) return;
        //被攻击者
        Entity entity = event.getEntity();
        //攻击者
        Entity damager = event.getDamager();

        List<Entity> passengers = damager.getPassengers();

        //当攻击玩家的乘客为 0 那就表示此玩家没有被骑乘
        if (passengers.isEmpty()) return;
        //如果此玩家有乘客，那就判断攻击对象是不是乘客, 如果是，取消攻击
        if (passengers.contains(entity)) {
            event.setCancelled(true);
        }
    }
    //取消玩家破坏
    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        if (!this.isEnable()) return;
        if (PLAYER_SIT_ACTION_MAP.containsKey(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    //取消玩家放置
    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
        if (!this.isEnable()) return;
        if (PLAYER_SIT_ACTION_MAP.containsKey(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    //当玩家取消坐
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (!this.isEnable()) return;
        Player player = event.getPlayer();
        PlayerAction playerAction = PLAYER_SIT_ACTION_MAP.get(player);
        if (playerAction == null) return;
        playerAction.cancel();
        PLAYER_SIT_ACTION_MAP.remove(player);
    }

    @EventHandler
    public void onVehicleExit(VehicleExitEvent event) {
        if (!this.isEnable()) return;
        if (event.getExited() instanceof Player player && PLAYER_SIT_ACTION_MAP.get(player) != null) {
            PLAYER_SIT_ACTION_MAP.get(player).cancel();
            PLAYER_SIT_ACTION_MAP.remove(player);
        }
    }

    private boolean isCanSit(Block block) {
        Collection<Entity> nearbyEntities = block.getLocation().getWorld().getNearbyEntities(BoundingBox.of(block));
        boolean b = nearbyEntities.stream().allMatch(entity -> entity instanceof ArmorStand);
        if (!b) {
            return false;
        }
        Material type = block.getType();
        String name = type.name();
        if (this.getDisableList().contains(name)) return false;
        return name.endsWith("_STAIRS") ||
                name.endsWith("_SLAB");
    }

    private Location calculateSeatLocation(Block clickedBlock, Player player) {
        Location loc = clickedBlock.getLocation().add(0.5, 0.3, 0.5);

        loc.add(0, 0.25, 0);
        if (clickedBlock.getBlockData() instanceof Stairs stairs) {
            loc.setYaw(this.getYawFromBlockFace(stairs.getFacing()));
        } else {
            loc.setYaw(player.getYaw());
        }

        return loc;
    }

    private float getYawFromBlockFace(BlockFace face) {
        return switch (face) {
            case EAST -> 90.0F;
            case SOUTH -> 180.0F;
            case WEST -> -90.0F;
            default -> 0.0F;
        };
    }

    private boolean isEnable() {
        return ConfigObjectUtils.getValue("action.sit.enable", FilePath.FunctionConfig.getName(), Boolean.class, false);
    }

    private List<String> getDisableList() {
        return ConfigObjectUtils.getValue("action.sit.disable-block", FilePath.FunctionConfig.getName(), new TypeToken<List<String>>(){}.getType(), List.of());
    }
}
