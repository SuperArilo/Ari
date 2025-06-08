package com.tty.lib.tool;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

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

}
