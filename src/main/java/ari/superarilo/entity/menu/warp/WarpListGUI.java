package ari.superarilo.entity.menu.warp;

import ari.superarilo.entity.menu.BaseMenu;
import ari.superarilo.entity.menu.DataItems;

import java.util.List;

public class WarpListGUI extends BaseMenu {
    private DataItems dataItems;

    public WarpListGUI() {
    }

    public DataItems getDataItems() {
        return dataItems;
    }

    public void setDataItems(DataItems dataItems) {
        this.dataItems = dataItems;
    }
}
