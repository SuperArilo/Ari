package ari.superarilo.tool;

import ari.superarilo.Ari;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class PermissionUtils {

    public Permission permission;

    public PermissionUtils() {
        if(Ari.instance.pluginManager.isPluginEnabled("Vault")) {
            RegisteredServiceProvider<Permission> registration = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
            if(registration != null) {
                this.permission = registration.getProvider();
                Log.info("Loaded permission plugin: " + this.permission.getName());
            } else {
                Log.error("No permission plugin is loaded");
            }
        } else {
            Log.warning("no vault, use default");
        }

    }
    public String getPlayerGroup(Player player) {
        return this.isNull() ? "default":this.permission.getPrimaryGroup(player);
    }
    protected boolean isNull() {
        return this.permission == null;
    }
}
