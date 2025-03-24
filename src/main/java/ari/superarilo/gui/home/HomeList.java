package ari.superarilo.gui.home;

import ari.superarilo.Ari;
import ari.superarilo.dto.CustomInventoryHolder;
import ari.superarilo.entity.menu.FunctionItems;
import ari.superarilo.entity.menu.Mask;
import ari.superarilo.entity.menu.home.HomeListGUI;
import ari.superarilo.entity.sql.PlayerHome;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.enumType.FunctionType;
import ari.superarilo.enumType.GuiType;
import ari.superarilo.enumType.LocationKeyType;
import ari.superarilo.function.HomeManager;
import ari.superarilo.gui.BasePageGui;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;


public class HomeList extends BasePageGui<List<PlayerHome>> {

    private final HomeListGUI gui;

    public HomeList(Player player) {
        super(player);
        this.gui = Ari.instance.objectConvert.yamlConvertToObj(Ari.instance.configManager.getObject(FilePath.HomeList.getName()).saveToString(), HomeListGUI.class);
        int size = this.gui.getRow() * 9;
        this.setPageSize(size);
        this.inventory = Bukkit.createInventory(new CustomInventoryHolder(player, GuiType.HOMELIST, this), size, TextTool.setHEXColorText(this.gui.getTitle(), player));
        this.data = this.requestData();
    }

    @Override
    protected Mask renderMasks() {
        return this.gui.getMask();
    }

    @Override
    protected Map<String, FunctionItems> renderFunctionItems() {
        return this.gui.getFunctionItems();
    }

    @Override
    public void renderDataItem() {
        Log.debug(Level.INFO, "---------- render home list ----------");
        long start = System.currentTimeMillis();
        List<Integer> dataSlot = this.gui.getDataItems().getSlot();
        List<String> rawLore = this.gui.getDataItems().getLore();
        for (int i = 0; i < this.data.size(); i++) {
            PlayerHome ph = this.data.get(i);
            ItemStack itemStack = new ItemStack(Material.valueOf(ph.getShowMaterial().toUpperCase()));
            ItemMeta itemMeta = itemStack.getItemMeta();
            if(itemMeta == null) {
                Log.error("There is a problem with the homeID: [" + ph.getHomeId() + "] of the player: [" + this.player.getName() + "]");
                Log.warning("Skip the rendering homeId [" + ph.getHomeId() + "] process...");
                continue;
            }
            itemMeta.displayName(TextTool.setHEXColorText(ph.getHomeName(), this.player));
            List<TextComponent> textComponents = new ArrayList<>();
            Location location = Ari.instance.objectConvert.parseLocation(ph.getLocation());
            rawLore.forEach(line -> {
                String replacedLine = line;
                for (LocationKeyType keyType : LocationKeyType.values()) {
                    replacedLine = switch (keyType) {
                        case ID -> replacedLine.replace(keyType.getKey(), ph.getHomeId());
                        case X -> replacedLine.replace(keyType.getKey(), Ari.instance.formatUtils.formatTwoDecimalPlaces(location.getX()));
                        case Y -> replacedLine.replace(keyType.getKey(), Ari.instance.formatUtils.formatTwoDecimalPlaces(location.getY()));
                        case Z -> replacedLine.replace(keyType.getKey(), Ari.instance.formatUtils.formatTwoDecimalPlaces(location.getZ()));
                        case WORLDNAME -> replacedLine.replace(keyType.getKey(), location.getWorld().getName());
                        default -> replacedLine;
                    };
                }
                textComponents.add(TextTool.setHEXColorText(replacedLine));
            });
            itemMeta.lore(textComponents);
            itemMeta.getPersistentDataContainer().set(new NamespacedKey(Ari.instance, "home_id"), PersistentDataType.STRING, ph.getHomeId());
            itemMeta.getPersistentDataContainer().set(new NamespacedKey(Ari.instance, "type"), PersistentDataType.STRING, FunctionType.DATA.name());
            itemStack.setItemMeta(itemMeta);
            this.inventory.setItem(dataSlot.get(i), itemStack);
        }
        Log.debug(Level.INFO, "---------- render time: " + (System.currentTimeMillis() - start) + "ms ----------");
    }

    @Override
    public List<PlayerHome> requestData() {
        Future<List<PlayerHome>> future = HomeManager.create(this.player).asyncGetList(this.pageNum, this.gui.getDataItems().getSlot().size());
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void prev() {
        this.pageNum--;
        if(this.pageNum <= 0) {
            this.player.sendMessage(TextTool.setHEXColorText("base.page-change.none-prev", FilePath.Lang));
            Log.debug("home list: 第一页");
            this.pageNum = 1;
            return;
        }
        this.data = this.requestData();
        this.renderDataItem();
    }
    @Override
    public void next() {
        this.pageNum++;
        List<PlayerHome> homes = this.requestData();
        if(homes.isEmpty()) {
            this.player.sendMessage(TextTool.setHEXColorText("base.page-change.none-next", FilePath.Lang));
            Log.debug("home list: 最后一页");
            this.pageNum--;
        } else {
            this.data = homes;
            this.cleanRenderDataItem(this.gui.getDataItems().getSlot());
            this.renderDataItem();
        }
    }

}
