package com.tty.function;

import com.tty.entity.sql.ServerSpawn;
import com.tty.lib.dto.Page;
import com.tty.lib.dto.SqlKey;
import com.tty.lib.dto.SqlOrderByKey;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SpawnManager implements BaseManager<ServerSpawn>{


    @Override
    public CompletableFuture<List<ServerSpawn>> asyncGetList(Page page, List<SqlKey> sqlKeys, List<SqlOrderByKey> orderByKeys) {
        return null;
    }

    @Override
    public CompletableFuture<ServerSpawn> asyncGetInstance(List<SqlKey> sqlKeys) {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> createInstance(ServerSpawn instance) {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> deleteInstance(ServerSpawn instance) {
        return null;
    }


    @Override
    public CompletableFuture<Boolean> modify(ServerSpawn instance) {
        return null;
    }
}
