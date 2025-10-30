package com.tty.dto.action;

import com.google.gson.reflect.TypeToken;
import com.tty.Ari;
import com.tty.dto.BaseAction;
import com.tty.enumType.FilePath;
import com.tty.lib.Lib;
import com.tty.lib.tool.Log;
import com.tty.tool.ConfigUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.List;

public class PlayerSit extends BaseAction {

    public final Block actionBlock;

    public PlayerSit(Player player, Block actionBlock) {
        super(player);
        this.actionBlock = actionBlock;
    }

    @Override
    public void action() {
        Location location = this.locationRecalculate();
        this.createEntity(location);
        //设置玩家转向
        this.action_player.setRotation(location.getYaw(), 0);
        this.action_player.sendActionBar(ConfigUtils.t("function.sit.tips"));

        this.task = Lib.Scheduler.runAtEntityFixedRate(
                Ari.instance,
                this.action_player,
                i -> {
                    Log.debug("action_player: " + this.action_player.getName() + " sit now, status: " + PLAYER_ACTION_MAP.size());
                    if (this.action_player.isDead() ||
                            this.action_player.isFlying() ||
                            this.action_player.isSleeping() ||
                            this.action_player.isDeeplySleeping() ||
                            !this.action_player.isOnline() ||
                            !this.action_player.isInsideVehicle()) {
                        this.cancel();
                    }
                },
                () -> {
                    Log.error("action_player: " + this.action_player.getName() + "sit error");
                    this.cancel();
                },
                1L,
                20L);
        this.add();
    }


    @Override
    public boolean check() {
        if (PLAYER_ACTION_MAP.containsKey(this.action_player)) return false;

        String name = this.actionBlock.getType().name();
        //获取列表判断是否满足的方块
        if (this.getDisableList().contains(name)) return false;

        BlockData blockData = this.actionBlock.getBlockData();
        //如果为楼梯sit
        if (blockData instanceof Stairs stairs) {
            //如果为倒放楼梯，不允许
            if (!this.checkTop() || stairs.getHalf().equals(Bisected.Half.TOP)) {
                this.action_player.sendActionBar(ConfigUtils.t("function.sit.error-location"));
                return false;
            }
            return name.endsWith("_STAIRS");
        }
        //如果为半砖sit
        if (blockData instanceof Slab) {
            if (!this.checkTop()) {
                this.action_player.sendActionBar(ConfigUtils.t("function.sit.error-location"));
                return false;
            }
            return name.endsWith("_SLAB");
        }
        return false;
    }

    @Override
    protected void createEntity(Location location) {
        this.entity = this.action_player.getWorld().spawnEntity(location, EntityType.AREA_EFFECT_CLOUD, CreatureSpawnEvent.SpawnReason.CUSTOM, e -> {
            if (e instanceof AreaEffectCloud cloud) {
                cloud.setRadius(0);
                cloud.setInvulnerable(true);
                cloud.setGravity(false);
                cloud.setInvisible(true);
                cloud.addPassenger(this.action_player);
            }
        });
    }

    //重新计算正确的坐的location
    private Location locationRecalculate() {
        Location location = this.actionBlock.getLocation();
        BlockData blockData = this.actionBlock.getBlockData();
        double centerX = 0.5;
        double centerZ = 0.5;

        if (blockData instanceof Stairs stairs) {
            location.add(centerX, 0, centerZ);
            location.setYaw(this.getYawFromBlockFace(stairs.getFacing()));
        } else if (blockData instanceof Slab slab){
            switch (slab.getType()) {
                case BOTTOM ->  location.add(centerX, 0, centerZ);
                case TOP, DOUBLE -> location.add(centerX, 0.5, centerZ);
            }
            location.setYaw(action_player.getYaw());
        }
        return location;
    }

    private boolean checkTop() {
        Location location = this.actionBlock.getLocation();
        World world = location.getWorld();

        if (!location.clone().add(0, 2, 0).getBlock().isEmpty()) {
            return false;
        }

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        BoundingBox seatBox = BoundingBox.of(
                new Vector(x + 0.1, y + 1, z + 0.1),
                new Vector(x + 0.9, y + 1.8, z + 0.9)
        );

        for (Entity entity : world.getNearbyEntities(seatBox)) {
            if (entity instanceof Player player &&
                    !player.equals(this.action_player) &&
                    player.getGameMode() != GameMode.SPECTATOR) {
                return false;
            }
        }
        return true;
    }

    private float getYawFromBlockFace(BlockFace face) {
        return switch (face) {
            case EAST -> 90.0F;
            case SOUTH -> 180.0F;
            case WEST -> -90.0F;
            default -> 0.0F;
        };
    }

    private List<String> getDisableList() {
        return ConfigUtils.getValue("action.sit.disable-block", FilePath.FunctionConfig, new TypeToken<List<String>>(){}.getType(), List.of());
    }
}
