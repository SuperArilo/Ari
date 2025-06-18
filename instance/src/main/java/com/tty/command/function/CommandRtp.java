package com.tty.command.function;

import com.google.gson.reflect.TypeToken;
import com.tty.Ari;
import com.tty.dto.rtp.RtpConfig;
import com.tty.enumType.FilePath;
import com.tty.function.TeleportCheck;
import com.tty.lib.EntityTeleport;
import com.tty.lib.Lib;
import com.tty.lib.enum_type.LangType;
import com.tty.lib.tool.RandomGeneratorUtils;
import com.tty.tool.ConfigObjectUtils;
import com.tty.tool.Log;
import com.tty.tool.PlayerStatusCheck;
import com.tty.tool.TextTool;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;


public class CommandRtp {

    private final CommandSender sender;
    private int count = 5;
    private boolean isRunning = false;
    private boolean isDone = false;
    private final World world;
    private final RtpConfig config;

    public CommandRtp(CommandSender sender) {
        this.sender = sender;
        this.world = ((Player) sender).getWorld();

        Map<String, RtpConfig> value = ConfigObjectUtils.getValue(
                "rtp.worlds",
                FilePath.FunctionConfig.getName(),
                new TypeToken<Map<String, RtpConfig>>() {
                }.getType(),
                null);
        this.config = value.get(this.world.getName());
    }

    public void rtp() {
        if (this.config == null || !ConfigObjectUtils.getValue(
                "rtp.enable",
                FilePath.FunctionConfig.getName(),
                Boolean.class,
                false)) return;

        if (!this.config.isEnable()) {
            this.sender.sendMessage(
                    TextTool.setHEXColorText(
                            "function.rtp.world-disable",
                            FilePath.Lang));
            return;
        }

        if (!(sender instanceof Player player)) return;

        if (!PlayerStatusCheck.playerStatusCheck(player) && !this.sender.isOp()) return;

        if (!TeleportCheck.create().preCheckStatus(
                player,
                null,
                ConfigObjectUtils.getValue("rtp.delay", FilePath.FunctionConfig.getName(), Integer.class, 3) * 20)) return;

        Lib.Scheduler.runAsyncAtFixedRate(Ari.instance, i -> {
            if (this.count <= 0) {
                this.sender.clearTitle();
                this.sender.sendMessage(TextTool.setHEXColorText("function.rtp.search-failure", FilePath.Lang));
                i.cancel();
                return;
            }
            if (this.isDone) {
                i.cancel();
                return;
            }
            if (this.isRunning) return;
            this.search();
            this.isRunning = true;
        }, 1L, 20L);
    }

    private void search() {
        final long l = System.currentTimeMillis();
        this.count--;
        this.sendCountTitle();
        if (this.count <= 0) {
            return;
        }
        int x = RandomGeneratorUtils.get((int) this.config.getMin(), (int) this.config.getMax());
        int z = RandomGeneratorUtils.get((int) this.config.getMin(), (int) this.config.getMax());

        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        int relativeX = x & 0xF;
        int relativeZ = z & 0xF;

        this.world.getChunkAtAsync(chunkX, chunkZ).thenAccept(chunk -> {
            int highestBlockYAt = chunk.getChunkSnapshot().getHighestBlockYAt(relativeX, relativeZ);
            Block block = chunk.getBlock(relativeX, highestBlockYAt, relativeZ);
            Lib.Scheduler.runAtRegion(Ari.instance, this.world, chunkX, chunkZ, i -> {
                if (this.isLocationSafe(block)) {
                    int y = block.getLocation().getBlockY();
                    Log.debug("random location " + x + ", " + y + ", " + z);
                    this.isDone = true;
                    this.count = 10;
                    int finalY = y + 1;
                    Player player = (Player) this.sender;
                    player.clearTitle();
                    Lib.Scheduler.runAtEntity(Ari.instance, player, b -> {
                        Location targetLocation = new Location(this.world, x + 0.5, finalY, z + 0.5);
                        boolean teleport = EntityTeleport.teleport(player, targetLocation);
                        this.sender.sendMessage(TextTool.setHEXColorText(
                                teleport ? "teleport.success":"base.on-error",
                                FilePath.Lang));
                    }, () -> Log.error("teleport error on " + player.getName()));
                } else {
                    Log.debug(" not safe skip...");
                }
                this.isRunning = false;
                Log.debug("search time: " + (System.currentTimeMillis() -l) + "ms");
            });
        });
    }

    private boolean isLocationSafe(Block feetBlock) {
        World world = feetBlock.getWorld();
        int x = feetBlock.getX();
        int y = feetBlock.getY();
        int z = feetBlock.getZ();

        Material feetMaterial = feetBlock.getType();
        if (!isSafeStandingBlock(feetMaterial)) {
            return false;
        }

        Material bodyMaterial = world.getBlockAt(x, y + 1, z).getType();
        Material headMaterial = world.getBlockAt(x, y + 2, z).getType();

        if (isSolid(bodyMaterial) || isSolid(headMaterial) ||
                isDangerous(bodyMaterial) || isDangerous(headMaterial)) {
            return false;
        }

        if (isDangerous(feetMaterial)) return false;

        if (world.getBlockAt(x, y - 1, z).getType().isAir()) {
            return false;
        }

        BlockFace[] directions = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
        for (BlockFace face : directions) {
            Block adjacent = feetBlock.getRelative(face);
            Material adjacentType = adjacent.getType();

            if (isDangerous(adjacentType)) return false;

            if (adjacent.getRelative(BlockFace.DOWN).getType().isAir()) {
                return false;
            }
        }

        return true;
    }

    private boolean isSafeStandingBlock(Material material) {
        return material.isSolid() &&
                !material.name().contains("LEAVES") &&
                !material.name().contains("GLASS") &&
                material != Material.SLIME_BLOCK;
    }

    private boolean isSolid(Material material) {
        return switch (material) {
            case AIR, CAVE_AIR, VOID_AIR, WATER, LAVA -> false;
            default -> material.isSolid();
        };
    }

    private boolean isDangerous(Material material) {
        return switch (material) {
            case LAVA, FIRE, SOUL_FIRE, MAGMA_BLOCK, CACTUS, SWEET_BERRY_BUSH -> true;
            default -> false;
        };
    }

    private void sendCountTitle() {
        String sub = ConfigObjectUtils.getValue(
                "function.rtp.search-count",
                FilePath.Lang.getName(),
                String.class,
                "null");
        sub = sub.replace(LangType.RTPSEARCHCOUNT.getType(), String.valueOf(this.count));
        Title title = TextTool.setPlayerTitle(
                ConfigObjectUtils.getValue("function.rtp.searching", FilePath.Lang.getName(), String.class, "null"),
                sub,
                0,
                1000L,
                1000L);
        this.sender.showTitle(title);
    }
}
