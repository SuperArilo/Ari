package com.tty.listener;

import com.tty.dto.CustomInventoryHolder;
import com.tty.enumType.GuiType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

import org.bukkit.inventory.InventoryView;

public abstract class BaseGuiListener implements Listener {

    protected final GuiType guiType;

    protected BaseGuiListener(GuiType guiType) {
        this.guiType = guiType;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR &&
                clickedInventory.getHolder() instanceof CustomInventoryHolder) {
            event.setCancelled(true);
            return;
        }

        boolean isTopCustom = topInventory.getHolder() instanceof CustomInventoryHolder;
        boolean isClickedCustom = clickedInventory.getHolder() instanceof CustomInventoryHolder;

        if (isTopCustom && isClickedCustom) {
            CustomInventoryHolder holder = (CustomInventoryHolder) clickedInventory.getHolder();
            if (holder.getType().equals(this.guiType)) {
                event.setCancelled(true);
                if (event.getCurrentItem() == null) return;
                if (event.isShiftClick()) return;
                this.passClick(event);
            }
            return;
        }

        if (isTopCustom && event.isShiftClick()) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void dragWarpEdit(InventoryDragEvent event) {
        InventoryView view = event.getView();
        Inventory topInventory = view.getTopInventory(); // 获取自定义 GUI（顶部库存）
        if (!(topInventory.getHolder() instanceof CustomInventoryHolder holder && holder.getType().equals(this.guiType))) {
            return;
        }
        int topSize = topInventory.getSize();
        for (int rawSlot : event.getRawSlots()) {
            if (rawSlot < topSize) {
                event.setCancelled(true);
                break;
            }
        }
    }

    /**
     * 检查通过的gui
     * @param event 点击事件
     */
    public abstract void passClick(InventoryClickEvent event);

}
