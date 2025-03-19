package ari.superarilo.function.impl;

import ari.superarilo.Ari;
import ari.superarilo.dto.Page;
import ari.superarilo.entity.sql.ServerWarp;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.function.WarpManager;
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
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class WarpManagerImpl extends BaseFunctionImpl implements WarpManager {

    private final Player player;
    private final Location location;

    public WarpManagerImpl(Player player) {
        this.player = player;
        this.location = player.getLocation();
    }

    @Override
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

    @Override
    public CompletableFuture<List<String>> asyncGetIdList() {
        long start = System.currentTimeMillis();
        CompletableFuture<List<String>> future = new CompletableFuture<>();
        Bukkit.getAsyncScheduler().runNow(Ari.instance, i -> {
            try (SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)) {
                ServerWrapMapper mapper = sqlSession.getMapper(ServerWrapMapper.class);
                List<String> warpIdList = mapper.getWarpIdList(this.player.getUniqueId().toString());
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

    @Override
    public void createInstance(String warpId) {
        Material material = this.checkIsItem(this.location.getBlock().getRelative(BlockFace.DOWN).getType());
        Bukkit.getAsyncScheduler().runNow(Ari.instance, i -> {
            long start = System.currentTimeMillis();
            try (SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)) {

                ServerWrapMapper mapper = sqlSession.getMapper(ServerWrapMapper.class);
                List<String> warpIdList = mapper.getWarpIdList(this.player.getUniqueId().toString());
                Integer value = Ari.instance.configManager.getValue("main.quantity." + Ari.instance.permissionUtils.getPlayerGroup(this.player), FilePath.WarpConfig, Integer.class);
                if(warpIdList.size() >= value && value != -1) {
                    Log.debug("Exceeds the specified quantity");
                    this.player.sendMessage(TextTool.setHEXColorText("command.setwarp.exceeds", FilePath.Lang));
                    i.cancel();
                    return;
                }
                if(warpIdList.contains(warpId)) {
                    this.player.sendMessage(TextTool.setHEXColorText("command.setwarp.exist", FilePath.Lang, this.player));
                    i.cancel();
                    return;
                }
                ServerWarp warp = new ServerWarp();
                warp.setWarpId(warpId);
                warp.setWarpName(warpId);
                warp.setCreateBy(this.player.getUniqueId().toString());
                warp.setLocation(this.location.toString());
                warp.setShowMaterial(material.name());

                mapper.save(warp);
                this.player.sendMessage(TextTool.setHEXColorText("command.setwarp.success", FilePath.Lang, this.player));
            } catch (Exception e) {
                Log.error(e.getMessage());
                i.cancel();
            } finally {
                Log.debug(Level.INFO, "save warp done. time: " + (System.currentTimeMillis() - start));
            }
        });
    }

    @Override
    public void deleteInstance(String warpId) {
        long start = System.currentTimeMillis();
        Bukkit.getAsyncScheduler().runNow(Ari.instance, i -> {
           try (SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)) {
               Integer delete = sqlSession.getMapper(ServerWrapMapper.class).delete(warpId, this.player.getUniqueId().toString());
               if(delete == 1){
                   this.player.sendMessage(TextTool.setHEXColorText("command.deletehome.success", FilePath.Lang));
               } else {
                   this.player.sendMessage(TextTool.setHEXColorText("command.deletehome.none", FilePath.Lang));
               }
           } catch (Exception e) {
               Log.error("remove warp fail, id: " + warpId, e);
           } finally {
               Log.debug(Level.INFO, "remove warp time: " + (System.currentTimeMillis() - start) + "ms");
           }
        });
    }

    @Override
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
}
