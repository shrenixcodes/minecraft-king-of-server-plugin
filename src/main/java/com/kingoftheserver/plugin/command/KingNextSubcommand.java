package com.kingoftheserver.plugin.command;

import com.kingoftheserver.plugin.king.KingManager;
import com.kingoftheserver.plugin.util.Messages;
import org.bukkit.command.CommandSender;

public final class KingNextSubcommand implements KingSubcommand {

    private final KingManager kingManager;

    public KingNextSubcommand(KingManager kingManager) {
        this.kingManager = kingManager;
    }

    @Override
    public String name() {
        return "next";
    }

    @Override
    public String permission() {
        return "king.admin.next";
    }

    @Override
    public String usage() {
        return "/king next";
    }

    @Override
    public String description() {
        return "Immediately end the current reign and choose the next King.";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        kingManager.forceNext();
        sender.sendMessage(Messages.success("Rotated to the next King."));
    }
}
