package com.kingoftheserver.plugin.command;

import com.kingoftheserver.plugin.king.KingManager;
import com.kingoftheserver.plugin.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public final class KingSetSubcommand implements KingSubcommand {

    private final KingManager kingManager;

    public KingSetSubcommand(KingManager kingManager) {
        this.kingManager = kingManager;
    }

    @Override
    public String name() {
        return "set";
    }

    @Override
    public String permission() {
        return "king.admin.set";
    }

    @Override
    public String usage() {
        return "/king set <player>";
    }

    @Override
    public String description() {
        return "Manually assign a player as King.";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Messages.error("Usage: " + usage()));
            return;
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(Messages.error("Player '" + args[0] + "' is not online."));
            return;
        }
        kingManager.setKing(target);
        sender.sendMessage(Messages.success(target.getName() + " is now King."));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length != 1) {
            return List.of();
        }
        String partial = args[0].toLowerCase();
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(partial))
                .collect(Collectors.toList());
    }
}
