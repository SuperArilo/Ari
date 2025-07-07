package com.tty.listener.spawn;

import com.tty.enumType.GuiType;
import com.tty.listener.BaseGuiListener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SpawnListListener extends BaseGuiListener {

    public SpawnListListener(GuiType guiType) {
        super(guiType);
    }

    @Override
    public void passClick(InventoryClickEvent event) {

    }
}
