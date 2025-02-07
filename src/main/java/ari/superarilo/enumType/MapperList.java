package ari.superarilo.enumType;

import ari.superarilo.mapper.CreateTable;
import ari.superarilo.mapper.PlayerHomeMapper;

public enum MapperList {
    CREATETABLE(CreateTable.class),
    PLAYERHOME(PlayerHomeMapper.class);
    private final Class<?> clazz;
    MapperList(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
