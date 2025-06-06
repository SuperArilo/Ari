package com.tty.mapper;

import com.tty.entity.sql.ServerPlayer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PlayerMapper extends BaseMapper<ServerPlayer> {
    @Override
    void save(@Param("serverPlayer") ServerPlayer entity);

    @Override
    boolean update(@Param("serverPlayer") ServerPlayer entity);

    @Override
    ServerPlayer selectOne(@Param("playerUUID") String id);
}
