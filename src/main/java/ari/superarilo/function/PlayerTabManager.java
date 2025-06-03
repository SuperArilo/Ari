package ari.superarilo.function;

import ari.superarilo.Ari;
import ari.superarilo.dto.tab.TabGroupLine;
import ari.superarilo.enumType.FilePath;
import ari.superarilo.tool.TextTool;
import com.google.gson.reflect.TypeToken;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PlayerTabManager  {

    private ScheduledTask playerTabTask;
    private List<String> rawHeaders;
    private List<String> rawFooters;
    private Integer updateInterval;
    private Map<String, TabGroupLine> groupLineMap;

    public PlayerTabManager() {
        this.updateInterval = Ari.instance.configManager.getValue("tab.update-interval", FilePath.FunctionConfig, Integer.class);
        this.start();
    }

    public void start() {
        if (this.playerTabTask != null) {
            this.cancel();
        }
        this.buildLayout();
        this.playerTabTask = Bukkit.getAsyncScheduler()
                .runAtFixedRate(Ari.instance, i -> {
                    List<? extends Player> list = Bukkit.getOnlinePlayers().stream().toList();
                    if (list.isEmpty()) return;
                    Audience.audience(list).forEachAudience(audience -> {
                        Player player = (Player) audience;
                        audience.sendPlayerListHeaderAndFooter(
                            Component.join(JoinConfiguration.separator(Component.newline()), this.rawHeaders.stream().map(k -> TextTool.setHEXColorText(k, player)).collect(Collectors.toList())),
                            Component.join(JoinConfiguration.separator(Component.newline()), this.rawFooters.stream().map(k -> TextTool.setHEXColorText(k, player)).collect(Collectors.toList()))
                        );
                        player.playerListName(TextTool.setHEXColorText(this.buildPlayerRealLine(player)));
                    });
                }, 1L, this.updateInterval, TimeUnit.MILLISECONDS);
    }

    private String buildPlayerRealLine(Player player) {
        Optional<TabGroupLine> mainGroup = groupLineMap.entrySet().stream()
                .filter(entry -> Ari.instance.permissionUtils.getPlayerIsInGroup(player, entry.getKey()))
                .filter(entry -> !"_default_".equals(entry.getKey()))
                .findFirst()
                .map(Map.Entry::getValue);
        TabGroupLine targetLine = mainGroup.orElseGet(() -> groupLineMap.getOrDefault("_default_", new TabGroupLine("null", "null")));
        return targetLine.getPrefix() + player.getName() + targetLine.getSuffix();
    }
    /**
     * 取消 tab update 的任务
     */
    public void cancel() {
        if(this.playerTabTask == null) return;
        this.playerTabTask.cancel();
        this.playerTabTask = null;
    }

    /**
     * Tab reload 方法
     */
    public void reload() {
        this.cancel();
        this.updateInterval = null;
        this.rawHeaders = null;
        this.rawFooters = null;
        this.groupLineMap = null;
        this.start();
    }

    /**
     * 构建tab的布局
     */
    private void buildLayout() {
        if (this.updateInterval == null) {
            this.updateInterval = Ari.instance.configManager.getValue("tab.update-interval", FilePath.FunctionConfig, Integer.class);
        }
        if(this.rawHeaders == null || this.rawFooters == null) {
            this.rawHeaders = Ari.instance.configManager.getValue("tab.layout.header", FilePath.FunctionConfig, new TypeToken<List<String>>(){}.getType());
            this.rawFooters  = Ari.instance.configManager.getValue("tab.layout.footer", FilePath.FunctionConfig, new TypeToken<List<String>>(){}.getType());
        }
        if (this.groupLineMap == null) {
            this.groupLineMap = Ari.instance.configManager.getValue("tab.groups", FilePath.FunctionConfig, new TypeToken<Map<String, TabGroupLine>>(){}.getType());
        }
    }
}
