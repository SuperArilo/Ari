package ari.superarilo.mapper;

import ari.superarilo.entity.sql.ServerPlayer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PlayerMapper extends BaseMapper<ServerPlayer> {
    @Override
    void save(@Param("serverPlayer") ServerPlayer entity);

    @Override
    void update(@Param("serverPlayer") ServerPlayer entity);

    @Override
    ServerPlayer selectOne(@Param("playerUUID") String id);
}
