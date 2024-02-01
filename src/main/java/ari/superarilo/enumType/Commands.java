package ari.superarilo.enumType;

import ari.superarilo.command.Arilo;
import ari.superarilo.command.Reload;
import ari.superarilo.command.tpa.Tpa;
import ari.superarilo.command.tpa.Tpahere;
import org.bukkit.command.TabExecutor;

import java.util.Arrays;
import java.util.List;

public enum Commands {
    ARILO("arilo",
            List.of("ari"),
            "插件的主要指令",
            null,
            null,
            new Arilo(),
            false,
            Arrays.asList("tpa", "tpahere", "reload")),

    TPA("tpa",
            null,
            "传送指令",
            "ari.command.tpa",
            "无权限",
            new Tpa(),
            false,
            null),
    TPAHERE("tpahere",
            null,
            "让玩家传送到您此处",
            "ari.command.tpahere",
            "无权限",
            new Tpahere(),
            false,
            null),
    RELOAD("reload",
            null,
            "重载插件",
            "ari.admin.command.reload",
            "无权限",
            new Reload(),
            true,
            null),
    NONE;

    private String show;
    private List<String> aliases;
    private String usage;
    private String permission;
    private String permissionMessage;
    private TabExecutor commandClass;
    private Boolean isAdmin;
    private List<String> tabCompleteList;

    Commands(String show,
             List<String> aliases,
             String usage,
             String permission,
             String permissionMessage,
             TabExecutor commandClass,
             Boolean isAdmin,
             List<String> tabCompleteList){
        this.show = show;
        this.aliases = aliases;
        this.usage = usage;
        this.permission = permission;
        this.permissionMessage = permissionMessage;
        this.commandClass = commandClass;
        this.isAdmin = isAdmin;
        this.tabCompleteList = tabCompleteList;
    }

    Commands() {
    }

    public String getShow() {
        return show;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public String getUsage() {
        return usage;
    }

    public String getPermission() {
        return permission;
    }

    public String getPermissionMessage() {
        return permissionMessage;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public TabExecutor getCommandClass() {
        return commandClass;
    }

    public List<String> getTabCompleteList() {
        return tabCompleteList;
    }
}

