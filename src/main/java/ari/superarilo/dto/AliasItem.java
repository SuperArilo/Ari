package ari.superarilo.dto;

import java.util.List;

public class AliasItem {
    private boolean enable;
    private List<String> alias;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public List<String> getAlias() {
        return alias;
    }

    public void setAlias(List<String> alias) {
        this.alias = alias;
    }
}
