package ari.superarilo.enumType;

import ari.superarilo.command.MainCommand;
import ari.superarilo.command.lists.*;
import org.bukkit.command.TabExecutor;

import java.util.List;

public enum AriCommand {
    ARI("ari",
            List.of("ari"),
            "插件的主要指令",
            null,
            null,
            new MainCommand()),

    TPA("tpa",
            null,
            "传送指令",
            "ari.command.tpa",
            "无权限",
            new tpa()),
    TPAHERE("tpahere",
            null,
            "让玩家传送到您此处",
            "ari.command.tpahere",
            "无权限",
            new tpahere()),
    TPAACCEPT("tpaaccept",
            null,
            "接受此次传送请求",
            "ari.command.tpaaccept",
            "无权限",
            new tpaaccept()),
    TPAREFUSE("tparefuse",
            null,
            "拒绝当前的传送请求",
            "ari.command.tparefuse",
            "无权限",
            new tparefuse()),
    HOME("home",
            List.of("homes"),
            "打开您保存的home列表",
            "ari.command.home",
            "无权限",
            new home()),
    SETHOME("sethome",
            null,
            "将当前所处位置保存为您的家",
            "ari.command.sethome",
            "无权限",
            new sethome()),
    DELETEHOME("deletehome",
            null,
            "删除指定家",
            "ari.command.deletehome",
            "无权限",
            new deletehome()),
    RELOAD("reload",
            null,
            "重新加载插件",
            "ari.command.reload",
            "无权限",
            null);

    private final String show;
    private final List<String> aliases;
    private final String usage;
    private final String permission;
    private final String permissionMessage;
    private final TabExecutor commandClass;

    AriCommand(String show,
               List<String> aliases,
               String usage,
               String permission,
               String permissionMessage,
               TabExecutor commandClass){
        this.show = show;
        this.aliases = aliases;
        this.usage = usage;
        this.permission = permission;
        this.permissionMessage = permissionMessage;
        this.commandClass = commandClass;
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


    public TabExecutor getCommandClass() {
        return commandClass;
    }

}

