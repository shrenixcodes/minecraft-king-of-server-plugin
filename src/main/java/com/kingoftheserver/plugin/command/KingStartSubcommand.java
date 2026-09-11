package com.kingoftheserver.plugin.command;

import com.kingoftheserver.plugin.king.KingManager;
import com.kingoftheserver.plugin.util.Messages;
import org.bukkit.command.CommandSender;

public final class KingStartSubcommand implements KingSubcommand {

    private final KingManager kingManager;

    public KingStartSubcommand(KingManager kingManager) {
        this.kingManager = kingManager;
    }

    @Override
    public String name() {
        return "start";
    }

    @Override
    public String permission() {
        return "king.admin.start";
    }

    @Override
    public String usage() {
        return "/king start";
    }

    @Override
    public String description() {
        return "Start the King rotation.";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (kingManager.start()) {
            sender.sendMessage(Messages.success("King rotation started."));
        } else {
            sender.sendMessage(Messages.error("King rotation is already running."));
        }
    }
}
