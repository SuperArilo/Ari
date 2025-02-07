package ari.superarilo.entity.menu;

import java.util.List;

public class DataItems {
    private List<Integer> slot;
    private List<String> lore;

    public List<Integer> getSlot() {
        return slot;
    }

    public void setSlot(List<Integer> slot) {
        this.slot = slot;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }
}
