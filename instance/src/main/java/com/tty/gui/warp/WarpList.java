package com.tty.gui.warp;

import com.tty.Ari;
import com.tty.dto.CustomInventoryHolder;
import com.tty.entity.menu.BaseDataMenu;
import com.tty.entity.menu.FunctionItems;
import com.tty.entity.menu.Mask;
import com.tty.entity.sql.ServerWarp;
import com.tty.enumType.FilePath;
import com.tty.enumType.GuiType;
import com.tty.function.WarpManager;
import com.tty.gui.BaseDataItemInventory;
import com.tty.lib.Log;
import com.tty.lib.dto.Page;
import com.tty.lib.enum_type.FunctionType;
import com.tty.lib.enum_type.IconKeyType;
import com.tty.lib.tool.ComponentUtils;
import com.tty.lib.tool.FormatUtils;
import com.tty.tool.ConfigUtils;
import com.tty.lib.tool.EconomyUtils;
import com.tty.lib.tool.PermissionUtils;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class WarpList extends BaseDataItemInventory<ServerWarp> {

    private final String baseYesRe = "base.yes_re";
    private final String baseNoRe = "base.no_re";
    private final String baseFree = Ari.C_INSTANCE.getValue("base.free", FilePath.LANG);

    public WarpList(Player player) {
        super(FormatUtils.yamlConvertToObj(Ari.C_INSTANCE.getObject(FilePath.WARP_LIST_GUI.name()).saveToString(), BaseDataMenu.class), player);
    }

    @Override
    public CompletableFuture<List<ServerWarp>> requestData() {
        return new WarpManager(true).getList(Page.create(this.pageNum, this.pageSize));
    }

    @Override
    protected Map<Integer, ItemStack> getRenderItem() {
        Map<Integer, ItemStack> map = new HashMap<>();
        List<Integer> dataSlot = this.baseDataInstance.getDataItems().getSlot();
        List<String> rawLore = this.baseDataInstance.getDataItems().getLore();
        for (int i = 0;i < this.data.size();i++) {
            ServerWarp serverWarp = this.data.get(i);
            ItemStack itemStack;
            try {
                itemStack = new ItemStack(Material.valueOf(serverWarp.getShowMaterial().toUpperCase()));
            } catch (Exception e) {
                Log.warn("There is a problem with the warpID: [%s] of the player: [%s]", serverWarp.getWarpId(), this.player.getName());
                Log.error(e, "Skip the rendering warpId [%s] process...", serverWarp.getWarpId());
                this.player.sendMessage(ConfigUtils.t("base.on-error"));
                continue;
            }
            List<TextComponent> textComponents = new ArrayList<>();
            Location location = FormatUtils.parseLocation(serverWarp.getLocation());

            UUID uuid = UUID.fromString(serverWarp.getCreateBy());
            String playName;
            Player onlinePlayer = Bukkit.getPlayer(uuid);
            if (onlinePlayer != null) {
                playName = onlinePlayer.getName();
            } else {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                playName = offlinePlayer.getName() != null ? offlinePlayer.getName() : "";
            }

            boolean hasPermission = serverWarp.getPermission() == null ||
                    serverWarp.getPermission().isEmpty() ||
                    PermissionUtils.hasPermission(this.player, serverWarp.getPermission()) ||
                    UUID.fromString(serverWarp.getCreateBy()).equals(this.player.getUniqueId());

            rawLore.forEach(line -> {
                StringBuilder sb = new StringBuilder(line);
                for (IconKeyType keyType : IconKeyType.values()) {
                    String replacement = switch (keyType) {
                        case ID -> serverWarp.getWarpId();
                        case X -> FormatUtils.formatTwoDecimalPlaces(location.getX());
                        case Y -> FormatUtils.formatTwoDecimalPlaces(location.getY());
                        case Z -> FormatUtils.formatTwoDecimalPlaces(location.getZ());
                        case WORLDNAME -> location.getWorld().getName();
                        case PLAYERNAME -> playName;
                        case COST -> {
                            Double cost = serverWarp.getCost();
                            yield cost == null || cost == 0 ? baseFree : cost + EconomyUtils.getNamePlural();
                        }
                        case TOP_SLOT -> serverWarp.isTopSlot() ? this.baseYesRe:this.baseNoRe;
                        case PERMISSION -> Ari.C_INSTANCE.getValue(hasPermission ? this.baseYesRe:this.baseNoRe, FilePath.LANG);
                    };
                    int index;
                    while ((index = sb.indexOf(keyType.getKey())) != -1) {
                        sb.replace(index, index + keyType.getKey().length(), replacement);
                    }
                }
                textComponents.add(ComponentUtils.text(sb.toString(), player));
            });
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.displayName(ComponentUtils.text(serverWarp.getWarpName(), this.player));
            itemMeta.lore(textComponents);
            itemMeta.getPersistentDataContainer().set(new NamespacedKey(Ari.instance, "warp_id"), PersistentDataType.STRING, serverWarp.getWarpId());
            itemMeta.getPersistentDataContainer().set(new NamespacedKey(Ari.instance, "type"), PersistentDataType.STRING, FunctionType.DATA.name());
            if (serverWarp.isTopSlot()) {
                itemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            itemStack.setItemMeta(itemMeta);
            map.put(dataSlot.get(i), itemStack);
        }
        return map;
    }

    @Override
    protected Mask getMasks() {
        return null;
    }

    @Override
    protected Map<String, FunctionItems> getFunctionItems() {
        return null;
    }

    @Override
    protected CustomInventoryHolder createHolder() {
        return new CustomInventoryHolder(player, GuiType.WARPLIST, this);
    }

}
