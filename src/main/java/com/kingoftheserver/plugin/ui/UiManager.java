package com.kingoftheserver.plugin.ui;

import com.kingoftheserver.plugin.config.ConfigManager;
import com.kingoftheserver.plugin.config.PluginConfig;
import com.kingoftheserver.plugin.config.UiSettings;
import com.kingoftheserver.plugin.util.DurationUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;

/**
 * Single entry point {@link com.kingoftheserver.plugin.king.KingManager} uses to drive
 * every visual and audible signal of who the King is and how much time is left. Each
 * concern (boss bar, scoreboard, particles, crown, chat/title broadcasts) lives in its
 * own small service so a contributor can change one without touching the others.
 */
public final class UiManager {

    private final ConfigManager configManager;
    private final BossBarService bossBarService = new BossBarService();
    private final ScoreboardService scoreboardService = new ScoreboardService();
    private final ParticleService particleService = new ParticleService();
    private final CrownService crownService = new CrownService();
    private final BroadcastService broadcastService = new BroadcastService();

    public UiManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void registerPlayer(Player player) {
        UiSettings ui = configManager.current().ui();
        if (ui.bossBarEnabled()) {
            bossBarService.configure(ui.bossBarColor(), ui.bossBarStyle());
            bossBarService.show(player);
        }
        if (ui.scoreboardEnabled()) {
            scoreboardService.show(player);
        }
    }

    public void unregisterPlayer(Player player) {
        bossBarService.hide(player);
        scoreboardService.hide(player);
    }

    public void onCrowned(Player king) {
        crownService.crown(king);
    }

    public void onDeposed(Player previousKing) {
        crownService.depose(previousKing);
    }

    public void tick(String kingName, Player kingPlayer, Duration remaining, Duration total, int roundNumber) {
        UiSettings ui = configManager.current().ui();

        if (ui.bossBarEnabled()) {
            bossBarService.update(kingName, remaining, total);
        }
        if (ui.scoreboardEnabled()) {
            scoreboardService.update(kingName, remaining, roundNumber);
        }
        if (ui.actionBarEnabled()) {
            Component actionBar = Component.text("♕ " + kingName + " is King — ", NamedTextColor.GOLD)
                    .append(Component.text(DurationUtil.formatClock(remaining), NamedTextColor.YELLOW))
                    .append(Component.text(" remaining", NamedTextColor.GOLD));
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendActionBar(actionBar);
            }
        }
        if (ui.particlesEnabled() && kingPlayer != null) {
            particleService.pulse(kingPlayer);
        }
    }

    public void announceNewKing(String kingName, Duration reignLength) {
        PluginConfig config = configManager.current();
        if (config.messages().announceNewKing()) {
            broadcastService.announceNewKing(kingName, reignLength, config.ui().soundsEnabled());
        }
    }

    public void announceContinuedReign(String kingName, Duration newRoundLength) {
        broadcastService.announceContinuedReign(kingName, newRoundLength);
    }

    public void announceOneMinuteWarning(String kingName) {
        if (configManager.current().messages().announceOneMinuteWarning()) {
            broadcastService.announceOneMinuteWarning(kingName);
        }
    }

    public void countdownTick(int secondsLeft) {
        broadcastService.countdownTick(secondsLeft);
    }

    public void shutdown() {
        bossBarService.shutdown();
        scoreboardService.shutdown();
        crownService.shutdown();
    }
}
