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
    public CompletableFuture<List<PlayerHome>> asyncGetList(int pageNum, int pageSize) {
        long start = System.currentTimeMillis();
        CompletableFuture<List<PlayerHome>> future = new CompletableFuture<>();
        Bukkit.getAsyncScheduler().runNow(Ari.instance, i -> {
            try(SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)) {
                PlayerHomeMapper mapper = sqlSession.getMapper(PlayerHomeMapper.class);
                future.complete(mapper.getHomeList(this.player.getUniqueId().toString(), Page.create(pageNum, pageSize)));
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
                future.complete(sqlSession.getMapper(PlayerHomeMapper.class).getHomeIdList(this.player.getUniqueId().toString()));
            } catch (Exception e) {
                Log.error("SqlSession error", e);
            } finally {
                Log.debug(Level.INFO, "query homeId list time: " + (System.currentTimeMillis() - start) + "ms");
            }
        });
        return future;
    }


    @Override
    public void createInstance(String homeId) {
        Material material = this.checkIsItem(location.getBlock().getRelative(BlockFace.DOWN).getType());
        Bukkit.getAsyncScheduler().runNow(Ari.instance, i -> {
            long start = System.currentTimeMillis();
            try (SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)) {
                PlayerHomeMapper mapper = sqlSession.getMapper(PlayerHomeMapper.class);
                List<String> homeIdList = mapper.getHomeIdList(this.player.getUniqueId().toString());
                Integer value = Ari.instance.configManager.getValue("main.set-home.quantity." + Ari.instance.permissionUtils.getPlayerGroup(this.player), FilePath.HomeConfig, Integer.class);
                if(homeIdList.size() >= value && value != -1) {
                    Log.debug("Exceeds the specified quantity");
                    this.player.sendMessage(TextTool.setHEXColorText("command.sethome.exceeds", FilePath.Lang));
                    i.cancel();
                    return;
                }
                if (homeIdList.contains(homeId)) {
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
            } catch (Exception e) {
                Log.error(e.getMessage());
                i.cancel();
            } finally {
                Log.debug(Level.INFO, "save home done. time: " + (System.currentTimeMillis() - start));
            }
        });
    }

    @Override
    public void deleteInstance(String homeId) {
        long start = System.currentTimeMillis();
        Bukkit.getAsyncScheduler().runNow(Ari.instance, i -> {
            try(SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)) {
                Integer delete = sqlSession.getMapper(PlayerHomeMapper.class).delete(homeId, this.player.getUniqueId().toString());
                if(delete == 1){
                    this.player.sendMessage(TextTool.setHEXColorText("command.deletehome.success", FilePath.Lang));
                } else {
                    this.player.sendMessage(TextTool.setHEXColorText("command.deletehome.none", FilePath.Lang));
                }
            } catch (Exception e) {
                Log.error("remove home fail, id: " + homeId, e);
            } finally {
                Log.debug(Level.INFO, "remove home time: " + (System.currentTimeMillis() - start) + "ms");
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> modify(PlayerHome instance) {
        long start = System.currentTimeMillis();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Bukkit.getAsyncScheduler().runNow(Ari.instance, i -> {
            try(SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)) {
                Integer update = sqlSession.getMapper(PlayerHomeMapper.class).update(instance);
                if(update == 1) {
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

}
