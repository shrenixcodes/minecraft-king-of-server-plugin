package com.kingoftheserver.plugin.command;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * A single {@code /king <name>} subcommand. Adding a new one means adding a new class
 * and registering it in {@link KingCommandExecutor}, rather than growing a single switch
 * statement.
 */
public interface KingSubcommand {

    String name();

    /** Permission required to run this subcommand, beyond the base {@code king.use}. */
    String permission();

    String usage();

    String description();

    void execute(CommandSender sender, String[] args);

    default List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
