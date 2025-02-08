package ari.superarilo.dto;


public class AliasItem {
    private boolean enable;
    private boolean tabComplete;
    public boolean isEnable() {
        return this.enable;
    }
    public boolean isTabComplete() {
        return this.tabComplete;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setTabComplete(boolean tabComplete) {
        this.tabComplete = tabComplete;
    }
}
