package com.tty.lib.tool;

import org.bukkit.Bukkit;
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
    public static List<String> filterByPrefix(List<String> list, String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return new ArrayList<>(list);
        }
        String lowerPrefix = prefix.toLowerCase();
        return list.stream()
                .filter(Objects::nonNull)
                .filter(s -> s.toLowerCase().startsWith(lowerPrefix))
                .toList();
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
}
