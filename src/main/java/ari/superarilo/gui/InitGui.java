package ari.superarilo.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public abstract class InitGui {

    protected final Player player;
    protected Inventory inventory;
    public InitGui(Player player) {
        this.player = player;
    }
    public void open() {
        this.player.openInventory(this.inventory);
    }

    protected List<String> parseLayout(List<String> layout) {
        List<String> result = new ArrayList<>(layout.size() * 9);
        StringBuilder bracketContent = null;
        for (String line : layout) {
            boolean inBrackets = false;
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                if (c == '(') {
                    if (!inBrackets) {
                        inBrackets = true;
                        bracketContent = new StringBuilder();
                    }
                } else if (c == ')') {
                    if (inBrackets) {
                        inBrackets = false;
                        result.add(bracketContent.toString());
                        bracketContent = null;
                    }
                } else if (inBrackets) {
                    bracketContent.append(c);
                } else {
                    result.add(String.valueOf(c));
                }
            }
            if (inBrackets) {
                bracketContent = null;
            }
        }
        return result;
    }
}
