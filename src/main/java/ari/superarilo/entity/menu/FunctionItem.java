package ari.superarilo.entity.menu;

import ari.superarilo.enumType.FunctionType;


public class FunctionItem extends BaseItem {
    private FunctionType type;
    public FunctionItem() {}

    public FunctionType getType() {
        return type;
    }

    public void setType(FunctionType type) {
        this.type = type;
    }
}
