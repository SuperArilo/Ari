package com.tty.gui;

import com.tty.entity.menu.BaseDataMenu;
import com.tty.lib.Log;
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
        this.requestData().thenAccept(list -> {
            this.data = list;
            this.renderDataItem();
        }).exceptionally(i -> {
            Log.error(i, "%s: request data error!", this.holder.type().name());
            return null;
        });
    }

    /**
     * 上一页
     */
    public void prev() {
        this.pageNum--;
        if(this.pageNum <= 0) {
            this.player.sendMessage(ConfigUtils.t("base.page-change.none-prev"));
            this.pageNum = 1;
            return;
        }
        this.requestData().thenAccept(list -> {
            this.data = list;
            this.renderDataItem();
        }).exceptionally(i -> {
            Log.error(i, "%s: request data error!", this.holder.type().name());
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
                this.player.sendMessage(ConfigUtils.t("base.page-change.none-next"));
                this.pageNum--;
            } else {
                this.data = list;
                this.renderDataItem();
            }
        }).exceptionally(i -> {
            Log.error( i, "%s: request data error!", this.holder.type().name());
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
        long l = System.currentTimeMillis();
        Map<Integer, ItemStack> renderItem = this.getRenderItem();
        if (renderItem == null || renderItem.isEmpty()) return;

        for (Integer index : this.baseDataInstance.getDataItems().getSlot()) {
            this.clearItem(index);
            if(renderItem.size() != index + 1) {
                this.setItem(index, renderItem.get(index));
            }
        }
        Log.debug("%s: render data items: %sms", this.holder.type().name(), (System.currentTimeMillis() - l));
    }

}
