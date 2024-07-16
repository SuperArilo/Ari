package ari.superarilo.mapper;

import ari.superarilo.entity.sql.PlayerHome;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlayerHomeMapper  {
    List<PlayerHome> getHomeList(@Param("playerUUID") String playerUUID);
    PlayerHome getHome(@Param("home_id") String homeId);
    boolean exist(@Param("home_id") String homeId);
    void save(@Param("playerhome") PlayerHome playerHome);
}
