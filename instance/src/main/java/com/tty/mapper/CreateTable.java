package com.tty.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CreateTable {
    void createPlayers();
    void createHomeList();
    void createWarpList();
}
