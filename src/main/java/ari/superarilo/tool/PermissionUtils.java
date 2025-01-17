package ari.superarilo.tool;

import ari.superarilo.Ari;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
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

    /**
     * 返回指定玩家的组id名称
     * @param player 被检查玩家
     * @return 返回的组id名称
     */
    public String getPlayerGroup(Player player) {
        return this.isNull() ? "default":this.permission.getPrimaryGroup(player);
    }

    /**
     * 返回玩家是否具有对应的权限
     * @param player 被检查玩家
     * @param permission 权限字符串
     * @return 布尔值
     */
    public boolean hasPermission(Player player, String permission) {
        return this.isNull() ? player.hasPermission(permission):this.permission.has(player, permission);
    }

    /**
     * 返回玩家是否具有对应的权限
     * @param sender 被检查玩家
     * @param permission 权限字符串
     * @return 布尔值
     */
    public boolean hasPermission(CommandSender sender, String permission) {
        return this.isNull() ? sender.hasPermission(permission):this.permission.has(sender, permission);
    }
    protected boolean isNull() {
        return this.permission == null;
    }
}
