package ari.superarilo.entity.menu.home;

import ari.superarilo.entity.menu.BaseMenu;
import ari.superarilo.entity.menu.DataItems;

public class HomeListGUI extends BaseMenu {
    private DataItems dataItems;
    public HomeListGUI(){}

    public DataItems getDataItems() {
        return dataItems;
    }

    public void setDataItems(DataItems dataItems) {
        this.dataItems = dataItems;
    }
}

