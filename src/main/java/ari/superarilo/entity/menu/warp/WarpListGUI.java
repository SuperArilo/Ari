package ari.superarilo.entity.menu.warp;

import ari.superarilo.entity.menu.BaseMenu;

import java.util.List;

public class WarpListGUI extends BaseMenu {
    private List<Integer> dataSlot;

    public WarpListGUI() {
    }

    public List<Integer> getDataSlot() {
        return dataSlot;
    }

    public void setDataSlot(List<Integer> dataSlot) {
        this.dataSlot = dataSlot;
    }
}
