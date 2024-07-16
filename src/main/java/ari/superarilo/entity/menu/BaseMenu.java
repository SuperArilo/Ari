package ari.superarilo.entity.menu;

import ari.superarilo.entity.menu.home.RenderItem;

import java.util.List;
import java.util.Map;

public class BaseMenu {
    private String title;
    private Integer row;
    private Mask mask;
    private Map<String, FunctionItem> functionItems;
    public BaseMenu() {}
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Mask getMask() {
        return mask;
    }

    public void setMask(Mask mask) {
        this.mask = mask;
    }

    public Map<String, FunctionItem> getFunctionItems() {
        return functionItems;
    }

    public void setFunctionItems(Map<String, FunctionItem> functionItems) {
        this.functionItems = functionItems;
    }
}
