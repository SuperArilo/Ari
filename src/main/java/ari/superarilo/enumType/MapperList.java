package ari.superarilo.enumType;

import ari.superarilo.mapper.PlayerHomeMapper;

public enum MapperList {
    PLAYERHOME(PlayerHomeMapper.class);
    private final Class<?> clazz;
    MapperList(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
