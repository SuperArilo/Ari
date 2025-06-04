package ari.superarilo.function;

import ari.superarilo.Ari;
import ari.superarilo.dto.event.CustomPluginReloadEvent;
import ari.superarilo.dto.tab.TabGroupLine;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.tool.Log;
import ari.superarilo.tool.TextTool;
import com.google.gson.reflect.TypeToken;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class PlayerTabManager implements Listener {

    private ScheduledTask playerTabTask;
    private final List<String> rawHeaders = new ArrayList<>();
    private final List<String> rawFooters = new ArrayList<>();
    private Integer updateInterval;
    private final Map<String, TabGroupLine> groupLineMap = new HashMap<>();
    private final Map<Player, TextComponent> cachePlayerLine = new HashMap<>();

    //换行
    private final JoinConfiguration newlineSeparator = JoinConfiguration.separator(Component.newline());

    //debug
    private Integer debugCount = 0;

    public PlayerTabManager() {
        this.updateInterval = Ari.instance.configManager.getValue("tab.update-interval", FilePath.FunctionConfig, Integer.class);
        this.start();
    }

    public void start() {
        if (!this.isEnable()) return;
        if (this.playerTabTask != null) {
            this.cancel();
        }
        this.buildLayout();
        this.playerTabTask = Bukkit.getGlobalRegionScheduler().runAtFixedRate(Ari.instance, i -> {
            if (this.debugCount < 5 && Ari.debug) {
                this.debugCount++;
            }
            final long l = System.currentTimeMillis();
            final Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
            if (onlinePlayers.isEmpty()) return;
            Audience.audience(onlinePlayers).forEachAudience(audience -> {
                Player player = (Player) audience;
                audience.sendPlayerListHeaderAndFooter(
                    Component.join(this.newlineSeparator, this.rawHeaders.stream().map(k -> TextTool.setHEXColorText(k, player)).toList()),
                    Component.join(this.newlineSeparator, this.rawFooters.stream().map(k -> TextTool.setHEXColorText(k, player)).toList())
                );
                player.playerListName(Objects.requireNonNullElseGet(this.cachePlayerLine.get(player), () -> TextTool.setHEXColorText(this.buildPlayerRealLine(player))));
            });
            if (this.debugCount >= 5 && Ari.debug) {
                Log.debug("update tab time: " + (System.currentTimeMillis() - l) + "ms");
                this.debugCount = 0;
            }
        }, 1L, this.updateInterval);
    }

    private String buildPlayerRealLine(Player player) {
        Optional<TabGroupLine> mainGroup = this.groupLineMap.entrySet().stream()
                .filter(entry -> Ari.instance.permissionUtils.getPlayerIsInGroup(player, entry.getKey()))
                .filter(entry -> !"_default_".equals(entry.getKey()))
                .findFirst()
                .map(Map.Entry::getValue);
        TabGroupLine targetLine = mainGroup.orElseGet(() -> this.groupLineMap.getOrDefault("_default_", new TabGroupLine("null", "null")));
        return targetLine.getPrefix() + player.getName() + targetLine.getSuffix();
    }
    /**
     * 取消 tab update 的任务
     */
    private void cancel() {
        if(this.playerTabTask == null) return;
        this.playerTabTask.cancel();
        this.playerTabTask = null;
    }

    /**
     * Tab reload 方法
     */
    private void reload() {
        this.cancel();
        this.updateInterval = null;
        this.rawHeaders.clear();
        this.rawFooters.clear();
        this.groupLineMap.clear();
        this.cachePlayerLine.clear();
        this.start();
    }

    /**
     * 构建tab的布局
     */
    private void buildLayout() {
        if (this.updateInterval == null) {
            this.updateInterval = Ari.instance.configManager.getValue("tab.update-interval", FilePath.FunctionConfig, Integer.class);
        }
        if(this.rawHeaders.isEmpty() || this.rawFooters.isEmpty()) {
            TypeToken<List<String>> typeToken = new TypeToken<>() {};
            this.rawHeaders.addAll(Ari.instance.configManager.getValue("tab.layout.header", FilePath.FunctionConfig,typeToken.getType()));
            this.rawFooters.addAll(Ari.instance.configManager.getValue("tab.layout.footer", FilePath.FunctionConfig, typeToken.getType()));
        }
        if (this.groupLineMap.isEmpty()) {
            this.groupLineMap.putAll(Ari.instance.configManager.getValue("tab.groups", FilePath.FunctionConfig, new TypeToken<Map<String, TabGroupLine>>(){}.getType()));
        }
    }

    private boolean isEnable() {
        return Ari.instance.configManager.getValue("tab.enable", FilePath.FunctionConfig, Boolean.class);
    }

    @EventHandler
    public void onReload(CustomPluginReloadEvent event) {
        this.reload();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        this.cachePlayerLine.remove(event.getPlayer());
    }
}
