package ari.superarilo.entity.menu;

import ari.superarilo.enumType.FunctionType;


public class FunctionItems extends BaseItem {
    private FunctionType type;
    public FunctionItems() {}

    public FunctionType getType() {
        return type;
    }

    public void setType(FunctionType type) {
        this.type = type;
    }
}
