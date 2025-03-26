package ari.superarilo.function;

import ari.superarilo.Ari;
import ari.superarilo.entity.sql.ServerPlayer;
import ari.superarilo.mapper.PlayerMapper;
import ari.superarilo.tool.Log;
import ari.superarilo.tool.SQLInstance;
import org.apache.ibatis.session.SqlSession;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PlayerManager implements BaseManager<ServerPlayer> {

    private static final Logger log = LoggerFactory.getLogger(PlayerManager.class);
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
        Bukkit.getAsyncScheduler().runNow(Ari.instance, i -> {
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
        newPlayer.setTotalOnlineTime(0L);
        newPlayer.setNamePrefix("");
        newPlayer.setNameSuffix("");
        Bukkit.getAsyncScheduler().runNow(Ari.instance, i -> {
           try (SqlSession sqlSession = SQLInstance.sessionFactory.openSession(true)) {
               sqlSession.getMapper(PlayerMapper.class).save(newPlayer);
           } catch (Exception e) {
               i.cancel();
               Log.error("error", e);
           }
        });
    }

    @Override
    public void deleteInstance(String id) {

    }

    @Override
    public CompletableFuture<Boolean> modify(ServerPlayer instance) {
        return null;
    }

    public static PlayerManager build(Player player) {
        return new PlayerManager(player);
    }

    public static PlayerManager build(OfflinePlayer offlinePlayer) {
        return new PlayerManager(offlinePlayer);
    }
}
