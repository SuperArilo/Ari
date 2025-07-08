package com.tty.listener.spawn;

import com.tty.Ari;
import com.tty.dto.CustomInventoryHolder;
import com.tty.entity.sql.ServerSpawn;
import com.tty.enumType.FilePath;
import com.tty.enumType.GuiType;
import com.tty.function.Teleport;
import com.tty.gui.spawn.SpawnEditor;
import com.tty.gui.spawn.SpawnList;
import com.tty.lib.Lib;
import com.tty.lib.enum_type.FunctionType;
import com.tty.lib.tool.FormatUtils;
import com.tty.listener.BaseGuiListener;
import com.tty.tool.ConfigUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;

public class SpawnListListener extends BaseGuiListener {

    public SpawnListListener(GuiType guiType) {
        super(guiType);
    }

    @Override
    public void passClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        CustomInventoryHolder holder = (CustomInventoryHolder) inventory.getHolder();
        assert holder != null;
        ItemStack currentItem = event.getCurrentItem();
        assert currentItem != null;

        FunctionType type = FormatUtils.ItemNBT_TypeCheck(currentItem.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Ari.instance, "type"), PersistentDataType.STRING));
        if(type == null) return;
        Player player = holder.getPlayer();

        SpawnList spawnList = (SpawnList) holder.getMeta();

        switch (type) {
            case BACK -> inventory.close();
            case DATA -> {
                String spawnId = currentItem.getPersistentDataContainer().get(new NamespacedKey(Ari.instance, "spawn_id"), PersistentDataType.STRING);
                if (spawnId == null) return;
                Optional<ServerSpawn> first = spawnList.data.stream().filter(j -> j.getSpawnId().equals(spawnId)).findFirst();
                if (first.isEmpty()) return;
                ServerSpawn serverSpawn = first.get();
                if (event.isLeftClick()) {
                    Teleport.create(player,
                            FormatUtils.parseLocation(serverSpawn.getLocation()),
                            ConfigUtils.getValue("main.teleport-delay", FilePath.TPA, Integer.class, 3))
                            .teleport();
                } else if (event.isRightClick()) {
                    Lib.Scheduler.runAtEntity(Ari.instance,
                            player,
                            i -> {
                                inventory.close();
                                new SpawnEditor(serverSpawn, player).open();
                            },
                            () -> {});
                }
            }
            case PREV -> spawnList.prev();
            case NEXT -> spawnList.next();
        }

    }

}
