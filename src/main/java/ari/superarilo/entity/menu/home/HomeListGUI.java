package ari.superarilo.entity.menu.home;

import ari.superarilo.entity.menu.BaseMenu;
import ari.superarilo.entity.menu.FunctionItem;
import ari.superarilo.entity.sql.PlayerHome;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

