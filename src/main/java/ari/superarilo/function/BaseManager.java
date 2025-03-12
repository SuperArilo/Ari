package ari.superarilo.function;


import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BaseManager<T> {

    /**
     * 异步获取保存的列表
     * @param pageNum 页数
     * @param pageSize 每页的数量
     * @return 列表数组
     */

    CompletableFuture<List<T>> asyncGetList(int pageNum, int pageSize);

    CompletableFuture<List<String>> asyncGetIdList();

    void createInstance(String id);

    void deleteInstance(String id);
    /**
     * 修改信息
     * @param instance 被修改的对象
     * @return 修改成功状态。true：成功，false：失败
     */
    CompletableFuture<Boolean> modify(T instance);
}
