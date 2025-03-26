package ari.superarilo.mapper;


public interface BaseMapper<T> {
    void save(T entity);
    void update(T entity);
    T selectOne(String id);
}
