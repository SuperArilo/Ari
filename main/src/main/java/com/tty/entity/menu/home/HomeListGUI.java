package com.tty.entity.menu.home;

import com.tty.entity.menu.BaseMenu;
import com.tty.entity.menu.DataItems;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class HomeListGUI extends BaseMenu {
    private DataItems dataItems;
}

