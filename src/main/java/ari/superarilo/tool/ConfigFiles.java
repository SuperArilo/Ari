package ari.superarilo.tool;

import ari.superarilo.Ari;
import ari.superarilo.enumType.FilePath;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigFiles {

    public static Map<String, FileConfiguration> configs = new ConcurrentHashMap<>();

    public static void reloadAllConfig() {
        Ari.instance.saveDefaultConfig();
        Ari.instance.reloadConfig();
        checkFiles();
    }
    public static void checkFiles() {
        configs = new ConcurrentHashMap<>();
        for (FilePath filePath : FilePath.values()) {
            String path = filePath.getPath();
            File file = new File(Ari.instance.getDataFolder(), path);
            Ari.instance.saveResource(path, true);
//            if(!file.exists()) {
//                Ari.instance.saveResource(path, false);
//            }
            configs.put(filePath.getName(), YamlConfiguration.loadConfiguration(file));
        }
    }
}
