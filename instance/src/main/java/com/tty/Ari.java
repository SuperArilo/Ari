package com.tty;

import com.tty.enumType.AriCommand;
import com.tty.enumType.FilePath;
import com.tty.enumType.GuiType;
import com.tty.function.PlayerTabManager;
import com.tty.lib.ServerPlatform;
import com.tty.lib.tool.Log;
import com.tty.lib.tool.PublicFunctionUtils;
import com.tty.listener.OnPluginReloadListener;
import com.tty.listener.PlayerListener;
import com.tty.listener.home.EditHomeListener;
import com.tty.listener.home.HomeListListener;
import com.tty.listener.player.CustomChatFormantListener;
import com.tty.listener.player.KeepInventoryAndExperience;
import com.tty.listener.player.OnPlayerJoinAndLeaveListener;
import com.tty.listener.player.PlayerActionListener;
import com.tty.listener.skip_sleep.PlayerSkipNight;
import com.tty.listener.teleport.RecordLastLocationListener;
import com.tty.listener.warp.EditWarpListener;
import com.tty.listener.warp.WarpListListener;
import com.tty.papi.HomePAPI;
import com.tty.tool.*;
import io.papermc.paper.plugin.configuration.PluginMeta;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collection;


public class Ari extends JavaPlugin {

    public static Ari instance;
    public static Boolean debug = false;
    public SQLInstance sqlInstance;
    public CommandAlias commandAlias;
    @Override
    public void onLoad() {
        instance = this;
        reloadAllConfig();
        Log.initLogger(this.getLogger(), debug);
    }

    @Override
    public void onEnable() {

        PublicFunctionUtils.loadPlugin("Vault", Economy.class, EconomyUtils::setInstance, () -> Log.warning("Failed to load plugin: Vault, Economy may not be available!"));
        PublicFunctionUtils.loadPlugin("Vault", Permission.class, PermissionUtils::setInstance, () -> Log.warning("Failed to load plugin: Vault, Permission use server default!"));

        this.registerCommands();
        this.registerListener();

        this.commandAlias = new CommandAlias();

        //PAPI
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new HomePAPI().register();
        }
        this.sqlInstance = new SQLInstance();
        this.sqlInstance.start();

        ConfigObjectUtils.setRtpWorldConfig();

        this.printLogo();
    }
    @Override
    public void onDisable() {
        Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
        for (Player player : onlinePlayers) {
            OnPlayerJoinAndLeaveListener.SavePlayerData(player, false, true);
        }
        SQLInstance.close();
    }

    private void registerCommands() {
        PluginCommand pluginCommand = this.getCommand(AriCommand.ARI.getShow());
        TabExecutor commandClass = AriCommand.ARI.getCommandClass();
        if (pluginCommand != null) {
            pluginCommand.setExecutor(commandClass);
            pluginCommand.setTabCompleter(commandClass);
        }
    }
    private void registerListener() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new HomeListListener(GuiType.HOMELIST), this);
        pluginManager.registerEvents(new EditHomeListener(GuiType.HOMEEDIT), this);
        pluginManager.registerEvents(new RecordLastLocationListener(), this);
        pluginManager.registerEvents(new PlayerListener(), this);
        pluginManager.registerEvents(new WarpListListener(GuiType.WARPLIST), this);
        pluginManager.registerEvents(new EditWarpListener(GuiType.WARPEDIT), this);
        pluginManager.registerEvents(new OnPlayerJoinAndLeaveListener(), this);
        pluginManager.registerEvents(new PlayerSkipNight(), this);
        pluginManager.registerEvents(new OnPluginReloadListener(), this);
        pluginManager.registerEvents(new PlayerTabManager(), this);
        pluginManager.registerEvents(new CustomChatFormantListener(), this);
        pluginManager.registerEvents(new PlayerActionListener(), this);
        pluginManager.registerEvents(new KeepInventoryAndExperience(), this);
    }

    public static void reloadAllConfig() {
        Ari.instance.saveDefaultConfig();
        Ari.instance.reloadConfig();
        debug = Ari.instance.getConfig().getBoolean("debug.enable", false);
        loadConfigInMemory();
    }
    private static void loadConfigInMemory() {
        ConfigObjectUtils.clearConfigs();
        FileConfiguration pluginConfig = Ari.instance.getConfig();
        for (FilePath filePath : FilePath.values()) {
            String path = filePath.getPath();
            if(filePath.equals(FilePath.Lang)) {
                path = path.replace("[lang]", Ari.instance.getConfig().getString("lang", "cn"));
            }
            File file = new File(Ari.instance.getDataFolder(), path);
            if (!file.exists()) {
                Ari.instance.saveResource(path, true);
            } else if (pluginConfig.getBoolean("debug.overwrite-file", false)) {
                Ari.instance.saveResource(path, true);
            }
            ConfigObjectUtils.setConfig(filePath.getName(), YamlConfiguration.loadConfiguration(file));
        }
    }


    private void printLogo() {
        String d;
        if (ServerPlatform.isFolia()) {
            PluginMeta pluginMeta = Ari.instance.getPluginMeta();
            d = pluginMeta.getName() + " " + pluginMeta.getVersion();
        } else {
            PluginDescriptionFile description = Ari.instance.getDescription();
            d = description.getName() + " " + description.getVersion();
        }
        String ariArt =
                "        _   \n" +
                "  |    /_\\  " + d + "\n" +
                "  |___/   \\ Running on " + Bukkit.getName() + " " + Bukkit.getServer().getVersion();
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        for (String string : ariArt.split("\n")) {
            console.sendMessage(string);
        }
        console.sendMessage("");
    }
}
