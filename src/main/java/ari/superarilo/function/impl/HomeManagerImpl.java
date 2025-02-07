package ari.superarilo.function.impl;

import ari.superarilo.Ari;
import ari.superarilo.dto.Page;
import ari.superarilo.entity.sql.PlayerHome;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.function.HomeManager;
import ari.superarilo.mapper.PlayerHomeMapper;
import ari.superarilo.tool.Log;
import ari.superarilo.tool.SQLInstance;
import ari.superarilo.tool.TextTool;
import org.apache.ibatis.session.SqlSession;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class HomeManagerImpl extends BaseFunctionImpl implements HomeManager {

    private final Player player;
    private final Location location;

    public HomeManagerImpl(Player player) {
        this.player = player;
        this.location = player.getLocation();
    }

    @Override
    public List<PlayerHome> asyncGetHomeList(int pageNum, int pageSize) {
        long start = System.currentTimeMillis();
        CompletableFuture<List<PlayerHome>> future = new CompletableFuture<>();
        Bukkit.getAsyncScheduler().runNow(Ari.instance, i -> {
            try(SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)) {
                PlayerHomeMapper mapper = sqlSession.getMapper(PlayerHomeMapper.class);
                future.complete(
                        mapper.getHomeList(
                                this.player.getUniqueId().toString(),
                                Page.create(pageNum, pageSize)));
            } catch (Exception e) {
                Log.error("query home error!", e);
                future.complete(List.of());
            }
        });
        try {
            Log.debug(Level.INFO, "get home list time: " + (System.currentTimeMillis() - start) + "ms");
            return future.get();
        } catch (Exception e) {
            Log.debug(Level.INFO, "get home list error", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<PlayerHome> asyncGetHomeList() {
        long start = System.currentTimeMillis();
        CompletableFuture<List<PlayerHome>> future = new CompletableFuture<>();
        Bukkit.getAsyncScheduler().runNow(Ari.instance, i -> {
            try(SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)) {
                PlayerHomeMapper mapper = sqlSession.getMapper(PlayerHomeMapper.class);
                future.complete(mapper.getHomeList(this.player.getUniqueId().toString(), null));
            } catch (Exception e) {
                Log.error("SqlSession error", e);
            }
        });
        try {
            Log.debug(Level.INFO, "get home list time: " + (System.currentTimeMillis() - start) + "ms");
            return future.get();
        } catch (Exception e) {
            Log.debug(Level.INFO, "get home list error", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<String> asyncGetHomeIdList() {
        long start = System.currentTimeMillis();
        CompletableFuture<List<String>> future = new CompletableFuture<>();
        Bukkit.getAsyncScheduler().runNow(Ari.instance, i -> {
           try (SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)) {
               future.complete(sqlSession.getMapper(PlayerHomeMapper.class).getHomeIdList(this.player.getUniqueId().toString()));
           } catch (Exception e) {
               Log.error("SqlSession error", e);
           }
        });
        try {
            Log.debug(Level.INFO, "query homeId list time: " + (System.currentTimeMillis() - start) + "ms");
            return future.get();
        } catch (Exception e) {
            Log.debug(Level.INFO, "query homeId list error", e);
            return new ArrayList<>();
        }
    }

    @Override
    public void createNewHome(String homeId) {
        Material material = this.checkIsItem(location.getBlock().getRelative(BlockFace.DOWN).getType());
        Bukkit.getAsyncScheduler().runNow(Ari.instance, i -> {
            long start = System.currentTimeMillis();
            try (SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)) {
                PlayerHomeMapper mapper = sqlSession.getMapper(PlayerHomeMapper.class);
                int size = mapper.getHomeList(String.valueOf(this.player.getUniqueId()), Page.create(1, Integer.MAX_VALUE)).size();
                Integer value = Ari.instance.configManager.getValue("main.set-home.quantity." + Ari.instance.permissionUtils.getPlayerGroup(this.player), FilePath.HomeConfig, Integer.class);
                if(size >= value && value != -1) {
                    Log.debug("Exceeds the specified quantity");
                    this.player.sendMessage(TextTool.setHEXColorText("command.sethome.exceeds", FilePath.Lang));
                    return;
                }
                if (mapper.exist(homeId, this.player.getUniqueId().toString())) {
                    this.player.sendMessage(TextTool.setHEXColorText("command.sethome.exist", FilePath.Lang, this.player));
                    i.cancel();
                    return;
                }
                PlayerHome playerHome = new PlayerHome();
                playerHome.setHomeId(homeId);
                playerHome.setHomeName(homeId);
                playerHome.setPlayerUUID(this.player.getUniqueId().toString());
                playerHome.setLocation(this.location.toString());
                playerHome.setShowMaterial(material.name());

                mapper.save(playerHome);
                this.player.sendMessage(TextTool.setHEXColorText("command.sethome.success", FilePath.Lang, this.player));
                Log.debug(Level.INFO, "save home done. time: " + (System.currentTimeMillis() - start));
            } catch (Exception e) {
                Log.error(e.getMessage());
            }
        });
    }
    @Override
    public void deleteHome(String homeId) {
        long start = System.currentTimeMillis();
        Bukkit.getAsyncScheduler().runNow(Ari.instance, i -> {
            try(SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)) {
                Integer delete = sqlSession.getMapper(PlayerHomeMapper.class).delete(homeId, this.player.getUniqueId().toString());
                if(delete == 1){
                    this.player.sendMessage(TextTool.setHEXColorText("command.deletehome.success", FilePath.Lang));
                } else {
                    this.player.sendMessage(TextTool.setHEXColorText("command.deletehome.error", FilePath.Lang));
                }
                Log.debug(Level.INFO, "remove home time: " + (System.currentTimeMillis() - start) + "ms");
            } catch (Exception e) {
                Log.error("remove home fail, id: " + homeId, e);
            }
        });
    }

    @Override
    public boolean modifyHome(PlayerHome modify) {
        long start = System.currentTimeMillis();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Bukkit.getAsyncScheduler().runNow(Ari.instance, i -> {
            try(SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)) {
                Integer update = sqlSession.getMapper(PlayerHomeMapper.class).update(modify);
                if(update == 1) {
                    Log.debug("save time: " + (System.currentTimeMillis() - start) + "ms");
                    future.complete(true);
                } else {
                    future.complete(false);
                    Log.error("save homeId: [" + modify.getHomeId() + "] error");
                }
            } catch (Exception e) {
                Log.error("save home error", e);
                future.complete(false);
            }
        });
        try {
            return future.get();
        } catch (Exception e) {
            Log.error("get completableFuture error", e);
            return false;
        }
    }
}
