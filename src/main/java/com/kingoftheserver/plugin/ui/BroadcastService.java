package com.kingoftheserver.plugin.ui;

import com.kingoftheserver.plugin.util.DurationUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.time.Duration;

/**
 * All player-facing chat, title and sound feedback for reign transitions. Kept separate
 * from {@link BossBarService} / {@link ScoreboardService} since those are continuously
 * updated displays, while this class only fires on discrete events.
 */
public final class BroadcastService {

    private static final Component DIVIDER = Component.text(
            "━━━━━━━━━━━━━━━━━━━━", NamedTextColor.GOLD, TextDecoration.STRIKETHROUGH);

    public void announceNewKing(String kingName, Duration reignLength, boolean playSound) {
        Component message = Component.empty()
                .append(Component.newline())
                .append(DIVIDER)
                .append(Component.newline())
                .append(Component.text("       ♕ KING OF THE SERVER", NamedTextColor.GOLD, TextDecoration.BOLD))
                .append(Component.newline())
                .append(DIVIDER)
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text("        " + kingName + " is now King!", NamedTextColor.YELLOW))
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text("       Reign: ", NamedTextColor.GRAY)
                        .append(Component.text(DurationUtil.formatClock(reignLength), NamedTextColor.WHITE)))
                .append(Component.newline())
                .append(DIVIDER)
                .append(Component.newline());

        Bukkit.broadcast(message);
        Bukkit.getServer().showTitle(Title.title(
                Component.text("♕ " + kingName, NamedTextColor.GOLD, TextDecoration.BOLD),
                Component.text("is now King of the Server!", NamedTextColor.YELLOW)));

        if (playSound) {
            Bukkit.getServer().playSound(net.kyori.adventure.sound.Sound.sound(
                    Sound.UI_TOAST_CHALLENGE_COMPLETE, net.kyori.adventure.sound.Sound.Source.MASTER, 1f, 1f));
        }
    }

    public void announceContinuedReign(String kingName, Duration newRoundLength) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendActionBar(Component.text(
                    kingName + "'s reign continues (+" + DurationUtil.formatClock(newRoundLength) + ")",
                    NamedTextColor.GOLD));
        }
    }

    public void announceOneMinuteWarning(String kingName) {
        Bukkit.broadcast(Component.text("⏳ Only one minute left in ", NamedTextColor.GOLD)
                .append(Component.text(kingName, NamedTextColor.YELLOW))
                .append(Component.text("'s reign!", NamedTextColor.GOLD)));
        Bukkit.getServer().playSound(net.kyori.adventure.sound.Sound.sound(
                Sound.BLOCK_NOTE_BLOCK_BELL, net.kyori.adventure.sound.Sound.Source.MASTER, 1f, 1.2f));
    }

    public void countdownTick(int secondsLeft) {
        Title title = Title.title(
                Component.text(secondsLeft, NamedTextColor.GOLD, TextDecoration.BOLD),
                Component.text("A new King is coming...", NamedTextColor.GRAY),
                Title.Times.times(Duration.ZERO, Duration.ofMillis(900), Duration.ZERO));
        Bukkit.getServer().showTitle(title);
        Bukkit.getServer().playSound(net.kyori.adventure.sound.Sound.sound(
                Sound.UI_BUTTON_CLICK, net.kyori.adventure.sound.Sound.Source.MASTER, 0.6f, 1.5f));
    }
}
