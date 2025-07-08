package com.tty.listener.spawn;

import com.tty.dto.OnEdit;
import com.tty.enumType.GuiType;
import com.tty.listener.BaseEditFunctionGuiListener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class EditSpawnListener extends BaseEditFunctionGuiListener {

    public EditSpawnListener(GuiType guiType) {
        super(guiType);
    }

    @Override
    public void passClick(InventoryClickEvent event) {
        super.passClick(event);
    }

    @Override
    public boolean onTitleEditStatus(String message, OnEdit onEdit) {
        return false;
    }
}
