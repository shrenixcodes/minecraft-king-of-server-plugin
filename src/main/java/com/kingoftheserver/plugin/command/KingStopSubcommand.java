package com.kingoftheserver.plugin.command;

import com.kingoftheserver.plugin.king.KingManager;
import com.kingoftheserver.plugin.util.Messages;
import org.bukkit.command.CommandSender;

public final class KingStopSubcommand implements KingSubcommand {

    private final KingManager kingManager;

    public KingStopSubcommand(KingManager kingManager) {
        this.kingManager = kingManager;
    }

    @Override
    public String name() {
        return "stop";
    }

    @Override
    public String permission() {
        return "king.admin.stop";
    }

    @Override
    public String usage() {
        return "/king stop";
    }

    @Override
    public String description() {
        return "Stop the King rotation.";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (kingManager.stop()) {
            sender.sendMessage(Messages.success("King rotation stopped. The current King keeps the crown until you start it again."));
        } else {
            sender.sendMessage(Messages.error("King rotation is already stopped."));
        }
    }
}
