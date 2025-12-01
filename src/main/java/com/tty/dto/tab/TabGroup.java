package com.tty.dto.tab;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
@Setter
public class TabGroup extends TabGroupLine {

    private List<Player> players;

    public TabGroup(String prefix, String suffix) {
        super(prefix, suffix);
    }

    public static TabGroup build(List<Player> players,TabGroupLine line) {
        TabGroup tabGroup = new TabGroup(line.getPrefix(), line.getSuffix());
        tabGroup.setPlayers(players);
        tabGroup.setPrefix(line.getPrefix());
        tabGroup.setSuffix(line.getSuffix());
        return tabGroup;
    }
}
