package ari.superarilo.enumType;

import ari.superarilo.command.MainCommand;
import ari.superarilo.command.lists.*;
import org.bukkit.command.TabExecutor;

public enum AriCommand {
    ARI("ari",
            null,
            new MainCommand()),
    TPA("tpa",
            "ari.command.tpa",
            new tpa()),
    TPAHERE("tpahere",
            "ari.command.tpahere",
            new tpahere()),
    TPAACCEPT("tpaaccept",
            "ari.command.tpaaccept",
            new tpaaccept()),
    TPAREFUSE("tparefuse",
            "ari.command.tparefuse",
            new tparefuse()),
    HOME("home",
            "ari.command.home",
            new home()),
    SETHOME("sethome",
            "ari.command.sethome",
            new sethome()),
    DELETEHOME("deletehome",
            "ari.command.deletehome",
            new deletehome()),
    BACK("back",
            "ari.command.back",
            new back()),
    WARP("warp",
            "ari.command.warp",
            new warp()),
    SETWARP("setwarp",
            "ari.command.setwarp",
            new setwarp()),
    DELETEWARP("deletewarp",
            "ari.command.deletewarp",
            new deletewarp()),
    RELOAD("reload",
            "ari.command.reload",
            null);

    private final String show;
    private final String permission;
    private final TabExecutor commandClass;

    AriCommand(String show,
               String permission,
               TabExecutor commandClass){
        this.show = show;
        this.permission = permission;
        this.commandClass = commandClass;
    }


    public String getShow() {
        return show;
    }

    public String getPermission() {
        return permission;
    }

    public TabExecutor getCommandClass() {
        return commandClass;
    }

}

