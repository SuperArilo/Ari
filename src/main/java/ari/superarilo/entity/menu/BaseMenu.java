package ari.superarilo.entity.menu;


import java.util.Map;

public class BaseMenu {
    private String title;
    private Integer row;
    private Mask mask;
    private Map<String, FunctionItems> functionItems;
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

    public Map<String, FunctionItems> getFunctionItems() {
        return functionItems;
    }

    public void setFunctionItems(Map<String, FunctionItems> functionItems) {
        this.functionItems = functionItems;
    }
}
