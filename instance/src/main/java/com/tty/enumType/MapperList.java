package com.tty.enumType;

import com.tty.mapper.CreateTable;
import com.tty.mapper.PlayerHomeMapper;
import com.tty.mapper.PlayerMapper;
import com.tty.mapper.ServerWrapMapper;
import lombok.Getter;

@Getter
public enum MapperList {
    CREATETABLE(CreateTable.class),
    PLAYERHOME(PlayerHomeMapper.class),
    WARPS(ServerWrapMapper.class),
    PLAYER(PlayerMapper.class);
    private final Class<?> clazz;
    MapperList(Class<?> clazz) {
        this.clazz = clazz;
    }

}
