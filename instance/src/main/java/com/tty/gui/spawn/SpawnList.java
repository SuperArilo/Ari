package com.tty.gui.spawn;

import com.tty.Ari;
import com.tty.dto.CustomInventoryHolder;
import com.tty.entity.menu.FunctionItems;
import com.tty.entity.menu.Mask;
import com.tty.entity.menu.spawn.SpawnListGUI;
import com.tty.entity.sql.ServerSpawn;
import com.tty.enumType.FilePath;
import com.tty.enumType.GuiType;
import com.tty.function.SpawnManager;
import com.tty.gui.BasePageGui;
import com.tty.lib.dto.Page;
import com.tty.lib.enum_type.FunctionType;
import com.tty.lib.enum_type.LocationKeyType;
import com.tty.lib.tool.ComponentUtils;
import com.tty.lib.tool.FormatUtils;
import com.tty.lib.tool.Log;
import com.tty.tool.ConfigUtils;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SpawnList extends BasePageGui<ServerSpawn, SpawnListGUI> {


    public SpawnList(Player player) {
        super(player, FormatUtils.yamlConvertToObj(ConfigUtils.getObject(FilePath.SpawnList.name()).saveToString(), SpawnListGUI.class));
    }

    @Override
    protected Mask renderMasks() {
        return this.instance.getMask();
    }

    @Override
    protected Map<String, FunctionItems> renderFunctionItems() {
        return this.instance.getFunctionItems();
    }

    @Override
    protected CompletableFuture<List<ServerSpawn>> requestData() {
        return new SpawnManager(true).getList(Page.create(this.pageNum, this.pageSize));
    }

    @Override
    protected void init() {
        this.setPageSize(this.instance.getDataItems().getSlot().size());
        this.inventory = Bukkit.createInventory(
                new CustomInventoryHolder(player, GuiType.SPAWNLIST, this),
                this.instance.getRow() * 9,
                ComponentUtils.text(this.instance.getTitle(), player));
    }

    @Override
    protected void renderDataItem() {
        List<Integer> dataSlot = this.instance.getDataItems().getSlot();
        List<String> rawLore = this.instance.getDataItems().getLore();
        for (int i = 0; i < this.data.size(); i++) {
            ServerSpawn serverSpawn = this.data.get(i);
            ItemStack itemStack = new ItemStack(Material.valueOf(serverSpawn.getShowMaterial().toUpperCase()));
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta == null) {
                Log.error("There is a problem with the spawnID: [" + serverSpawn.getSpawnId() + "] of the player: [" + this.player.getName() + "]");
                Log.warning("Skip the rendering spawnID [" + serverSpawn.getSpawnId() + "] process...");
                continue;
            }
            itemMeta.displayName(ComponentUtils.text(serverSpawn.getSpawnName(), this.player));
            List<TextComponent> textComponents = new ArrayList<>();
            Location location = FormatUtils.parseLocation(serverSpawn.getLocation());
            for (String line : rawLore) {
                for (LocationKeyType keyType : LocationKeyType.values()) {
                    line = switch (keyType) {
                        case ID -> line.replace(keyType.getKey(), serverSpawn.getSpawnId());
                        case X -> line.replace(keyType.getKey(), FormatUtils.formatTwoDecimalPlaces(location.getX()));
                        case Y -> line.replace(keyType.getKey(), FormatUtils.formatTwoDecimalPlaces(location.getY()));
                        case Z -> line.replace(keyType.getKey(), FormatUtils.formatTwoDecimalPlaces(location.getZ()));
                        case WORLDNAME -> line.replace(keyType.getKey(), location.getWorld().getName());
                        case PLAYERNAME -> {
                            UUID uuid = UUID.fromString(serverSpawn.getCreateBy());
                            String name;
                            Player onlinePlayer = Bukkit.getPlayer(uuid);
                            if (onlinePlayer != null) {
                                name = onlinePlayer.getName();
                            } else {
                                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                                name = offlinePlayer.getName() != null ? offlinePlayer.getName() : "null";
                            }
                            yield line.replace(keyType.getKey(), name);
                        }
                        default -> line;
                    };
                }
                textComponents.add(ComponentUtils.text(line));
            }
            itemMeta.lore(textComponents);
            itemMeta.getPersistentDataContainer().set(new NamespacedKey(Ari.instance, "spawn_id"), PersistentDataType.STRING, serverSpawn.getSpawnId());
            itemMeta.getPersistentDataContainer().set(new NamespacedKey(Ari.instance, "type"), PersistentDataType.STRING, FunctionType.DATA.name());
            if (serverSpawn.isTopSlot()) {
                itemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            itemStack.setItemMeta(itemMeta);
            this.inventory.setItem(dataSlot.get(i), itemStack);
        }
    }
}
