package com.kingoftheserver.plugin.command;

import com.kingoftheserver.plugin.config.ConfigManager;
import com.kingoftheserver.plugin.king.KingManager;
import com.kingoftheserver.plugin.util.Messages;
import org.bukkit.command.CommandSender;

public final class KingReloadSubcommand implements KingSubcommand {

    private final ConfigManager configManager;
    private final KingManager kingManager;

    public KingReloadSubcommand(ConfigManager configManager, KingManager kingManager) {
        this.configManager = configManager;
        this.kingManager = kingManager;
    }

    @Override
    public String name() {
        return "reload";
    }

    @Override
    public String permission() {
        return "king.admin.reload";
    }

    @Override
    public String usage() {
        return "/king reload";
    }

    @Override
    public String description() {
        return "Reload the configuration without restarting the server.";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            configManager.load();
            kingManager.reloadSelector();
            sender.sendMessage(Messages.success("Configuration reloaded."));
        } catch (Exception e) {
            sender.sendMessage(Messages.error("Failed to reload configuration: " + e.getMessage()));
        }
    }
}
