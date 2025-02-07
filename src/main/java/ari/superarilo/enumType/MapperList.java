package ari.superarilo.enumType;

import ari.superarilo.mapper.CreateTable;
import ari.superarilo.mapper.PlayerHomeMapper;
import ari.superarilo.mapper.ServerWrapMapper;

public enum MapperList {
    CREATETABLE(CreateTable.class),
    PLAYERHOME(PlayerHomeMapper.class),
    WARPS(ServerWrapMapper.class);
    private final Class<?> clazz;
    MapperList(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
