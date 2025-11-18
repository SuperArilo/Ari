package com.tty.lib.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.tty.lib.tool.LibConfigUtils;
import com.tty.lib.tool.PermissionUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

@SuppressWarnings("SameReturnValue")
public abstract class BaseCommand<T> implements SuperHandsomeCommand {

    private final boolean allowConsole;
    private final ArgumentType<T> type;

    protected BaseCommand(boolean allowConsole, ArgumentType<T> type) {
        this.allowConsole = allowConsole;
        this.type = type;
    }

    public abstract List<SuperHandsomeCommand> getSubCommands();

    public abstract List<String> tabSuggestions(CommandSender sender, String[] args);

    @Override
    public LiteralCommandNode<CommandSourceStack> toBrigadier() {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(this.name());
        RequiredArgumentBuilder<CommandSourceStack, T> argNode = RequiredArgumentBuilder.argument("args", type);

        if (this.name().equals("ari")) {
            argNode
               .executes(this::baseExecute)
               .suggests((ctx, b) -> {
                    for (SuperHandsomeCommand subCommand : this.getSubCommands()) {
                        if (PermissionUtils.hasPermission(ctx.getSource().getSender(), subCommand.getPermission())) {
                            b.suggest(subCommand.getName());
                        }
                    }
                    return b.buildFuture();
                });
        } else {
            argNode.requires(ctx -> PermissionUtils.hasPermission(ctx.getSender(), this.permission()))
                .executes(this::baseExecute)
                .suggests((ctx, b) -> {
                    String input = ctx.getInput();
                    String[] args = input.split(" ");
                    String currentLower = input.endsWith(" ") ? "" : args[args.length - 1].toLowerCase();
                    for (String suggestion : this.tabSuggestions(ctx.getSource().getSender(), args)) {
                        if (currentLower.isEmpty() || suggestion.toLowerCase().startsWith(currentLower)) {
                            b.suggest(suggestion);
                        }
                    }
                    return b.buildFuture();
                });
        }
        builder.requires(ctx -> PermissionUtils.hasPermission(ctx.getSender(), this.permission()));
        builder.executes(this::baseExecute);

        builder.then(argNode);
        return builder.build();
    }

    @Override
    public String getName() {
        return this.name();
    }

    @Override
    public String getPermission() {
        return this.permission();
    }

    public abstract void execute(CommandSender sender, String[] args);

    public abstract String name();

    public abstract String permission();

    private int baseExecute(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        if (!this.allowConsole && !(sender instanceof Player)) {
            sender.sendMessage(LibConfigUtils.t("function.public.not-player"));
            return SINGLE_SUCCESS;
        }
        if(!PermissionUtils.hasPermission(sender, this.permission())) {
            sender.sendMessage(LibConfigUtils.t("base.permission.no-permission"));
            return SINGLE_SUCCESS;
        }
        this.execute(sender, ctx.getInput().replace("ari ", "").split(" "));
        return SINGLE_SUCCESS;
    }
}
