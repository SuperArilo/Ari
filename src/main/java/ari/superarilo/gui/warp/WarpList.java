package ari.superarilo.gui.warp;

import ari.superarilo.Ari;
import ari.superarilo.dto.CustomInventoryHolder;
import ari.superarilo.entity.menu.FunctionItems;
import ari.superarilo.entity.menu.Mask;
import ari.superarilo.entity.menu.warp.WarpListGUI;
import ari.superarilo.entity.sql.ServerWarp;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.enumType.FunctionType;
import ari.superarilo.enumType.GuiType;
import ari.superarilo.enumType.LocationKeyType;
import ari.superarilo.function.WarpManager;
import ari.superarilo.gui.BaseGui;
import ari.superarilo.tool.Log;
import ari.superarilo.tool.TextTool;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class WarpList extends BaseGui {

    private final WarpListGUI gui;
    private List<ServerWarp> serverWarpList;

    public WarpList(Player player) {
        super(player);
        this.gui = Ari.instance.objectConvert.yamlConvertToObj(
                Ari.instance.configManager.getObject(FilePath.WarpList.getName()).saveToString(),
                WarpListGUI.class
        );
        this.inventory = Bukkit.createInventory(new CustomInventoryHolder(player, GuiType.WARPLIST, this), this.gui.getRow() * 9, TextTool.setHEXColorText(this.gui.getTitle(), player));
        this.serverWarpList = this.requestWarps();
    }

    @Override
    protected Mask getMask() {
        return this.gui.getMask();
    }

    @Override
    protected Map<String, FunctionItems> getFunctionItems() {
        return this.gui.getFunctionItems();
    }

    @Override
    public void renderDataItem() {
        Log.debug(Level.INFO, "---------- render warp list ----------");
        long start = System.currentTimeMillis();
        List<Integer> dataSlot = this.gui.getDataItems().getSlot();
        List<ServerWarp> warpList = this.getWarpList();
        List<String> rawLore = this.gui.getDataItems().getLore();
        for (int i = 0;i < warpList.size();i++) {
            ServerWarp serverWarp = warpList.get(i);
            ItemStack itemStack = new ItemStack(Material.valueOf(serverWarp.getShowMaterial().toUpperCase()));
            ItemMeta itemMeta = itemStack.getItemMeta();
            if(itemMeta == null) {
                Log.warning("There is a problem with the warpID: [" + serverWarp.getWarpId() + "] of the player: [" + this.player.getName() + "]");
                Log.error("Skip the rendering warpId [" + serverWarp.getWarpId() + "] process...");
                continue;
            }
            itemMeta.displayName(TextTool.setHEXColorText(serverWarp.getWarpName(), this.player));
            List<TextComponent> textComponents = new ArrayList<>();
            Location location = Ari.instance.objectConvert.parseLocation(serverWarp.getLocation());
            rawLore.forEach(line -> {
                for (LocationKeyType keyType : LocationKeyType.values()) {
                    line = switch (keyType) {
                        case ID -> line.replace(keyType.getKey(), serverWarp.getWarpId());
                        case X -> line.replace(keyType.getKey(), Ari.instance.formatUtils.formatTwoDecimalPlaces(location.getX()));
                        case Y -> line.replace(keyType.getKey(), Ari.instance.formatUtils.formatTwoDecimalPlaces(location.getY()));
                        case Z -> line.replace(keyType.getKey(), Ari.instance.formatUtils.formatTwoDecimalPlaces(location.getZ()));
                        case WORLDNAME -> line.replace(keyType.getKey(), location.getWorld().getName());
                        case PLAYERNAME -> line.replace(keyType.getKey(), Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(serverWarp.getCreateBy()))).getName());
                        case COST -> {
                            Integer cost = serverWarp.getCost();
                            if(cost == null || cost == 0) {
                                yield line.replace(keyType.getKey(), "&afree");
                            } else {
                                yield line.replace(keyType.getKey(), cost.toString());
                            }
                        }
                        case PERMISSION -> {
                            boolean hasPermission = serverWarp.getPermission() == null ||
                                    Ari.instance.permissionUtils.hasPermission(this.player, serverWarp.getPermission()) ||
                                    UUID.fromString(serverWarp.getCreateBy()).equals(this.player.getUniqueId());
                            yield line.replace(keyType.getKey(), Ari.instance.configManager.getValue(hasPermission ? "base.yes_re":"base.no_re", FilePath.Lang, String.class));
                        }
                    };
                }
                textComponents.add(TextTool.setHEXColorText(line));
            });
            itemMeta.lore(textComponents);
            itemMeta.getPersistentDataContainer().set(new NamespacedKey(Ari.instance, "warp_id"), PersistentDataType.STRING, serverWarp.getWarpId());
            itemMeta.getPersistentDataContainer().set(new NamespacedKey(Ari.instance, "type"), PersistentDataType.STRING, FunctionType.DATA.name());
            itemStack.setItemMeta(itemMeta);
            this.inventory.setItem(dataSlot.get(i), itemStack);
        }
        Log.debug(Level.INFO, "---------- render time: " + (System.currentTimeMillis() - start) + "ms ----------");
    }
    private List<ServerWarp> requestWarps() {
        CompletableFuture<List<ServerWarp>> future = WarpManager.create(this.player).asyncGetList(this.pageNum, this.gui.getDataItems().getSlot().size());
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ServerWarp> getWarpList() {
        return serverWarpList;
    }
}
