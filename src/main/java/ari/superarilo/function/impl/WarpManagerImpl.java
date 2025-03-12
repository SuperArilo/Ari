package ari.superarilo.function.impl;

import ari.superarilo.Ari;
import ari.superarilo.dto.Page;
import ari.superarilo.entity.sql.ServerWarp;
import ari.superarilo.function.WarpManager;
import ari.superarilo.mapper.ServerWrapMapper;
import ari.superarilo.tool.Log;
import ari.superarilo.tool.SQLInstance;
import org.apache.ibatis.session.SqlSession;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class WarpManagerImpl implements WarpManager {

    private final Player player;

    public WarpManagerImpl(Player player) {
        this.player = player;
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
        return null;
    }

    @Override
    public void createInstance(String id) {

    }

    @Override
    public void deleteInstance(String id) {

    }

    @Override
    public CompletableFuture<Boolean> modify(ServerWarp instance) {
        return null;
    }
}
