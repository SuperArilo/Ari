package com.tty.gui;

import com.tty.function.PageChange;
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
}
