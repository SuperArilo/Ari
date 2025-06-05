package com.tty.function;

import com.tty.Ari;
import com.tty.dto.Page;
import com.tty.entity.sql.ServerHome;
import com.tty.enumType.FilePath;
import com.tty.function.impl.BaseFunctionImpl;
import com.tty.mapper.PlayerHomeMapper;
import com.tty.tool.Log;
import com.tty.tool.SQLInstance;
import com.tty.tool.TextTool;
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

public class HomeManager extends BaseFunctionImpl implements BaseManager<ServerHome> {

    private final String playerUUID;
    private final Location location;

    private HomeManager(String playerUUID) {
        this.playerUUID = playerUUID;
        Player player = Bukkit.getPlayer(UUID.fromString(playerUUID));
        this.location = Objects.requireNonNull(player).getLocation();
    }

    @Override
    public CompletableFuture<List<ServerHome>> asyncGetList(int pageNum, int pageSize) {
        long start = System.currentTimeMillis();
        CompletableFuture<List<ServerHome>> future = new CompletableFuture<>();
        Bukkit.getAsyncScheduler().runNow(Ari.instance, i -> {
            try(SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)) {
                PlayerHomeMapper mapper = sqlSession.getMapper(PlayerHomeMapper.class);
                future.complete(mapper.getHomeList(this.playerUUID, Page.create(pageNum, pageSize)));
            } catch (Exception e) {
                Log.error("query home error!", e);
                future.complete(List.of());
            } finally {
                Log.debug(Level.INFO, "get home list time: " + (System.currentTimeMillis() - start) + "ms");
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
                future.complete(sqlSession.getMapper(PlayerHomeMapper.class).getHomeIdList(this.playerUUID));
            } catch (Exception e) {
                Log.error("SqlSession error", e);
            } finally {
                Log.debug(Level.INFO, "query homeId list time: " + (System.currentTimeMillis() - start) + "ms");
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<ServerHome> asyncGetInstance(String id) {
        CompletableFuture<ServerHome> future = new CompletableFuture<>();
        Bukkit.getAsyncScheduler().runNow(Ari.instance, i -> {
           try (SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)) {
               future.complete(sqlSession.getMapper(PlayerHomeMapper.class).getHome(id, this.playerUUID));
           } catch (Exception e) {
               future.completeExceptionally(e);
               Log.error("SqlSession error", e);
           }
        });
        return future;
    }


    @Override
    public void createInstance(String homeId) {
        Player player = Bukkit.getPlayer(UUID.fromString(this.playerUUID));
        if(player == null) {
            Log.error("player: " + this.playerUUID + "is not online");
            return;
        }
        Material material = this.checkIsItem(location.getBlock().getRelative(BlockFace.DOWN).getType());
        Bukkit.getAsyncScheduler().runNow(Ari.instance, i -> {
            long start = System.currentTimeMillis();
            try (SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)) {
                PlayerHomeMapper mapper = sqlSession.getMapper(PlayerHomeMapper.class);
                List<String> homeIdList = mapper.getHomeIdList(player.getUniqueId().toString());
                boolean hasPermission = Ari.instance.permissionUtils.hasPermission(player, "ari.count.home." + homeIdList.size() + 1) || player.isOp();
                if(!hasPermission) {
                    Log.debug("Exceeds the specified quantity");
                    player.sendMessage(TextTool.setHEXColorText("function.home.exceeds", FilePath.Lang));
                    i.cancel();
                    return;
                }
                if (homeIdList.contains(homeId)) {
                    player.sendMessage(TextTool.setHEXColorText("function.home.exist", FilePath.Lang, player));
                    i.cancel();
                    return;
                }
                ServerHome serverHome = new ServerHome();
                serverHome.setHomeId(homeId);
                serverHome.setHomeName(homeId);
                serverHome.setPlayerUUID(this.playerUUID);
                serverHome.setLocation(this.location.toString());
                serverHome.setShowMaterial(material.name());

                mapper.save(serverHome);
                player.sendMessage(TextTool.setHEXColorText("function.home.create-success", FilePath.Lang, player));
            } catch (Exception e) {
                Log.error(e.getMessage());
                i.cancel();
            } finally {
                Log.debug(Level.INFO, "save home done. time: " + (System.currentTimeMillis() - start) + "ms");
            }
        });
    }

    @Override
    public void deleteInstance(String homeId) {
        Player player = Bukkit.getPlayer(UUID.fromString(this.playerUUID));
        if(player == null) {
            Log.error("player: " + this.playerUUID + "is not online");
            return;
        }
        long start = System.currentTimeMillis();
        Bukkit.getAsyncScheduler().runNow(Ari.instance, i -> {
            try(SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)) {
                Integer delete = sqlSession.getMapper(PlayerHomeMapper.class).delete(homeId, player.getUniqueId().toString());
                if(delete == 1){
                    player.sendMessage(TextTool.setHEXColorText("function.home.delete-success", FilePath.Lang));
                } else {
                    player.sendMessage(TextTool.setHEXColorText("function.home.not-found", FilePath.Lang));
                }
            } catch (Exception e) {
                Log.error("remove home fail, id: " + homeId, e);
            } finally {
                Log.debug(Level.INFO, "remove home time: " + (System.currentTimeMillis() - start) + "ms");
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> modify(ServerHome instance) {
        long start = System.currentTimeMillis();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Bukkit.getAsyncScheduler().runNow(Ari.instance, i -> {
            try(SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)) {
                boolean update = sqlSession.getMapper(PlayerHomeMapper.class).update(instance);
                if(update) {
                    future.complete(true);
                } else {
                    future.complete(false);
                    Log.error("save homeId: [" + instance.getHomeId() + "] error");
                }
            } catch (Exception e) {
                Log.error("save home error", e);
                future.complete(false);
            } finally {
                Log.debug("save time: " + (System.currentTimeMillis() - start) + "ms");
            }
        });
        return future;
    }

    public static HomeManager create(String playerUUID) {
        return new HomeManager(playerUUID);
    }
}
