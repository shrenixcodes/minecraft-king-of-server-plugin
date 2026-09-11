package com.kingoftheserver.plugin.command;

import com.kingoftheserver.plugin.king.KingManager;
import com.kingoftheserver.plugin.util.DurationUtil;
import com.kingoftheserver.plugin.util.Messages;
import org.bukkit.command.CommandSender;

public final class KingStatusSubcommand implements KingSubcommand {

    private final KingManager kingManager;

    public KingStatusSubcommand(KingManager kingManager) {
        this.kingManager = kingManager;
    }

    @Override
    public String name() {
        return "status";
    }

    @Override
    public String permission() {
        return null;
    }

    @Override
    public String usage() {
        return "/king status";
    }

    @Override
    public String description() {
        return "Show a concise rotation status.";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String rotation = kingManager.isRunning() ? "running" : "stopped";
        if (kingManager.currentKing().isPresent()) {
            String name = kingManager.displayNameOf(kingManager.currentKing().get());
            sender.sendMessage(Messages.info("Rotation " + rotation + " — King: " + name
                    + " — " + DurationUtil.formatClock(kingManager.remaining()) + " left."));
        } else {
            sender.sendMessage(Messages.info("Rotation " + rotation + " — no King has been chosen yet."));
        }
    }
}
