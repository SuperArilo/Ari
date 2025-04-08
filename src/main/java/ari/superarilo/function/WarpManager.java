package ari.superarilo.function;

import ari.superarilo.Ari;
import ari.superarilo.dto.Page;
import ari.superarilo.entity.sql.ServerWarp;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.function.impl.BaseFunctionImpl;
import ari.superarilo.mapper.ServerWrapMapper;
import ari.superarilo.tool.Log;
import ari.superarilo.tool.SQLInstance;
import ari.superarilo.tool.TextTool;
import org.apache.ibatis.session.SqlSession;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class WarpManager extends BaseFunctionImpl {

    private final String playerUUID;
    private final Location location;

    private WarpManager(String playerUUID) {
        this.playerUUID = playerUUID;
        Player player = Bukkit.getPlayer(UUID.fromString(playerUUID));
        this.location = Objects.requireNonNull(player).getLocation();
    }

    public CompletableFuture<List<ServerWarp>> asyncGetList(int pageNum, int pageSize) {
        long start = System.currentTimeMillis();
        CompletableFuture<List<ServerWarp>> future = new CompletableFuture<>();
        Bukkit.getAsyncScheduler().runNow(Ari.instance, i -> {
            try(SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)){
                List<ServerWarp> serverWarps = sqlSession.getMapper(ServerWrapMapper.class).getServerWarps(Page.create(pageNum, pageSize));
                future.complete(serverWarps);
            } catch (Exception e) {
                future.completeExceptionally(e);
                Log.error("query warps error!", e);
            } finally {
                Log.debug(Level.INFO, "get home warp time: " + (System.currentTimeMillis() - start) + "ms");
            }
        });
        return future;
    }

    public CompletableFuture<List<String>> asyncGetIdList() {
        long start = System.currentTimeMillis();
        CompletableFuture<List<String>> future = new CompletableFuture<>();
        Bukkit.getAsyncScheduler().runNow(Ari.instance, i -> {
            try (SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)) {
                ServerWrapMapper mapper = sqlSession.getMapper(ServerWrapMapper.class);
                List<String> warpIdList = mapper.getWarpIdList(this.playerUUID);
                future.complete(warpIdList);
            } catch (Exception e) {
                future.completeExceptionally(e);
                Log.error("SqlSession error", e);
            } finally {
                Log.debug(Level.INFO, "query warpId list time: " + (System.currentTimeMillis() - start) + "ms");
            }
        });
        return future;
    }

    public CompletableFuture<ServerWarp> asyncGetInstance(String id) {
        CompletableFuture<ServerWarp> future = new CompletableFuture<>();
        Bukkit.getAsyncScheduler().runNow(Ari.instance, i -> {
            try (SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)) {
                ServerWrapMapper mapper = sqlSession.getMapper(ServerWrapMapper.class);
                future.complete(mapper.getWarp(id, this.playerUUID));
            } catch (Exception e) {
                future.completeExceptionally(e);
                Log.error("SqlSession error", e);
            }
        });
        return future;
    }

    public void createInstance(String warpId) {
        Material material = this.checkIsItem(this.location.getBlock().getRelative(BlockFace.DOWN).getType());
        Bukkit.getAsyncScheduler().runNow(Ari.instance, i -> {
            long start = System.currentTimeMillis();
            try (SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)) {
                Player player = Bukkit.getPlayer(UUID.fromString(this.playerUUID));
                if(player == null) {
                    Log.error("player: " + this.playerUUID + "is not online");
                    return;
                }
                ServerWrapMapper mapper = sqlSession.getMapper(ServerWrapMapper.class);
                List<String> warpIdList = mapper.getWarpIdList(this.playerUUID);

                boolean hasPermission = Ari.instance.permissionUtils.hasPermission(player, "ari.count.warp." + warpIdList.size() + 1) || player.isOp();

                if(!hasPermission) {
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
                ServerWarp warp = new ServerWarp();
                warp.setWarpId(warpId);
                warp.setWarpName(warpId);
                warp.setCreateBy(this.playerUUID);
                warp.setLocation(this.location.toString());
                warp.setShowMaterial(material.name());

                mapper.save(warp);
                player.sendMessage(TextTool.setHEXColorText("function.warp.create-success", FilePath.Lang, player));
            } catch (Exception e) {
                Log.error(e.getMessage());
                i.cancel();
            } finally {
                Log.debug(Level.INFO, "save warp done. time: " + (System.currentTimeMillis() - start) + "ms");
            }
        });
    }

    public void deleteInstance(String warpId) {
        long start = System.currentTimeMillis();
        Bukkit.getAsyncScheduler().runNow(Ari.instance, i -> {
            Player player = Bukkit.getPlayer(UUID.fromString(this.playerUUID));
            if(player == null) {
                Log.error("player: " + this.playerUUID + "is not online");
                return;
            }
           try (SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)) {
               Integer delete = sqlSession.getMapper(ServerWrapMapper.class).delete(warpId, player.getUniqueId().toString());
               if(delete == 1){
                   player.sendMessage(TextTool.setHEXColorText("function.warp.delete-success", FilePath.Lang));
               } else {
                   player.sendMessage(TextTool.setHEXColorText("function.warp.not-found", FilePath.Lang));
               }
           } catch (Exception e) {
               Log.error("remove warp fail, id: " + warpId, e);
           } finally {
               Log.debug(Level.INFO, "remove warp time: " + (System.currentTimeMillis() - start) + "ms");
           }
        });
    }

    public CompletableFuture<Boolean> modify(ServerWarp instance) {
        long start = System.currentTimeMillis();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Bukkit.getAsyncScheduler().runNow(Ari.instance, i -> {
           try (SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)) {
               Integer update = sqlSession.getMapper(ServerWrapMapper.class).update(instance);
               if(update == 1) {
                   future.complete(true);
               } else {
                   future.complete(false);
                   Log.error("save warpId: [" + instance.getWarpId() + "] error");
               }
           } catch (Exception e) {
               Log.error("save warp error", e);
               future.complete(false);
           } finally {
               Log.debug("save time: " + (System.currentTimeMillis() - start) + "ms");
           }
        });
        return future;
    }

    public static WarpManager create(String playerUUID) {
        return new WarpManager(playerUUID);
    }
}
