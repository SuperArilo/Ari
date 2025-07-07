package com.tty.gui.home;

import com.tty.Ari;
import com.tty.dto.CustomInventoryHolder;
import com.tty.entity.menu.FunctionItems;
import com.tty.entity.menu.Mask;
import com.tty.entity.menu.home.HomeListGUI;
import com.tty.entity.sql.ServerHome;
import com.tty.enumType.FilePath;
import com.tty.enumType.GuiType;
import com.tty.function.HomeManager;
import com.tty.gui.BasePageGui;
import com.tty.lib.dto.Page;
import com.tty.lib.enum_type.FunctionType;
import com.tty.lib.enum_type.LocationKeyType;
import com.tty.lib.tool.ComponentUtils;
import com.tty.lib.tool.FormatUtils;
import com.tty.tool.ConfigUtils;
import com.tty.lib.tool.Log;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


public class HomeList extends BasePageGui<ServerHome, HomeListGUI> {

    public HomeList(Player player) {
        super(player, FormatUtils.yamlConvertToObj(ConfigUtils.getObject(FilePath.HomeList.name()).saveToString(), HomeListGUI.class));
    }

    @Override
    public CompletableFuture<List<ServerHome>> requestData() {
        return new HomeManager(this.player, true)
                .getList(Page.create(this.pageNum, this.instance.getDataItems().getSlot().size()));
    }

    @Override
    protected void init() {
        this.setPageSize(this.instance.getDataItems().getSlot().size());
        this.inventory = Bukkit.createInventory(
                new CustomInventoryHolder(player, GuiType.HOMELIST, this),
                this.instance.getRow() * 9,
                ComponentUtils.text(this.instance.getTitle(), player));
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
    public void renderDataItem() {
        List<Integer> dataSlot = this.instance.getDataItems().getSlot();
        List<String> rawLore = this.instance.getDataItems().getLore();
        for (int i = 0; i < this.data.size(); i++) {
            ServerHome ph = this.data.get(i);
            ItemStack itemStack = new ItemStack(Material.valueOf(ph.getShowMaterial().toUpperCase()));
            ItemMeta itemMeta = itemStack.getItemMeta();
            if(itemMeta == null) {
                Log.error("There is a problem with the homeID: [" + ph.getHomeId() + "] of the player: [" + this.player.getName() + "]");
                Log.warning("Skip the rendering homeId [" + ph.getHomeId() + "] process...");
                continue;
            }
            itemMeta.displayName(ComponentUtils.text(ph.getHomeName(), this.player));
            List<TextComponent> textComponents = new ArrayList<>();
            Location location = FormatUtils.parseLocation(ph.getLocation());
            rawLore.forEach(line -> {
                String replacedLine = line;
                for (LocationKeyType keyType : LocationKeyType.values()) {
                    replacedLine = switch (keyType) {
                        case ID -> replacedLine.replace(keyType.getKey(), ph.getHomeId());
                        case X -> replacedLine.replace(keyType.getKey(), FormatUtils.formatTwoDecimalPlaces(location.getX()));
                        case Y -> replacedLine.replace(keyType.getKey(), FormatUtils.formatTwoDecimalPlaces(location.getY()));
                        case Z -> replacedLine.replace(keyType.getKey(), FormatUtils.formatTwoDecimalPlaces(location.getZ()));
                        case WORLDNAME -> replacedLine.replace(keyType.getKey(), location.getWorld().getName());
                        default -> replacedLine;
                    };
                }
                textComponents.add(ComponentUtils.text(replacedLine));
            });
            itemMeta.lore(textComponents);
            itemMeta.getPersistentDataContainer().set(new NamespacedKey(Ari.instance, "home_id"), PersistentDataType.STRING, ph.getHomeId());
            itemMeta.getPersistentDataContainer().set(new NamespacedKey(Ari.instance, "type"), PersistentDataType.STRING, FunctionType.DATA.name());
            if (ph.isTopSlot()) {
                itemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            itemStack.setItemMeta(itemMeta);
            this.inventory.setItem(dataSlot.get(i), itemStack);
        }
    }
}
