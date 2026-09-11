package com.kingoftheserver.plugin.command;

import com.kingoftheserver.plugin.config.ConfigManager;
import com.kingoftheserver.plugin.king.KingManager;
import com.kingoftheserver.plugin.util.DurationUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import io.papermc.paper.plugin.configuration.PluginMeta;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class KingInfoSubcommand implements KingSubcommand {

    private final KingManager kingManager;
    private final ConfigManager configManager;
    private final JavaPlugin plugin;

    public KingInfoSubcommand(KingManager kingManager, ConfigManager configManager, JavaPlugin plugin) {
        this.kingManager = kingManager;
        this.configManager = configManager;
        this.plugin = plugin;
    }

    @Override
    public String name() {
        return "info";
    }

    @Override
    public String permission() {
        return null;
    }

    @Override
    public String usage() {
        return "/king info";
    }

    @Override
    public String description() {
        return "Show detailed King and rotation information.";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        PluginMeta description = plugin.getPluginMeta();

        sender.sendMessage(Component.text("♕ King of the Server", NamedTextColor.GOLD));
        if (kingManager.currentKing().isPresent()) {
            String name = kingManager.displayNameOf(kingManager.currentKing().get());
            sender.sendMessage(Component.text(" King: ", NamedTextColor.GRAY)
                    .append(Component.text(name, NamedTextColor.YELLOW)));
            sender.sendMessage(Component.text(" Time remaining: ", NamedTextColor.GRAY)
                    .append(Component.text(DurationUtil.formatClock(kingManager.remaining()), NamedTextColor.WHITE)));
        } else {
            sender.sendMessage(Component.text(" King: ", NamedTextColor.GRAY)
                    .append(Component.text("none yet", NamedTextColor.WHITE)));
        }
        sender.sendMessage(Component.text(" Round: ", NamedTextColor.GRAY)
                .append(Component.text("#" + kingManager.roundNumber(), NamedTextColor.WHITE)));
        sender.sendMessage(Component.text(" Rotation: ", NamedTextColor.GRAY)
                .append(Component.text(kingManager.isRunning() ? "running" : "stopped",
                        kingManager.isRunning() ? NamedTextColor.GREEN : NamedTextColor.RED)));
        sender.sendMessage(Component.text(" Reign length: ", NamedTextColor.GRAY)
                .append(Component.text(DurationUtil.formatClock(configManager.current().kingDuration()), NamedTextColor.WHITE)));
        sender.sendMessage(Component.text(" Selection mode: ", NamedTextColor.GRAY)
                .append(Component.text(configManager.current().selectionMode().name().toLowerCase(), NamedTextColor.WHITE)));
        sender.sendMessage(Component.text(" Plugin version: ", NamedTextColor.GRAY)
                .append(Component.text(description.getVersion(), NamedTextColor.WHITE)));
    }
}
