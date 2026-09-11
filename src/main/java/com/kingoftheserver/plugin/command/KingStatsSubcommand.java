package com.kingoftheserver.plugin.command;

import com.kingoftheserver.plugin.king.KingManager;
import com.kingoftheserver.plugin.king.KingStats;
import com.kingoftheserver.plugin.king.PlayerKingStats;
import com.kingoftheserver.plugin.util.DurationUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;

public final class KingStatsSubcommand implements KingSubcommand {

    private final KingManager kingManager;

    public KingStatsSubcommand(KingManager kingManager) {
        this.kingManager = kingManager;
    }

    @Override
    public String name() {
        return "stats";
    }

    @Override
    public String permission() {
        return null;
    }

    @Override
    public String usage() {
        return "/king stats";
    }

    @Override
    public String description() {
        return "Show King of the Server statistics.";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        KingStats stats = kingManager.stats();

        sender.sendMessage(Component.text("♕ King Statistics", NamedTextColor.GOLD));
        sender.sendMessage(Component.text(" Total rotations: ", NamedTextColor.GRAY)
                .append(Component.text(stats.totalRotations(), NamedTextColor.WHITE)));

        stats.mostSelected().ifPresent(top -> sender.sendMessage(Component.text(" Most crowned: ", NamedTextColor.GRAY)
                .append(Component.text(kingManager.displayNameOf(top.uuid()) + " (" + top.timesSelected() + "x)",
                        NamedTextColor.WHITE))));

        stats.longestReign().ifPresent(top -> sender.sendMessage(Component.text(" Longest reign: ", NamedTextColor.GRAY)
                .append(Component.text(kingManager.displayNameOf(top.uuid()) + " ("
                        + DurationUtil.formatClock(Duration.ofMillis(top.longestReignMillis())) + ")", NamedTextColor.WHITE))));

        if (sender instanceof Player player) {
            PlayerKingStats own = stats.statsFor(player.getUniqueId());
            sender.sendMessage(Component.text(" Your times crowned: ", NamedTextColor.GRAY)
                    .append(Component.text(own.timesSelected(), NamedTextColor.WHITE)));
            sender.sendMessage(Component.text(" Your total reign time: ", NamedTextColor.GRAY)
                    .append(Component.text(DurationUtil.formatClock(Duration.ofMillis(own.totalReignMillis())), NamedTextColor.WHITE)));
        }
    }
}
