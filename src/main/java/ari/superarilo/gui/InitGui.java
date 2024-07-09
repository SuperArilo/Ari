package ari.superarilo.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class InitGui {

    protected final Player player;
    protected Inventory inventory;
    public InitGui(Player player) {
        this.player = player;
    }
    public void open() {
        this.player.openInventory(this.inventory);
    }

    protected List<String> parseLayout(List<String> layout) {
        int rowCount = Math.min(layout.size(), 6);
        List<String> parsedLayout = new ArrayList<>(rowCount * 9);
        for (int i = 0; i < rowCount; i++) {
            String line = layout.get(i);
            if (line.length() != 9) {
                throw new IllegalArgumentException("Each line must be 9 characters long.");
            }
            for (int j = 0; j < 9; j++) {
                parsedLayout.add(String.valueOf(line.charAt(j)));
            }
        }
        return parsedLayout;
    }
}
