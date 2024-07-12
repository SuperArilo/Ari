package ari.superarilo.entity.menu.home;

import java.util.ArrayList;
import java.util.List;

public class RenderItem {
    private String name;
    private String material;
    private List<String> lore = new ArrayList<>();

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
}
