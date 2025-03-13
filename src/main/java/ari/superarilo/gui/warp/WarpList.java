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
    private List<ServerWarp> warpList;

    public WarpList(Player player) {
        super(player);
        this.gui = Ari.instance.objectConvert.yamlConvertToObj(
                Ari.instance.configManager.getObject(FilePath.WarpList.getName()).saveToString(),
                WarpListGUI.class
        );
        this.inventory = Bukkit.createInventory(new CustomInventoryHolder(player, GuiType.WARPLIST, this), this.gui.getRow() * 9, TextTool.setHEXColorText(this.gui.getTitle(), player));
        this.warpList = this.requestWarps();
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
                Log.warning("There is a problem with the homeID: [" + serverWarp.getWarpId() + "] of the player: [" + this.player.getName() + "]");
                Log.error("Skip the rendering homeId [" + serverWarp.getWarpId() + "] process...");
                continue;
            }
            itemMeta.displayName(TextTool.setHEXColorText(serverWarp.getWarpName(), this.player));
            List<TextComponent> textComponents = new ArrayList<>();
            Location location = Ari.instance.objectConvert.parseLocation(serverWarp.getLocation());
            rawLore.forEach(line -> {
                String replacedLine = line;
                for (LocationKeyType keyType : LocationKeyType.values()) {
                    replacedLine = switch (keyType) {
                        case ID -> replacedLine.replace(keyType.getKey(), serverWarp.getWarpId());
                        case X -> replacedLine.replace(keyType.getKey(), Ari.instance.formatUtil.format_2(location.getX()));
                        case Y -> replacedLine.replace(keyType.getKey(), Ari.instance.formatUtil.format_2(location.getY()));
                        case Z -> replacedLine.replace(keyType.getKey(), Ari.instance.formatUtil.format_2(location.getZ()));
                        case WORLDNAME -> replacedLine.replace(keyType.getKey(), location.getWorld().getName());
                        case PLAYERNAME -> replacedLine.replace(keyType.getKey(), Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(serverWarp.getCreateBy()))).getName());
                    };
                }
                textComponents.add(TextTool.setHEXColorText(replacedLine));
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
        return warpList;
    }
}
