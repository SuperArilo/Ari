package com.tty.command.function;

import com.google.gson.reflect.TypeToken;
import com.tty.Ari;
import com.tty.command.check.TeleportCheck;
import com.tty.dto.rtp.RtpConfig;
import com.tty.enumType.FilePath;
import com.tty.function.Teleport;
import com.tty.lib.Lib;
import com.tty.lib.enum_type.LangType;
import com.tty.lib.tool.ComponentUtils;
import com.tty.lib.tool.Log;
import com.tty.lib.tool.RandomGeneratorUtils;
import com.tty.tool.ConfigUtils;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;


public class CommandRtp {

    private final CommandSender sender;
    private final int initCount = ConfigUtils.getValue("rtp.search-count", FilePath.FunctionConfig, Integer.class, 10);
    private int count = 0;
    private final boolean showSearchResult = ConfigUtils.getValue("rtp.show-search-result", FilePath.FunctionConfig, Boolean.class, false);
    private boolean isRunning = false;
    private boolean isDone = false;
    private final World world;
    private final RtpConfig config;

    public CommandRtp(CommandSender sender) {
        this.sender = sender;
        this.world = ((Player) sender).getWorld();

        Map<String, RtpConfig> value = ConfigUtils.getValue(
                "rtp.worlds",
                FilePath.FunctionConfig,
                new TypeToken<Map<String, RtpConfig>>() {
                }.getType(),
                null);
        this.config = value.get(this.world.getName());
    }

    public void rtp() {
        if (this.config == null || !ConfigUtils.getValue(
                "rtp.enable",
                FilePath.FunctionConfig,
                Boolean.class,
                false)) return;

        if (!this.config.isEnable()) {
            this.sender.sendMessage(ComponentUtils.text(ConfigUtils.getValue("function.rtp.world-disable", FilePath.Lang)));
            return;
        }

        if (!(sender instanceof Player player)) return;

        if ((player.isSleeping() || player.isDeeplySleeping() || player.isFlying()) && !this.sender.isOp()) return;

        if (!TeleportCheck.preCheckStatus(
                player,
                null,
                ConfigUtils.getValue("rtp.delay", FilePath.FunctionConfig, Integer.class, 3) * 20)
        ) return;

        Lib.Scheduler.runAsyncAtFixedRate(Ari.instance, i -> {
            if (this.count >= this.initCount) {
                this.sender.clearTitle();
                this.sender.sendMessage(ComponentUtils.text(ConfigUtils.getValue("function.rtp.search-failure", FilePath.Lang)));
                i.cancel();
                return;
            }
            if (this.isDone) {
                i.cancel();
                return;
            }
            if (this.isRunning) return;
            this.count++;
            this.search();
            this.isRunning = true;
        }, 1L, 20L);
    }

    private void search() {

        final long l = System.currentTimeMillis();
        this.sendCountTitle();

        int x = (int) Math.min(RandomGeneratorUtils.get((int) this.config.getMin(), (int) this.config.getMax()), this.world.getWorldBorder().getMaxSize());
        int z = (int) Math.min(RandomGeneratorUtils.get((int) this.config.getMin(), (int) this.config.getMax()), this.world.getWorldBorder().getMaxSize());

        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        int relativeX = x & 0xF;
        int relativeZ = z & 0xF;

        boolean isNether = this.world.getEnvironment().equals(World.Environment.NETHER);

        this.world.getChunkAtAsync(chunkX, chunkZ).thenAccept(chunk -> {
            int highestBlockYAt = isNether ? this.getHighestBlockYAtNether(chunk, relativeX, relativeZ):chunk.getChunkSnapshot().getHighestBlockYAt(relativeX, relativeZ);
            Lib.Scheduler.runAtRegion(Ari.instance, this.world, chunkX, chunkZ, i -> {
                if (this.isLocationSafe(chunk, relativeX, highestBlockYAt, relativeZ)) {
                    Log.debug("random location " + x + ", " + highestBlockYAt + ", " + z);
                    this.isDone = true;
                    this.count = 0;
                    int finalY = highestBlockYAt + 1;
                    Player player = (Player) this.sender;
                    player.clearTitle();
                    Lib.Scheduler.runAtEntity(Ari.instance, player, b -> {
                        Location targetLocation = new Location(this.world, x + 0.5, finalY, z + 0.5);
                        Teleport.create(player, targetLocation, 0).teleport();
                    }, () -> Log.error("teleport error on " + player.getName()));
                }
                this.isRunning = false;
                Log.debug("search time: " + (System.currentTimeMillis() -l) + "ms");
            });
        }).exceptionally(i -> {
            Log.error("search error", i);
            return null;
        });
    }

