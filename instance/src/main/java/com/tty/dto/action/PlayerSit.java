package com.tty.dto.action;

import com.google.gson.reflect.TypeToken;
import com.tty.Ari;
import com.tty.dto.BaseAction;
import com.tty.enumType.FilePath;
import com.tty.lib.Lib;
import com.tty.lib.tool.Log;
import com.tty.tool.ConfigObjectUtils;
import com.tty.tool.TextTool;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.List;

import static com.tty.listener.player.PlayerActionListener.PLAYER_SIT_ACTION_MAP;


public class PlayerSit extends BaseAction {

    public final Block actionBlock;

    public PlayerSit(Player player, Block actionBlock) {
        super(player);
        this.actionBlock = actionBlock;
    }

    @Override
    public boolean action(Entity entity) {
        Location location = this.locationRecalculate();
        this.createEntity(location);
        //设置玩家转向
        this.player.setRotation(location.getYaw(), 0);
        this.player.sendActionBar(TextTool.setHEXColorText("function.sit.tips", FilePath.Lang));

        this.task = Lib.Scheduler.runAtEntityFixedRate(
                Ari.instance,
                this.player,
                i -> {
                    Log.debug("player: " + this.player.getName() + " sit now, status: " + PLAYER_SIT_ACTION_MAP.size());
                    if (this.player.isDead() ||
                            this.player.isFlying() ||
                            !this.player.isOnline() ||
                            !this.player.isInsideVehicle()) {
                        this.cancel();
                        PLAYER_SIT_ACTION_MAP.remove(this.player);
                    }
                },
                () -> {
                    Log.error("player: " + this.player.getName() + "sit error");
                    this.cancel();
                    PLAYER_SIT_ACTION_MAP.remove(this.player);
                },
                1L,
                20L);
        return true;
    }


    @Override
    public boolean check() {

        String name = this.actionBlock.getType().name();
        //获取列表判断是否满足的方块
        if (this.getDisableList().contains(name)) return false;

        BlockData blockData = this.actionBlock.getBlockData();
        //如果为楼梯sit
        if (blockData instanceof Stairs stairs) {
            //如果为倒放楼梯，不允许
            if (!this.checkTop() || stairs.getHalf().equals(Bisected.Half.TOP)) {
                this.player.sendActionBar(TextTool.setHEXColorText("function.sit.error-location", FilePath.Lang));
                return false;
            }
            return name.endsWith("_STAIRS");
        }
        //如果为半砖sit
        if (blockData instanceof Slab) {
            if (!this.checkTop()) {
                this.player.sendActionBar(TextTool.setHEXColorText("function.sit.error-location", FilePath.Lang));
                return false;
            }
            return name.endsWith("_SLAB");
        }
        return false;
    }

    @Override
    protected void createEntity(Location location) {
        this.entity = this.player.getWorld().spawnEntity(location, EntityType.AREA_EFFECT_CLOUD, CreatureSpawnEvent.SpawnReason.CUSTOM, e -> {
            if (e instanceof AreaEffectCloud cloud) {
                cloud.setRadius(0);
                cloud.setInvulnerable(true);
                cloud.setGravity(false);
                cloud.setInvisible(true);
                cloud.addPassenger(this.player);
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
            location.setYaw(player.getYaw());
        }
        return location;
    }

    private boolean checkTop() {
        Location location = this.actionBlock.getLocation();
        //判断sit位置上方是否村子有效空间
        //判断是否已经有玩家sit到指定位置
        return location.clone().add(0, 1, 0).getBlock().isEmpty() &&
                location.getNearbyLivingEntities(1).stream().noneMatch(i -> i instanceof Player);
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
        return ConfigObjectUtils.getValue("action.sit.disable-block", FilePath.FunctionConfig.getName(), new TypeToken<List<String>>(){}.getType(), List.of());
    }
}
