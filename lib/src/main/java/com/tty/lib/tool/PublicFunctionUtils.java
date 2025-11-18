package com.tty.lib.tool;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;

public class PublicFunctionUtils {

    public static <T> void loadPlugin(String pluginName, Class<T> tClass, Consumer<T> consumer, Runnable runnable) {
        if (Bukkit.getPluginManager().isPluginEnabled(pluginName)) {
            RegisteredServiceProvider<T> registration = Bukkit.getServer().getServicesManager().getRegistration(tClass);
            if (registration != null) {
                consumer.accept(registration.getProvider());
            } else {
                runnable.run();
            }
        } else {
            runnable.run();
        }
    }

    public static <K, V> Map<K, V> deepCopyBySerialization(Map<K, V> original) {
        if (original.isEmpty()) {
            return new HashMap<>();
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(original);
            oos.flush();
            try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                 ObjectInputStream ois = new ObjectInputStream(bais)) {
                @SuppressWarnings("unchecked")
                Map<K, V> copy = (Map<K, V>) ois.readObject();
                return copy;
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalArgumentException("Deep copy failed: " + e.getMessage(), e);
        }
    }

    /**
     * 检查材质是否是ITEM
     * @param material 被检查的材质
     * @return 返回一个正确的材质
     */
    public static Material checkIsItem(Material material) {
        if(!material.isItem() || !material.isSolid()) {
            return Material.DIRT;
        }
        return material;
    }

}
