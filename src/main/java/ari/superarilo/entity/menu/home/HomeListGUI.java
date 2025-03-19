package ari.superarilo.entity.menu.home;

import ari.superarilo.entity.menu.BaseMenu;
import ari.superarilo.entity.menu.DataItems;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class HomeListGUI extends BaseMenu {
    private DataItems dataItems;
}

