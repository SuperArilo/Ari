package ari.superarilo.mapper;

import ari.superarilo.dto.Page;
import ari.superarilo.entity.sql.ServerWarp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ServerWrapMapper {
    void createWarp(@Param("warp") ServerWarp warp);
    List<ServerWarp> getServerWarps(@Param("page") Page page);
}
