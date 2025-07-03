package com.tty.listener.home;

import com.tty.Ari;
import com.tty.dto.CustomInventoryHolder;
import com.tty.entity.sql.ServerHome;
import com.tty.enumType.FilePath;
import com.tty.enumType.GuiType;
import com.tty.gui.home.HomeEditor;
import com.tty.gui.home.HomeList;
import com.tty.lib.Lib;
import com.tty.function.Teleport;
import com.tty.lib.enum_type.FunctionType;
import com.tty.listener.BaseGuiListener;
import com.tty.tool.ConfigObjectUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;


public class HomeListListener extends BaseGuiListener {
    public HomeListListener(GuiType guiType) {
        super(guiType);
    }

    @Override
    public void passClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        CustomInventoryHolder holder = (CustomInventoryHolder) inventory.getHolder();
        assert holder != null;
        ItemStack currentItem = event.getCurrentItem();
        assert currentItem != null;

        FunctionType type = ConfigObjectUtils.ItemNBT_TypeCheck(currentItem.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Ari.instance, "type"), PersistentDataType.STRING));
        if(type == null) return;
        Player player = holder.getPlayer();
        HomeList homeList = (HomeList) holder.getMeta();
        switch (type) {
            case BACK -> inventory.close();
            case DATA -> {
                String homeId = currentItem.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Ari.instance, "home_id"), PersistentDataType.STRING);
                if (homeId == null) break;
                Optional<ServerHome> first = homeList.data.stream().filter(j -> j.getHomeId().equals(homeId) && j.getPlayerUUID().equals(player.getUniqueId().toString())).findFirst();
                if(first.isEmpty()) return;
                ServerHome home = first.get();
                ClickType click = event.getClick();
                if (click.equals(ClickType.LEFT)) {
                    Teleport.create(
                            player,
                            ConfigObjectUtils.parseLocation(home.getLocation()),
                            ConfigObjectUtils.getValue("main.teleport.delay", FilePath.TPA.getName(), Integer.class, 3)).teleport();
                } else if (click.equals(ClickType.RIGHT)) {
                    Lib.Scheduler.run(Ari.instance, p -> {
                        inventory.close();
                        new HomeEditor(home,(Player) event.getWhoClicked()).open();
                    });
                }
                Lib.Scheduler.runAtRegion(Ari.instance, player.getLocation(), o -> inventory.close());
            }
            case PREV -> homeList.prev();
            case NEXT -> homeList.next();
        }
    }
}
