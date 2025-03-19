package ari.superarilo.entity.menu.home;

import lombok.Data;
import java.util.List;

@Data
public class RenderItem {
    private String name;
    private String material;
    private List<String> lore = List.of();
}
