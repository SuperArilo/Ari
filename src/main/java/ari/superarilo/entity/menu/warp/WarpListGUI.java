package ari.superarilo.entity.menu.warp;

import ari.superarilo.entity.menu.BaseMenu;
import ari.superarilo.entity.menu.DataItems;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class WarpListGUI extends BaseMenu {
    private DataItems dataItems;
}
