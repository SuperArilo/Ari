package com.tty.states.action;

import com.google.gson.reflect.TypeToken;
import com.tty.Ari;
import com.tty.lib.Log;
import com.tty.lib.dto.State;
import com.tty.dto.state.action.PlayerSitActionState;
import com.tty.enumType.FilePath;
import com.tty.lib.Lib;
import com.tty.lib.services.impl.StateServiceImpl;
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
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.List;

public class PlayerSitActionStateServiceImpl extends StateServiceImpl {

    public PlayerSitActionStateServiceImpl(long rate, long c, boolean isAsync, JavaPlugin javaPlugin) {
        super(rate, c, isAsync, javaPlugin);
    }

    @Override
    protected boolean canAddState(State state) {
        if (!(state instanceof PlayerSitActionState s)) return false;
        Player owner = (Player) s.getOwner();
        String playerName = owner.getName();
        //判断玩家是否已经 sit 了
        if (!this.getStates(owner).isEmpty()) {
            Log.debug("player %s is sited. skip...", playerName);
            return false;
        }
        //获取列表判断是否满足的方块
        Block sitBlock = s.getSitBlock();
        String sitBlockName = sitBlock.getType().name();
        if (this.getDisableList().contains(sitBlockName)) {
            Log.debug("player %s interact the block %s is disabled", playerName, sitBlockName);
            return false;
        }
        BlockData blockData = sitBlock.getBlockData();
        //如果为楼梯
        if (blockData instanceof Stairs stairs) {
            //如果为倒放楼梯，不允许
            if (!this.checkBlockTop(owner, sitBlock) || stairs.getHalf().equals(Bisected.Half.TOP)) {
                owner.sendActionBar(ConfigUtils.t("function.sit.error-location"));
                return false;
            }
            return sitBlockName.endsWith("_STAIRS");
            //如果为半砖
        } else if (blockData instanceof Slab) {
            if (!this.checkBlockTop(owner, sitBlock)) {
                owner.sendActionBar(ConfigUtils.t("function.sit.error-location"));
                return false;
            }
            return sitBlockName.endsWith("_SLAB");
        } else {
            return false;
        }
    }

    @Override
    protected void condition(State state) {
        if (!(state instanceof PlayerSitActionState s)) return;
        Player owner = (Player) s.getOwner();
        if (s.getTool_entity() == null) {
            Log.error("player %s tool entity is null", owner.getName());
            return;
        }

        boolean b = !owner.isDead() &&
                !owner.isFlying() &&
                !owner.isSleeping() &&
                !owner.isDeeplySleeping() &&
                owner.isOnline() &&
                owner.isInsideVehicle();
        if (b) {
            Log.debug("player %s is sitting now.", owner.getName());
        } else {
            state.setOver(true);
        }
    }

    @Override
    protected void abortAddState(State state) {
        if (state instanceof PlayerSitActionState s) {
            Log.debug("player %s try sit block %s fail", s.getOwner().getName(), s.getSitBlock().getType().name());
        }
    }

    @Override
    protected void passAddState(State state) {
        if (!(state instanceof PlayerSitActionState s)) return;
        Player player = (Player) s.getOwner();
        Block sitBlock = s.getSitBlock();
        Log.debug("create sit entity to player %s", player.getName());
        Location location = this.locationRecalculate(player, sitBlock);
        Entity entity = player.getWorld()
                .spawnEntity(
                        location,
                        EntityType.AREA_EFFECT_CLOUD,
                        CreatureSpawnEvent.SpawnReason.CUSTOM,
                        e -> {
                            if (e instanceof AreaEffectCloud cloud) {
                                cloud.setRadius(0);
                                cloud.setInvulnerable(true);
                                cloud.setGravity(false);
                                cloud.setInvisible(true);
                                cloud.addPassenger(player);
                            }
                        }
                );
        player.setRotation(location.getYaw(), 0);
        player.sendActionBar(ConfigUtils.t("function.sit.tips"));
        s.setTool_entity(entity);
    }

    @Override
    protected void onEarlyExit(State state) {
        if (state instanceof PlayerSitActionState s) {
            String playerName = s.getOwner().getName();
            Log.debug("player %s sit check status fail, remove tool entity", playerName);
            Entity toolEntity = s.getTool_entity();
            if (toolEntity != null) {
                Lib.Scheduler.runAtEntity(
                    Ari.instance,
                    toolEntity,
                    i -> {
                        s.getTool_entity().remove();
                        s.setTool_entity(null);
                    },
                    () -> Log.error("error on player %s sit when remove tool entity", playerName));
            }
        }
    }

    @Override
    protected void onFinished(State state) {

    }

    //计算正确 sit 的 location
    private Location locationRecalculate(Player player, Block sitBlock) {
        Location location = sitBlock.getLocation();
        BlockData blockData = sitBlock.getBlockData();
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

    private boolean checkBlockTop(Player p, Block actionBlock) {
        Location location = actionBlock.getLocation();
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
                    !player.equals(p) &&
                    player.getGameMode() != GameMode.SPECTATOR) {
                return false;
            }
        }
        return true;
    }

    private List<String> getDisableList() {
        return Ari.C_INSTANCE.getValue("action.sit.disable-block", FilePath.FUNCTION_CONFIG, new TypeToken<List<String>>(){}.getType(), List.of());
    }

    private float getYawFromBlockFace(BlockFace face) {
        return switch (face) {
            case EAST -> 90.0F;
            case SOUTH -> 180.0F;
            case WEST -> -90.0F;
            default -> 0.0F;
        };
    }

}
