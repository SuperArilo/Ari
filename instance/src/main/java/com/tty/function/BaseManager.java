package com.tty.function;


import com.tty.lib.dto.Page;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BaseManager<T> {

    /**
     * 异步获取保存的列表
     * @param page 分页对象
     * @return 列表数组
     */

    CompletableFuture<List<T>> asyncGetList(Page page);

    CompletableFuture<Boolean> createInstance(T instance);

    CompletableFuture<Boolean> deleteInstance(T instance);
    /**
     * 修改信息
     * @param instance 被修改的对象
     * @return 修改成功状态。true：成功，false：失败
     */
    CompletableFuture<Boolean> modify(T instance);

}
