package com.tty.function;

import com.google.gson.reflect.TypeToken;
import com.tty.Ari;
import com.tty.dto.event.CustomPluginReloadEvent;
import com.tty.dto.tab.TabGroup;
import com.tty.dto.tab.TabGroupLine;
import com.tty.enumType.FilePath;
import com.tty.lib.Lib;
import com.tty.lib.task.CancellableTask;
import com.tty.tool.ConfigObjectUtils;
import com.tty.tool.Log;
import com.tty.tool.PermissionUtils;
import com.tty.tool.TextTool;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerTabManager implements Listener {

    private CancellableTask playerTabTask;
    private final List<String> rawHeaders = new ArrayList<>();
    private final List<String> rawFooters = new ArrayList<>();
    private Integer updateInterval;
    private final Map<String, TabGroupLine> groupLineMap = new HashMap<>();
    private final List<String> groupSequence = new ArrayList<>();
    //换行
    private final JoinConfiguration newlineSeparator = JoinConfiguration.separator(Component.newline());

    //debug
    private Integer debugCount = 0;

    public PlayerTabManager() {
        this.updateInterval = ConfigObjectUtils.getValue("tab.update-interval", FilePath.FunctionConfig.getName(), Integer.class, 1);
        this.start();
    }

    public void start() {
        if (!this.isEnable()) {
            this.sendTab(Bukkit.getOnlinePlayers());
            return;
        }
        if (this.playerTabTask != null) {
            this.cancel();
        }
        this.buildLayout();
        this.playerTabTask = Lib.Scheduler.runAtFixedRate(Ari.instance, i -> {
            if (this.debugCount < 5 && Ari.debug) {
                this.debugCount++;
            }
            Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
            if (onlinePlayers.isEmpty()) return;
            final long l = System.currentTimeMillis();
            this.sendTab(onlinePlayers);
            if (this.debugCount >= 5 && Ari.debug) {
                Log.debug("update tab time: " + (System.currentTimeMillis() - l) + "ms");
                this.debugCount = 0;
            }
        }, 1L, this.updateInterval);
    }

    private void sendTab(final Collection<? extends Player> players) {

        final Map<String, TabGroup> groupMap = new LinkedHashMap<>(this.groupSequence.size());
        final TabGroupLine defaultTabGroupLine = new TabGroupLine("", "");

        final Map<String, List<Player>> groupedPlayers = new HashMap<>();
        for (String group : this.groupSequence) {
            groupedPlayers.put(group, new ArrayList<>());
        }
        for (Player player : players) {
            String highestGroup = null;
            for (String group : this.groupSequence) {
                if (PermissionUtils.getPlayerIsInGroup(player, group)) {
                    highestGroup = group;
                    break;
                }
            }
            if (highestGroup != null) {
                groupedPlayers.get(highestGroup).add(player);
            }
        }
        for (String group : this.groupSequence) {
            List<Player> groupPlayers = groupedPlayers.get(group);
            if (!groupPlayers.isEmpty()) {
                groupPlayers.sort(Comparator.comparing(Player::getName));
            }
            TabGroup tabGroup = TabGroup.build(groupPlayers, this.groupLineMap.getOrDefault(group, defaultTabGroupLine));
            groupMap.put(group, tabGroup);
        }

        final AtomicInteger displayIndex = new AtomicInteger(Bukkit.getMaxPlayers());
        groupMap.forEach((k, v) -> Audience.audience(v.getPlayers()).forEachAudience(audience -> {
            Player player = (Player) audience;
            player.sendPlayerListHeaderAndFooter(this.buildTabLine(this.rawHeaders, player), this.buildTabLine(this.rawFooters, player));
            player.playerListName(TextTool.setHEXColorText(this.buildPlayerRealLine(player, v)));
            player.setPlayerListOrder(displayIndex.getAndDecrement());
        }));
    }

    private Component buildTabLine(List<String> s, final Player player) {
        if (s == null || s.isEmpty()) return Component.empty();
        return Component.join(this.newlineSeparator, s.stream().map(line -> TextTool.setHEXColorText(line, player)).toList());
    }

    private String buildPlayerRealLine(Player player, TabGroupLine line) {
        return line.getPrefix() + player.getName() + line.getSuffix();
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
        this.start();
    }

    /**
     * 构建tab的布局
     */
    private void buildLayout() {
        if (this.updateInterval == null) {
            this.updateInterval = ConfigObjectUtils.getValue("tab.update-interval", FilePath.FunctionConfig.getName(), Integer.class, 1);
        }
        if(this.rawHeaders.isEmpty() || this.rawFooters.isEmpty()) {
            TypeToken<List<String>> typeToken = new TypeToken<>() {};
            List<String> headerValue = ConfigObjectUtils.getValue("tab.layout.header", FilePath.FunctionConfig.getName(), typeToken.getType(), List.of());
            if (headerValue != null) {
                this.rawHeaders.addAll(headerValue);
            }
            List<String> footerValue = ConfigObjectUtils.getValue("tab.layout.footer", FilePath.FunctionConfig.getName(), typeToken.getType(), List.of());
            if (footerValue != null) {
                this.rawFooters.addAll(footerValue);
            }
        }
        Map<String, TabGroupLine> lineMap = ConfigObjectUtils.getValue("tab.groups", FilePath.FunctionConfig.getName(), new TypeToken<Map<String, TabGroupLine>>() {}.getType(), new HashMap<>());
        if (this.groupLineMap.isEmpty() && lineMap != null) {
            this.groupLineMap.putAll(lineMap);
        }
        List<String> value = ConfigObjectUtils.getValue("tab.slot", FilePath.FunctionConfig.getName(), new TypeToken<List<String>>() {}.getType(), List.of());
        if (this.groupSequence.isEmpty() && value != null) {
            this.groupSequence.addAll(value);
        }
    }

    private boolean isEnable() {
        return Boolean.TRUE.equals(ConfigObjectUtils.getValue("tab.enable", FilePath.FunctionConfig.getName(), Boolean.class, false));
    }

    @EventHandler
    public void onReload(CustomPluginReloadEvent event) {
        this.reload();
    }

}
