package com.tty;

import com.tty.entity.TpStatusValue;
import com.tty.enumType.AriCommand;
import com.tty.enumType.FilePath;
import com.tty.function.PlayerTabManager;
import com.tty.lib.ServerPlatform;
import com.tty.lib.tool.PublicFunctionUtils;
import com.tty.listener.OnPluginReloadListener;
import com.tty.listener.PlayerListener;
import com.tty.listener.home.EditHomeListener;
import com.tty.listener.home.HomeListListener;
import com.tty.listener.player.OnPlayerListener;
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
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;


public class Ari extends JavaPlugin {

    public static Ari instance;
    public static Boolean debug = false;
    public TpStatusValue tpStatusValue;

    public SQLInstance SQLInstance;
    public CommandAlias commandAlias;
    @Override
    public void onLoad() {
        instance = this;
        reloadAllConfig();
        Log.initLogger(this.getLogger(), Ari.debug);
        Log.debug(Level.INFO, "----------------");
        Log.debug(Level.INFO, "   " + ConfigObjectUtils.getValue("debug.on-open", FilePath.Lang.getName(), String.class, "ed") + "   ");
        Log.debug(Level.INFO, "----------------");
    }

    @Override
    public void onEnable() {

        PublicFunctionUtils.loadPlugin("Vault", Economy.class, EconomyUtils::setInstance, () -> Log.warning("Failed to load plugin: Vault"));
        PublicFunctionUtils.loadPlugin("Vault", Permission.class, PermissionUtils::setInstance, () -> Log.warning("Failed to load plugin: Vault"));

        this.registerCommands();
        this.registerListener();

        this.commandAlias = new CommandAlias();

        //PAPI
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new HomePAPI().register();
        }
        //sql
        this.SQLInstance = new SQLInstance();

        this.tpStatusValue = new TpStatusValue();
        this.printLogo();
    }
    @Override
    public void onDisable() {
        super.onDisable();
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
        Bukkit.getPluginManager().registerEvents(new HomeListListener(), this);
        Bukkit.getPluginManager().registerEvents(new EditHomeListener(), this);
        Bukkit.getPluginManager().registerEvents(new RecordLastLocationListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new WarpListListener(), this);
        Bukkit.getPluginManager().registerEvents(new EditWarpListener(), this);
        Bukkit.getPluginManager().registerEvents(new OnPlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerSkipNight(), this);
        Bukkit.getPluginManager().registerEvents(new OnPluginReloadListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerTabManager(), this);
    }

    public static void reloadAllConfig() {
        Ari.instance.saveDefaultConfig();
        Ari.instance.reloadConfig();
        boolean newDebugState = Ari.instance.getConfig().getBoolean("debug.enable", false);
        if (!Ari.debug && newDebugState) {
            Ari.instance.saveResource("config.yml", true);
            Ari.instance.reloadConfig();
            Ari.debug = true;
        } else {
            Ari.debug = newDebugState;
        }
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
