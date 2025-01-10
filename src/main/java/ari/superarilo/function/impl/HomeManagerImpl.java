package ari.superarilo.function.impl;

import ari.superarilo.Ari;
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
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class HomeManagerImpl implements HomeManager {

    private final Player player;
    private final Location location;

    public HomeManagerImpl(Player player) {
        this.player = player;
        this.location = player.getLocation();
    }

    @Override
    public List<PlayerHome> asyncGetHomeList() {
        long start = System.currentTimeMillis();
        CompletableFuture<List<PlayerHome>> future = new CompletableFuture<>();
        Bukkit.getAsyncScheduler().runNow(Ari.instance, i -> {
            try(SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)) {
                future.complete(sqlSession.getMapper(PlayerHomeMapper.class).getHomeList(this.player.getUniqueId().toString()));
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
    public void createNewHome(String homeId) {
        Material material = location.getBlock().getRelative(BlockFace.DOWN).getType();
        Bukkit.getAsyncScheduler().runNow(Ari.instance, i -> {
            long start = System.currentTimeMillis();
            try (SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)) {
                PlayerHomeMapper mapper = sqlSession.getMapper(PlayerHomeMapper.class);
                if (mapper.exist(homeId)) {
                    this.player.sendMessage(TextTool.setHEXColorText("command.sethome.exist", FilePath.Lang, this.player));
                    i.cancel();
                    return;
                }
                PlayerHome playerHome = new PlayerHome();
                playerHome.setHomeId(homeId);
                playerHome.setHomeName(homeId);
                playerHome.setPlayerUUID(player.getUniqueId().toString());
                playerHome.setX(Double.valueOf(Ari.instance.numberFormatUtil.format_2(location.getX())));
                playerHome.setY(Double.valueOf(Ari.instance.numberFormatUtil.format_2(location.getY())));
                playerHome.setZ(Double.valueOf(Ari.instance.numberFormatUtil.format_2(location.getZ())));
                playerHome.setWorld(player.getWorld().getName());
                playerHome.setShowMaterial(material.name());

                mapper.save(playerHome);
                this.player.sendMessage(TextTool.setHEXColorText("command.sethome.success", FilePath.Lang, this.player));
                Log.debug(Level.INFO, "save home done. time: " + (System.currentTimeMillis() - start));
            } catch (Exception e) {
                Log.debug(Level.INFO, "create SqlSession error on get home lists", e);
            }
        });
    }
    @Override
    public Integer deleteHome(String homeId) {
        long start = System.currentTimeMillis();
        try(SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)) {
            Integer deleteStatus = sqlSession.getMapper(PlayerHomeMapper.class).delete(homeId);
            Log.debug(Level.INFO, "remove home time: " + (System.currentTimeMillis() - start) + "ms");
            return deleteStatus;
        } catch (Exception e) {
            Log.error("remove home fail, id: " + homeId, e);
            return null;
        }
    }

    @Override
    public boolean modifyHome(PlayerHome modify) {
        long start = System.currentTimeMillis();
        try(SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)) {
            sqlSession.getMapper(PlayerHomeMapper.class).update(modify);
            Log.debug("save time: " + (System.currentTimeMillis() - start) + "ms");
            return true;
        } catch (Exception e) {
            Log.error("save home error", e);
            return false;
        }
    }
}
