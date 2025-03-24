package ari.superarilo.gui;

import ari.superarilo.function.PageChange;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.List;

@Setter
@Getter
public abstract class BasePageGui<T> extends BaseGui implements PageChange {

    protected int pageNum = 1;
    protected int pageSize = 10;
    public T data;

    public BasePageGui(Player player, int pageNum, int pageSize) {
        super(player);
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    @Override
    public void init() {
        this.renderDataItem();
    }

    public BasePageGui(Player player) {
        super(player);
    }
    /**
     * 渲染gui中带数据的item
     */
    public abstract void renderDataItem();

    /**
     * 请求相应的数据
     * @return 数据
     */
    public abstract T requestData();
    /**
     * 清除指定位置已经渲染的item
     * @param list 指定位置列表
     */
    protected void cleanRenderDataItem(List<Integer> list) {
        if(this.inventory == null) return;
        list.forEach(i -> this.inventory.clear(i));
    }
}
