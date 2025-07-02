package com.tty.function;

import com.tty.entity.sql.ServerSpawn;
import com.tty.lib.dto.Page;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SpawnManager implements BaseManager<ServerSpawn>{

    @Override
    public CompletableFuture<List<ServerSpawn>> asyncGetList(Page page) {
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
