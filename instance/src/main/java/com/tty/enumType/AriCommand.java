package com.tty.enumType;

import com.tty.command.MainCommand;
import com.tty.command.lists.*;
import lombok.Getter;
import org.bukkit.command.TabExecutor;

@Getter
public enum AriCommand {
    ARI("ari",
            null,
            new MainCommand()),
    TP("tp",
            "ari.command.tp",
            new tp()),
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
    SPAWN("spawn",
            "ari.command.spawn",
            new spawn()),
    SETSPAWN("setspawn",
            "ari.command.setspawn",
            new setspawn()),
    SETWARP("setwarp",
            "ari.command.setwarp",
            new setwarp()),
    DELETEWARP("deletewarp",
            "ari.command.deletewarp",
            new deletewarp()),
    TIME("time",
            "ari.command.time",
            new time()),
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
}

