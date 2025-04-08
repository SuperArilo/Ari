package ari.superarilo.function;

import ari.superarilo.Ari;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PlayerTabManager  {

    private ScheduledTask playerTabTask;
    private List<String> rawHeaders;
    private List<String> rawFooters;
    private Integer updateInterval;
    public PlayerTabManager() {
        this.updateInterval = Ari.instance.configManager.getValue("tab.update-interval", FilePath.FunctionConfig, Integer.class);
        this.start();
    }

    public void start() {
        if (this.playerTabTask != null) {
            this.cancel();
        }
        this.playerTabTask = Bukkit.getAsyncScheduler()
                .runAtFixedRate(Ari.instance, i -> {
                    List<? extends Player> list = Bukkit.getOnlinePlayers().stream().toList();
                    if (list.isEmpty()) return;
                    this.buildLayout();
                    Audience.audience(list).sendPlayerListHeaderAndFooter(
                        Component.join(JoinConfiguration.separator(Component.newline()), this.rawHeaders.stream().map(k -> TextTool.setHEXColorText(k, list.get(0))).collect(Collectors.toList())),
                        Component.join(JoinConfiguration.separator(Component.newline()), this.rawFooters.stream().map(k -> TextTool.setHEXColorText(k, list.get(0))).collect(Collectors.toList()))
                    );
                }, 1L, this.updateInterval, TimeUnit.MILLISECONDS);
    }
    public void cancel() {
        if(this.playerTabTask == null) return;
        this.playerTabTask.cancel();
        this.playerTabTask = null;
    }

    public void reload() {
        this.cancel();
        this.updateInterval = null;
        this.rawHeaders = null;
        this.rawFooters = null;
        this.start();
    }

    private void buildLayout() {
        if (this.updateInterval == null) {
            this.updateInterval = Ari.instance.configManager.getValue("tab.update-interval", FilePath.FunctionConfig, Integer.class);
        }
        if(this.rawHeaders == null || this.rawFooters == null) {
            this.rawHeaders = Ari.instance.configManager.getValue("tab.layout.header", FilePath.FunctionConfig, new TypeToken<List<String>>(){}.getType());
            this.rawFooters  = Ari.instance.configManager.getValue("tab.layout.footer", FilePath.FunctionConfig, new TypeToken<List<String>>(){}.getType());
        }

    }
}
