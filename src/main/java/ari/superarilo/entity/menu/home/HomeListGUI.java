package ari.superarilo.entity.menu.home;

import ari.superarilo.entity.menu.BaseMenu;

import java.util.List;

public class HomeListGUI extends BaseMenu {

    private List<Integer> dataSlot;
    public HomeListGUI(){}

    public List<Integer> getDataSlot() {
        return dataSlot;
    }

    public void setDataSlot(List<Integer> dataSlot) {
        this.dataSlot = dataSlot;
    }

}

