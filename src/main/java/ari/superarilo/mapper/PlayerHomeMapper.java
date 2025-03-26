package ari.superarilo.mapper;

import ari.superarilo.dto.Page;
import ari.superarilo.entity.sql.ServerHome;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlayerHomeMapper  {
    List<ServerHome> getHomeList(@Param("playerUUID") String playerUUID,
                                 @Param("page") Page page);
    List<String> getHomeIdList(@Param("playerUUID") String playerUUID);
    ServerHome getHome(@Param("home_id") String homeId, @Param("playerUUID") String playerUUID);
    boolean exist(@Param("home_id") String homeId, @Param("playerUUID") String playerUUID);
    void save(@Param("serverHome") ServerHome serverHome);
    Integer delete(@Param("home_id") String homeId, @Param("playerUUID") String playerUUID);
    Integer update(@Param("serverHome") ServerHome serverHome);
}
