package ari.superarilo.entity.menu;

import java.util.ArrayList;
import java.util.List;

public class BaseItem {
    private String name;
    private String material;
    private List<Integer> slot;
    private List<String> lore = new ArrayList<>();

    public BaseItem() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public List<Integer> getSlot() {
        return slot;
    }

    public void setSlot(List<Integer> slot) {
        this.slot = slot;
    }
}
