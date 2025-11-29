package com.tty.lib.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.tty.lib.tool.LibConfigUtils;
import com.tty.lib.tool.PermissionUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

@SuppressWarnings("SameReturnValue")
public abstract class BaseCommand<T> implements SuperHandsomeCommand {

    @Override
    public boolean isDisabledInGame(CommandSender sender, @NonNull YamlConfiguration configuration) {
        boolean b = configuration.getBoolean("main.enable", true);
        if (!b) {
            sender.sendMessage(LibConfigUtils.t("base.command.disabled"));
        }
        return b;
    }

    private final boolean allowConsole;
    private final ArgumentType<T> type;
    private final int correctArgsLength;

    protected BaseCommand(boolean allowConsole, ArgumentType<T> type, int correctArgsLength) {
        this.allowConsole = allowConsole;
        this.type = type;
        this.correctArgsLength = correctArgsLength;
    }

    public abstract List<SuperHandsomeCommand> getSubCommands();

    public abstract List<String> tabSuggestions(CommandSender sender, String[] args);

    @Override
    public LiteralCommandNode<CommandSourceStack> toBrigadier() {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(this.name());
        builder.requires(ctx -> PermissionUtils.hasPermission(ctx.getSender(), this.permission()));
        builder.executes(this::baseExecute);
        if(this.getSubCommands().isEmpty()) {
            var nodeArgs = Commands.argument("args", type)
                .requires(ctx -> PermissionUtils.hasPermission(ctx.getSender(), this.permission()))
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
            builder.then(nodeArgs);
        } else {
            for (SuperHandsomeCommand subCommand : this.getSubCommands()) {
                builder.then(subCommand.toBrigadier());
            }
        }
        return builder.build();
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
        String input = ctx.getInput().replace("ari ", "").trim();
        String[] args = input.isEmpty() ? new String[0] : input.split(" ");
        if (args.length != this.correctArgsLength) {
            sender.sendMessage(LibConfigUtils.t("function.public.fail"));
            return SINGLE_SUCCESS;
        }
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("\"") && arg.endsWith("\"") && arg.length() >= 2) {
                args[i] = arg.substring(1, arg.length() - 1);
            }
        }
        this.execute(sender, args);

        return SINGLE_SUCCESS;
    }

}
