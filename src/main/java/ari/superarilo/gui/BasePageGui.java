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
    public List<T> data;

    public BasePageGui(Player player) {
        super(player);
    }

    public BasePageGui(Player player, int pageNum, int pageSize) {
        super(player);
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    /**
     * 渲染gui中带数据的item
     */
    protected abstract void renderDataItem();

    /**
     * 指定位置更新当前的GUI
     * @param slots 更新位置
     */
    public void updateGui(List<Integer> slots) {
        this.cleanRenderDataItem(slots);
        this.renderDataItem();
    }

    /**
     * 清除指定位置已经渲染的item
     * @param list 指定位置列表
     */
    protected void cleanRenderDataItem(List<Integer> list) {
        if(this.inventory == null) return;
        list.forEach(i -> this.inventory.clear(i));
    }
}
