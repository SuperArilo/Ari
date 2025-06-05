package com.tty.mapper;

import com.tty.dto.Page;
import com.tty.entity.sql.ServerHome;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlayerHomeMapper extends BaseMapper<ServerHome>  {
    List<ServerHome> getHomeList(@Param("playerUUID") String playerUUID,
                                 @Param("page") Page page);
    List<String> getHomeIdList(@Param("playerUUID") String playerUUID);
    ServerHome getHome(@Param("home_id") String homeId, @Param("playerUUID") String playerUUID);
    boolean exist(@Param("home_id") String homeId, @Param("playerUUID") String playerUUID);
    @Override
    void save(@Param("serverHome") ServerHome serverHome);
    Integer delete(@Param("home_id") String homeId, @Param("playerUUID") String playerUUID);
    @Override
    boolean update(@Param("serverHome") ServerHome serverHome);
}
