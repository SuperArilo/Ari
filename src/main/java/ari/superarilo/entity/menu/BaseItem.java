package ari.superarilo.entity.menu;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BaseItem {
    private String name;
    private String material;
    private List<Integer> slot;
    private List<String> lore = new ArrayList<>();
}
