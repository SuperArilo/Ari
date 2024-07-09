package ari.superarilo.dto;

import java.util.List;

public class ItemsV {
    private String name;
    private String material;
    private List<String> lore;
    public ItemsV() {}
    public String getName() {
        return name;
    }

    public String getMaterial() {
        return material;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }
}
