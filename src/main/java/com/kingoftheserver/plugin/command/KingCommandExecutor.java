package com.kingoftheserver.plugin.command;

import com.kingoftheserver.plugin.king.KingManager;
import com.kingoftheserver.plugin.util.DurationUtil;
import com.kingoftheserver.plugin.util.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Dispatches {@code /king ...} to the matching {@link KingSubcommand}. Adding a new
 * subcommand only requires constructing it in
 * {@link com.kingoftheserver.plugin.KingOfTheServerPlugin} and passing it here; this
 * class never needs a new branch for it beyond that registration.
 */
public final class KingCommandExecutor implements CommandExecutor, TabCompleter {

    private static final String BASE_PERMISSION = "king.use";

    private final KingManager kingManager;
    private final Map<String, KingSubcommand> subcommands = new LinkedHashMap<>();

    public KingCommandExecutor(KingManager kingManager, List<KingSubcommand> subcommands) {
        this.kingManager = kingManager;
        for (KingSubcommand subcommand : subcommands) {
            this.subcommands.put(subcommand.name().toLowerCase(), subcommand);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(BASE_PERMISSION)) {
            sender.sendMessage(Messages.error("You do not have permission to use this command."));
            return true;
        }

        if (args.length == 0) {
            showDefault(sender);
            return true;
        }

        KingSubcommand subcommand = subcommands.get(args[0].toLowerCase());
        if (subcommand == null) {
            sender.sendMessage(Messages.error("Unknown subcommand '" + args[0] + "'. Try /king info."));
            return true;
        }

        if (subcommand.permission() != null && !sender.hasPermission(subcommand.permission())) {
            sender.sendMessage(Messages.error("You do not have permission to use " + subcommand.usage() + "."));
            return true;
        }

        String[] remainingArgs = args.length > 1
                ? java.util.Arrays.copyOfRange(args, 1, args.length)
                : new String[0];
        subcommand.execute(sender, remainingArgs);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            return subcommands.values().stream()
                    .filter(sub -> sub.permission() == null || sender.hasPermission(sub.permission()))
                    .map(KingSubcommand::name)
                    .filter(name -> name.startsWith(partial))
                    .collect(Collectors.toList());
        }
        if (args.length > 1) {
            KingSubcommand subcommand = subcommands.get(args[0].toLowerCase());
            if (subcommand != null && (subcommand.permission() == null || sender.hasPermission(subcommand.permission()))) {
                return subcommand.tabComplete(sender, java.util.Arrays.copyOfRange(args, 1, args.length));
            }
        }
        return List.of();
    }

    private void showDefault(CommandSender sender) {
        if (kingManager.currentKing().isPresent()) {
            String name = kingManager.displayNameOf(kingManager.currentKing().get());
            sender.sendMessage(Component.text("♕ ", NamedTextColor.GOLD)
                    .append(Component.text(name, NamedTextColor.YELLOW))
                    .append(Component.text(" is King — ", NamedTextColor.GOLD))
                    .append(Component.text(DurationUtil.formatClock(kingManager.remaining()), NamedTextColor.WHITE))
                    .append(Component.text(" remaining", NamedTextColor.GOLD)));
        } else {
            sender.sendMessage(Messages.info("No King has been chosen yet."));
        }
    }
}
