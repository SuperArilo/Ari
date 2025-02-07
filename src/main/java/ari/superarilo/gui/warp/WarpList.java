package ari.superarilo.gui.warp;

import ari.superarilo.Ari;
import ari.superarilo.dto.CustomInventoryHolder;
import ari.superarilo.entity.menu.FunctionItem;
import ari.superarilo.entity.menu.Mask;
import ari.superarilo.entity.menu.warp.WarpListGUI;
import ari.superarilo.entity.sql.ServerWarp;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.enumType.FunctionType;
import ari.superarilo.enumType.GuiType;
import ari.superarilo.function.WarpManager;
import ari.superarilo.gui.BaseGui;
import ari.superarilo.tool.Log;
import ari.superarilo.tool.TextTool;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class WarpList extends BaseGui {

    private final WarpListGUI gui;
    private final List<String> addLore = List.of("&7左击: &6传送", "&7右击: &6编辑");
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
    protected Map<String, FunctionItem> getFunctionItems() {
        return this.gui.getFunctionItems();
    }

    @Override
    public void renderDataItem() {
        Log.debug(Level.INFO, "---------- render warp list ----------");
        long start = System.currentTimeMillis();
        List<Integer> dataSlot = this.gui.getDataSlot();
        List<ServerWarp> warpList = this.getWarpList();
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
            textComponents.add(TextTool.setHEXColorText("&2ID: " + "&6" + serverWarp.getWarpId(), this.player));
            textComponents.add(TextTool.setHEXColorText(TextTool.XYZText(serverWarp.getX(), serverWarp.getY(), serverWarp.getZ())));
            textComponents.add(TextTool.setHEXColorText("&2世界: " + "&6" + serverWarp.getWorld(), this.player));
            textComponents.addAll(this.addLore.stream().map(k -> TextTool.setHEXColorText(k, this.player)).toList());
            itemMeta.lore(textComponents);
            itemMeta.getPersistentDataContainer().set(new NamespacedKey(Ari.instance, "warp_id"), PersistentDataType.STRING, serverWarp.getWarpId());
            itemMeta.getPersistentDataContainer().set(new NamespacedKey(Ari.instance, "type"), PersistentDataType.STRING, FunctionType.DATA.name());
            itemStack.setItemMeta(itemMeta);
            this.inventory.setItem(dataSlot.get(i), itemStack);
        }
    }
    private List<ServerWarp> requestWarps() {
        return WarpManager.create(this.player).asyncGetWarpList(this.pageNum, this.gui.getDataSlot().size());
    }

    public List<ServerWarp> getWarpList() {
        return warpList;
    }
}
