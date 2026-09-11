package com.kingoftheserver.plugin.ui;

import com.kingoftheserver.plugin.util.DurationUtil;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.time.Duration;

/**
 * Maintains a single shared sidebar scoreboard assigned to every online player, showing
 * the current King, time remaining and round number. One shared {@link Scoreboard}
 * instance is reused for all viewers so an update only has to touch it once.
 */
public final class ScoreboardService {

    private static final String OBJECTIVE_NAME = "kots_sidebar";
    private static final String[] LINE_ENTRIES = {"kots_line_0", "kots_line_1", "kots_line_2", "kots_line_3"};

    private Scoreboard scoreboard;
    private Objective objective;

    public void show(Player player) {
        player.setScoreboard(ensureScoreboard());
    }

    public void hide(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    public void update(String kingName, Duration remaining, int roundNumber) {
        ensureScoreboard();
        setLine(0, NamedTextColor.WHITE, kingName, 3);
        setLine(1, NamedTextColor.GRAY, "Reign ends in " + DurationUtil.formatClock(remaining), 2);
        setLine(2, NamedTextColor.DARK_GRAY, "Round #" + roundNumber, 1);
    }

    public void shutdown() {
        if (objective != null) {
            objective.unregister();
        }
        scoreboard = null;
        objective = null;
    }

    private void setLine(int index, NamedTextColor color, String text, int score) {
        Score line = objective.getScore(LINE_ENTRIES[index]);
        line.customName(Component.text(text, color));
        line.numberFormat(NumberFormat.blank());
        line.setScore(score);
    }

    private Scoreboard ensureScoreboard() {
        if (scoreboard == null) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            objective = scoreboard.registerNewObjective(OBJECTIVE_NAME, Criteria.DUMMY,
                    Component.text("♕ King of the Server", NamedTextColor.GOLD));
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        return scoreboard;
    }
}
