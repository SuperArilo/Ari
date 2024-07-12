package ari.superarilo.mapper;

import ari.superarilo.entity.sql.PlayerHome;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlayerHomeMapper  {
    List<PlayerHome> getHomeList(@Param("playerUUID") String playerUUID, @Param("serverName") String serverName);
}
