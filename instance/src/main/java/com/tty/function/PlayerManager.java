package com.tty.function;

import com.tty.Ari;
import com.tty.entity.sql.ServerPlayer;
import com.tty.lib.Lib;
import com.tty.mapper.PlayerMapper;
import com.tty.tool.Log;
import com.tty.tool.SQLInstance;
import org.apache.ibatis.session.SqlSession;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PlayerManager implements BaseManager<ServerPlayer> {

    private final Player player;
    private final OfflinePlayer offlinePlayer;

    private PlayerManager(Player player) {
        this.player = player;
        this.offlinePlayer = null;
    }

    private PlayerManager(OfflinePlayer offlinePlayer) {
        this.offlinePlayer = offlinePlayer;
        this.player = null;
    }

    @Override
    public CompletableFuture<List<ServerPlayer>> asyncGetList(int pageNum, int pageSize) {
        return null;
    }

    @Override
    public CompletableFuture<List<String>> asyncGetIdList() {
        return null;
    }

    @Override
    public CompletableFuture<ServerPlayer> asyncGetInstance(String id) {
        CompletableFuture<ServerPlayer> future = new CompletableFuture<>();
        Lib.Scheduler.runAsync(Ari.instance, i -> {
            try (SqlSession sqlSession = SQLInstance.sessionFactory.openSession()) {
                future.complete(sqlSession.getMapper(PlayerMapper.class).selectOne(id));
            } catch (Exception e) {
                future.complete(null);
                Log.error("error", e);
            }
        });
        return future;
    }

    @Override
    public void createInstance(String id) {
        ServerPlayer newPlayer = new ServerPlayer();
        long time = System.currentTimeMillis();
        newPlayer.setPlayerUUID(id);
        assert player != null;
        newPlayer.setPlayerName(player.getName());
        newPlayer.setFirstLoginTime(time);
        newPlayer.setLastLoginOffTime(time);
        Lib.Scheduler.runAsync(Ari.instance, i -> {
            try (SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)) {
                sqlSession.getMapper(PlayerMapper.class).save(newPlayer);
            } catch (Exception e) {
                i.cancel();
                Log.error("error", e);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteInstance(String id) {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> modify(ServerPlayer instance) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Lib.Scheduler.runAsync(Ari.instance, i -> {
            try (SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)) {
                future.complete(sqlSession.getMapper(PlayerMapper.class).update(instance));
            } catch (Exception e) {
                future.complete(false);
                Log.error("error", e);
            }
        });
        return future;
    }

    public static PlayerManager build(Player player) {
        return new PlayerManager(player);
    }

    public static PlayerManager build(OfflinePlayer offlinePlayer) {
        return new PlayerManager(offlinePlayer);
    }
}
