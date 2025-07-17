package com.tty.gui;

import com.tty.entity.menu.BaseDataMenu;
import com.tty.enumType.FilePath;
import com.tty.lib.tool.ComponentUtils;
import com.tty.lib.tool.Log;
import com.tty.tool.ConfigUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class BaseDataItemInventory<T> extends BaseInventory {

    protected int pageNum = 1;
    protected final int pageSize;
    public final BaseDataMenu baseDataInstance;
    public List<T> data;

    public BaseDataItemInventory(BaseDataMenu baseDataInstance, Player player) {
        super(baseDataInstance, player);
        this.baseDataInstance = baseDataInstance;
        this.pageSize = baseDataInstance.getDataItems().getSlot().size();
        CompletableFuture<List<T>> future = this.requestData();
        if (future == null) return;
        future.thenAccept(list -> {
            long l = System.currentTimeMillis();
            this.data = list;
            this.renderDataItem();
            Log.debug("render gui time: " + (System.currentTimeMillis() - l) + "ms");
        }).exceptionally(i -> {
            Log.error("request data error", i);
            return null;
        });
    }

    /**
     * 上一页
     */
    public void prev() {
        this.pageNum--;
        if(this.pageNum <= 0) {
            this.player.sendMessage(ComponentUtils.text(ConfigUtils.getValue("base.page-change.none-prev", FilePath.Lang)));
            this.pageNum = 1;
            return;
        }
        this.requestData().thenAccept(list -> {
            this.data = list;
            this.renderDataItem();
        }).exceptionally(i -> {
            Log.error("request data error", i);
            return null;
        });
    }

    /**
     * 下一页
     */
    public void next() {
        this.pageNum++;
        this.requestData().thenAccept(list -> {
            if (list.isEmpty()) {
                this.player.sendMessage(ComponentUtils.text(ConfigUtils.getValue("base.page-change.none-next", FilePath.Lang)));
                this.pageNum--;
            } else {
                this.data = list;
                this.renderDataItem();
            }
        }).exceptionally(i -> {
            Log.error("request data error", i);
            return null;
        });
    }

    /**
     * 请求数据的方法
     * @return 返回数据 CompletableFuture
     */
    protected abstract CompletableFuture<List<T>> requestData();
    protected abstract Map<Integer, ItemStack> getRenderItem();

    private void renderDataItem() {
        Map<Integer, ItemStack> renderItem = this.getRenderItem();
        if (renderItem == null || renderItem.isEmpty()) return;

        for (Integer index : this.baseDataInstance.getDataItems().getSlot()) {
            this.clearItem(index);
        }
        renderItem.forEach((k, v) -> {
            if (v == null) return;
            this.setItem(k, v);
        });
    }

}
