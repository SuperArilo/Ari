package com.tty.mapper;

import com.tty.dto.Page;
import com.tty.entity.sql.ServerWarp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ServerWrapMapper {
    void save(@Param("warp") ServerWarp warp);
    ServerWarp getWarp(@Param("warpId") String warpId, @Param("playerUUID") String playerUUID);
    List<ServerWarp> getServerWarps(@Param("page") Page page);
    List<String> getWarpIdList(@Param("playerUUID") String playerUUID);
    Integer delete(@Param("warpId") String warpId, @Param("playerUUID") String playerUUID);
    Integer update(@Param("serverWarp") ServerWarp serverWarp);
}
