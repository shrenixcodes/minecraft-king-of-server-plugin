package com.kingoftheserver.plugin.ui;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.time.Duration;

/**
 * Displays the reign countdown in a shared boss bar visible to every online player.
 */
public final class BossBarService {

    private BossBar bossBar;
    private BossBar.Color color = BossBar.Color.YELLOW;
    private BossBar.Overlay overlay = BossBar.Overlay.NOTCHED_10;

    public void configure(String colorName, String overlayName) {
        this.color = parseColor(colorName);
        this.overlay = parseOverlay(overlayName);
        if (bossBar != null) {
            bossBar.color(color);
            bossBar.overlay(overlay);
        }
    }

    public void show(Player player) {
        ensureBar().addViewer(player);
    }

    public void hide(Player player) {
        if (bossBar != null) {
            bossBar.removeViewer(player);
        }
    }

    public void update(String kingName, Duration remaining, Duration total) {
        BossBar bar = ensureBar();
        bar.name(Component.text("♕ King ", NamedTextColor.GOLD)
                .append(Component.text(kingName, NamedTextColor.YELLOW))
                .append(Component.text(" — reign ends in ", NamedTextColor.GOLD))
                .append(Component.text(com.kingoftheserver.plugin.util.DurationUtil.formatClock(remaining), NamedTextColor.WHITE)));

        long totalMillis = Math.max(1, total.toMillis());
        float progress = (float) Math.max(0.0, Math.min(1.0, remaining.toMillis() / (double) totalMillis));
        bar.progress(progress);
    }

    public void clear() {
        if (bossBar != null) {
            bossBar.name(Component.text("King of the Server", NamedTextColor.GOLD));
            bossBar.progress(1f);
        }
    }

    public void shutdown() {
        if (bossBar != null) {
            for (Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
                bossBar.removeViewer(player);
            }
            bossBar = null;
        }
    }

    private BossBar ensureBar() {
        if (bossBar == null) {
            bossBar = BossBar.bossBar(Component.text("King of the Server", NamedTextColor.GOLD), 1f, color, overlay);
        }
        return bossBar;
    }

    private static BossBar.Color parseColor(String raw) {
        try {
            return BossBar.Color.valueOf(raw.trim().toUpperCase());
        } catch (Exception e) {
            return BossBar.Color.YELLOW;
        }
    }

    private static BossBar.Overlay parseOverlay(String raw) {
        try {
            return BossBar.Overlay.valueOf(raw.trim().toUpperCase());
        } catch (Exception e) {
            return BossBar.Overlay.NOTCHED_10;
        }
    }
}
