package com.tty.gui;

import com.tty.enumType.FilePath;
import com.tty.lib.tool.Log;
import com.tty.tool.TextTool;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Setter
@Getter
public abstract class BasePageGui<T> extends BaseGui {

    protected int pageNum = 1;
    protected int pageSize = 10;
    public List<T> data;

    public BasePageGui(Player player) {
        super(player);
        this.init();
        this.requestData().thenAccept(list -> {
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
            this.player.sendMessage(TextTool.setHEXColorText("base.page-change.none-prev", FilePath.Lang));
            this.pageNum = 1;
            return;
        }
        this.requestData().thenAccept(list -> {
            this.data = list;
            this.updateGui();
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
               this.player.sendMessage(TextTool.setHEXColorText("base.page-change.none-next", FilePath.Lang));
               this.pageNum--;
           } else {
               this.data = list;
               this.updateGui();
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
    public abstract CompletableFuture<List<T>> requestData();

    /**
     * 开始请求数据之前的gui配置初始化
     */
    protected abstract void init();

    /**
     * 渲染gui中带数据的item
     */
    protected abstract void renderDataItem();

}
