package ari.superarilo.tool;

import ari.superarilo.SuperArilo;
import ari.superarilo.enumType.FilePath;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigFiles {

    public static Map<String, FileConfiguration> configs = new ConcurrentHashMap<>();

    public static void reloadAllConfig() {
        SuperArilo.instance.saveDefaultConfig();
        SuperArilo.instance.reloadConfig();
        checkFiles();
    }
    public static void checkFiles() {
        configs = new ConcurrentHashMap<>();
        for (FilePath filePath : FilePath.values()) {
            String path = filePath.getPath();
            File file = new File(SuperArilo.instance.getDataFolder(), path);
            SuperArilo.instance.saveResource(path, true);
//            if(!file.exists()) {
//                SuperArilo.instance.saveResource(path, false);
//            }
            configs.put(filePath.getName(), YamlConfiguration.loadConfiguration(file));
        }
    }
}
