package com.tty.mapper;


public interface BaseMapper<T> {
    void save(T entity);
    boolean update(T entity);
    T selectOne(String id);
}
