package com.tty.listener;

import com.tty.dto.CustomInventoryHolder;
import com.tty.dto.OnEdit;
import com.tty.enumType.FilePath;
import com.tty.enumType.GuiType;
import com.tty.lib.enum_type.FunctionType;
import com.tty.lib.tool.ComponentUtils;
import com.tty.lib.tool.FormatUtils;
import com.tty.lib.tool.Log;
import com.tty.tool.ConfigUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseEditFunctionGuiListener extends BaseGuiListener {

    private final Map<Player, OnEdit> onPlayerEditInstance = new ConcurrentHashMap<>();

    protected BaseEditFunctionGuiListener(GuiType guiType) {
        super(guiType);
    }

    @Override
    public void passClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof CustomInventoryHolder holder) {
            this.removeEditInstance(holder.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        if (this.onPlayerEditInstance.isEmpty()) return;
        Player player = event.getPlayer();
        if (!this.onPlayerEditInstance.containsKey(player)) return;
        event.setCancelled(true);
        String message = FormatUtils.componentToString(event.message());
        if (FunctionType.CANCEL.name().equals(message.toUpperCase())) {
            player.clearTitle();
            this.removeEditInstance(player);
            player.sendMessage(ComponentUtils.text(ConfigUtils.getValue("base.on-edit.cancel", FilePath.Lang)));
            return;
        }
        if (this.onTitleEditStatus(message, this.onPlayerEditInstance.get(player))) {
            this.removeEditInstance(player);
            player.clearTitle();
            Log.debug("player: [" + player.getName() + "] " + this.guiType.name() +  " status removed");
            Log.debug("onPlayerEditInstance size: " + this.onPlayerEditInstance.size());
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        this.removeEditInstance(event.getPlayer());
    }

    public abstract boolean onTitleEditStatus(String message, OnEdit onEdit);

    protected void addEditInstance(Player player, OnEdit onEdit) {
        this.onPlayerEditInstance.put(player, onEdit);
    }
    protected OnEdit removeEditInstance(Player player) {
        return this.onPlayerEditInstance.remove(player);
    }
}