    //下界特殊处理
    private int getHighestBlockYAtNether(Chunk chunk, int chunkX, int chunkZ) {
        final int minHeight = this.world.getMinHeight();

        int value = 0;
        for (int y = 80;y >= minHeight;y--) {
            Block block = chunk.getBlock(chunkX, y, chunkZ);
            if (block.isLiquid()) break;
            if (block.isEmpty() || block.isPassable()) continue;
            value = y;
            break;
        }
        return value;
    }

    private boolean isLocationSafe(Chunk chunk, int chunkX, int chunkY, int chunkZ) {

        //判断Y轴高度合不合法
        if (chunkY < chunk.getWorld().getMinHeight()) {
            Log.debug("rtp: illegal Y-axis height.");
            return false;
        }

        Block block = chunk.getBlock(chunkX, chunkY, chunkZ);

        //身体检查
        Material head = chunk.getBlock(chunkX, chunkY + 2, chunkZ).getType();
        Material body = chunk.getBlock(chunkX, chunkY + 1, chunkZ).getType();
        Material feet = block.getType();

        //周围检查
        Material left = block.getRelative(1, 0, 0).getType();
        Material right = block.getRelative(-1, 0, 0).getType();
        Material front = block.getRelative(0, 1, 0).getType();
        Material behind = block.getRelative(0, -1, 0).getType();

        if (!isSafeStandingBlock(feet)) {
            this.sendSearchMessage(ConfigUtils.getValue("function.rtp.tips-result.unstable-feet-position", FilePath.Lang));
            Log.debug("standing block illegal.");
            return false;
        }

        if (isSolid(body) || isSolid(head) ||
                isDangerous(body) || isDangerous(head) ||
                isDangerous(left) || isDangerous(right) || isDangerous(front) || isDangerous(behind)) {
            this.sendSearchMessage(ConfigUtils.getValue("function.rtp.tips-result.dangerous-surroundings", FilePath.Lang));
            Log.debug("the blocks around the player are illegal.");
            return false;
        }

        if (isDangerous(feet)) {
            this.sendSearchMessage(ConfigUtils.getValue("function.rtp.tips-result.dangerous-feet-block", FilePath.Lang));
            Log.debug("feet block is dangerous.");
            return false;
        }
        if (chunk.getBlock(chunkX, chunkY - 1, chunkZ).getType().isAir()) {
            this.sendSearchMessage(ConfigUtils.getValue("function.rtp.tips-result.unstable-feet-position", FilePath.Lang));
            Log.debug("feet block illegal.");
            return false;
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
        String sub = ConfigUtils.getValue(
                "function.rtp.title-search-count",
                FilePath.Lang,
                String.class,
                "null");
        sub = sub.replace(LangType.RTPSEARCHCOUNT.getType(), String.valueOf(this.initCount - this.count));
        Title title = ComponentUtils.setPlayerTitle(
                ConfigUtils.getValue("function.rtp.title-searching", FilePath.Lang, String.class, "null"),
                sub,
                0,
                1000L,
                1000L);
        this.sender.showTitle(title);
    }

    private void sendSearchMessage(String message) {
        if (!this.showSearchResult) return;
        String s = ConfigUtils.getValue("function.rtp.search-count-report", FilePath.Lang).replace(LangType.RTPSEARCHCOUNT.getType(), String.valueOf(this.count));
        this.sender.sendMessage(ComponentUtils.text(s + message));
    }
}
