package com.tty.entity.menu.warp;

import com.tty.entity.menu.BaseMenu;
import com.tty.entity.menu.DataItems;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class WarpListGUI extends BaseMenu {
    private DataItems dataItems;
}
