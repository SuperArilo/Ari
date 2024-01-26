package ari.superarilo;

import ari.superarilo.command.tpa.Tpa;
import ari.superarilo.tool.ConfigFiles;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Logger;

public class SuperArilo extends JavaPlugin {

    public static SuperArilo instance;
    public static Logger logger;

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        instance = this;
        logger = instance.getLogger();
        ConfigFiles.checkFiles();
        Objects.requireNonNull(getServer().getPluginCommand("tpa")).setExecutor(new Tpa());
    }
    @Override
    public void onDisable() {

    }


}
