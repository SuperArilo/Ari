package ari.superarilo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CreateTable {
    void createPlayers(@Param("sqlType")String sqlType);
    void createHomeList(@Param("sqlType")String sqlType);
}
