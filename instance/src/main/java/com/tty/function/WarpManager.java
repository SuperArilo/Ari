package com.tty.function;

import com.tty.Ari;
import com.tty.entity.sql.ServerWarp;
import com.tty.enumType.FilePath;
import com.tty.function.impl.BaseFunctionImpl;
import com.tty.lib.Lib;
import com.tty.lib.dto.Page;
import com.tty.tool.Log;
import com.tty.tool.SQLInstance;
import com.tty.tool.TextTool;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.sql2o.Connection;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class WarpManager extends BaseFunctionImpl {

    private final Player player;
    private final Location location;

    private WarpManager(Player player) {
        this.player = player;
        this.location = player.getLocation();
    }

    public CompletableFuture<List<ServerWarp>> asyncGetList(int pageNum, int pageSize) {
        long start = System.currentTimeMillis();
        CompletableFuture<List<ServerWarp>> future = new CompletableFuture<>();
        Lib.Scheduler.run(Ari.instance, i -> {
            Page page = Page.create(pageNum, pageSize);
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                List<ServerWarp> serverWarps = connection.createQuery("""
                                    select * from %swarps limit :limit offset :offset
                                """.formatted(SQLInstance.getTablePrefix()))
                        .addParameter("limit", page.getLimit())
                        .addParameter("offset", page.getOffset())
                        .executeAndFetch(ServerWarp.class);
                future.complete(serverWarps);
            } catch (Exception e) {
                future.completeExceptionally(e);
            } finally {
                Log.debug(Level.INFO, "get home warp time: " + (System.currentTimeMillis() - start) + "ms");
            }
        });
        return future;
    }

    public CompletableFuture<ServerWarp> asyncGetInstance(String id) {
        CompletableFuture<ServerWarp> future = new CompletableFuture<>();
        Lib.Scheduler.run(Ari.instance, i -> {
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                ServerWarp serverWarp = connection.createQuery("""
                                    select * from %swarps where warp_id = :warp_id and create_by = :create_by
                                """.formatted(SQLInstance.getTablePrefix()))
                        .addParameter("warp_id", id)
                        .addParameter("create_by", this.player.getUniqueId())
                        .executeAndFetchFirst(ServerWarp.class);

                future.complete(serverWarp);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    public CompletableFuture<Boolean> createInstance(String warpId) {
        Material material = this.checkIsItem(this.location.getBlock().getRelative(BlockFace.DOWN).getType());
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Lib.Scheduler.run(Ari.instance, i -> {
            long start = System.currentTimeMillis();
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                List<String> warpIdList = connection.createQuery("""
                                   select warp_id from %swarps where create_by = :create_by
                                """.formatted(SQLInstance.getTablePrefix()))
                        .addParameter("create_by", player.getUniqueId())
                        .executeAndFetch(String.class);
                if(warpIdList.size() + 1 > this.getMaxWarpCount(player) && !player.isOp()) {
                    Log.debug("Exceeds the specified quantity");
                    player.sendMessage(TextTool.setHEXColorText("function.warp.exceeds", FilePath.Lang));
                    i.cancel();
                    return;
                }
                if(warpIdList.contains(warpId)) {
                    player.sendMessage(TextTool.setHEXColorText("function.warp.exist", FilePath.Lang, player));
                    i.cancel();
                    return;
                }

                int result = connection.createQuery("""
                                    insert into %swarps
                                    (warp_id, warp_name, create_by, location, show_material, permission, cost)
                                    values
                                    (:warp_id, :warp_name, :create_by, :location, :show_material, :permission, :cost)
                                """.formatted(SQLInstance.getTablePrefix()))
                        .addParameter("warp_id", warpId)
                        .addParameter("warp_name", warpId)
                        .addParameter("create_by", player.getUniqueId())
                        .addParameter("location", this.location.toString())
                        .addParameter("show_material", material.name())
                        .executeUpdate().getResult();
                future.complete(result == 1);
            } catch (Exception e) {
                future.completeExceptionally(e);
            } finally {
                Log.debug(Level.INFO, "save warp done. time: " + (System.currentTimeMillis() - start) + "ms");
            }
        });
        return future;
    }

    public CompletableFuture<Boolean> deleteInstance(String warpId) {
        long start = System.currentTimeMillis();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Lib.Scheduler.run(Ari.instance, i -> {
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                int delete = connection.createQuery("""
                                    delete from %swarps where create_by = :create_by and warp_id = :warp_id
                                """.formatted(SQLInstance.getTablePrefix()))
                        .addParameter("warp_id", warpId)
                        .addParameter("create_by", this.player.getUniqueId())
                        .executeUpdate().getResult();

                future.complete(delete == 1);
            } catch (Exception e) {
                future.completeExceptionally(e);
            } finally {
                Log.debug(Level.INFO, "remove warp time: " + (System.currentTimeMillis() - start) + "ms");
            }
        });
        return future;
    }

    public CompletableFuture<Boolean> modify(ServerWarp instance) {
        long start = System.currentTimeMillis();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Lib.Scheduler.run(Ari.instance, i -> {
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                int update = connection.createQuery("""
                                    update %swarps set
                                        warp_name = :warp_name, location = :location,
                                        show_material = :show_material, permission = :permission,
                                        cost = :cost
                                    where warp_id =:warp_id and create_by = :create_by
                                """.formatted(SQLInstance.getTablePrefix())).addParameter("warp_name", instance.getWarpName())
                        .addParameter("location", instance.getLocation())
                        .addParameter("show_material", instance.getShowMaterial())
                        .addParameter("permission", instance.getPermission())
                        .addParameter("cost", instance.getCost())
                        .addParameter("warp_id", instance.getWarpId())
                        .addParameter("create_by", this.player.getUniqueId())
                        .executeUpdate().getResult();
                future.complete(update == 1);
            } catch (Exception e) {
                future.completeExceptionally(e);
            } finally {
                Log.debug("save time: " + (System.currentTimeMillis() - start) + "ms");
            }
        });
        return future;
    }

    private int getMaxWarpCount(Player player) {
        int maxHomes = 0;
        String firstErrorPermission = null;
        for (PermissionAttachmentInfo permissionInfo : player.getEffectivePermissions()) {
            String permission = permissionInfo.getPermission();
            if (!permission.startsWith("ari.count.warp.")) continue;
            String[] parts = permission.split("\\.");
            if (parts.length < 4) {
                if (firstErrorPermission == null) firstErrorPermission = permission;
                continue;
            }
            try {
                int homeCount = Integer.parseInt(parts[3]);
                if (homeCount > maxHomes) maxHomes = homeCount;
            } catch (NumberFormatException e) {
                if (firstErrorPermission == null) firstErrorPermission = permission;
            }
        }
        if (maxHomes == 0 && firstErrorPermission != null) {
            player.sendMessage(TextTool.setHEXColorText("base.on-error", FilePath.Lang));
            Log.error("player " + player.getName() + " permission format error: " + firstErrorPermission);
        }
        return maxHomes;
    }

    public static WarpManager create(Player player) {
        return new WarpManager(player);
    }
}
