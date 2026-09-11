package com.kingoftheserver.plugin.ui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * Marks a player as King in a way that is visible in-world without ever touching their
 * inventory: a glowing outline plus a crown prefix on their nametag and in the tab
 * list. A purely visual crown avoids item-duplication and inventory-loss edge cases
 * entirely (death, logout, world change all just work), which is why it was chosen
 * over a physical crown item.
 */
public final class CrownService {

    private static final String TEAM_NAME = "kots_crown";

    private Team crownTeam;

    public void crown(Player player) {
        Team team = crownTeam(player);
        team.addEntry(player.getName());
        player.setGlowing(true);
    }

    public void depose(Player player) {
        player.setGlowing(false);
        if (crownTeam != null) {
            crownTeam.removeEntry(player.getName());
        }
    }

    private Team crownTeam(Player player) {
        Scoreboard scoreboard = player.getServer().getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getTeam(TEAM_NAME);
        if (team == null) {
            team = scoreboard.registerNewTeam(TEAM_NAME);
            team.prefix(Component.text("♕ ", NamedTextColor.GOLD));
            team.color(NamedTextColor.GOLD);
        }
        crownTeam = team;
        return team;
    }

    /** Removes the crown team entirely, used when the plugin is disabled. */
    public void shutdown() {
        if (crownTeam != null) {
            crownTeam.getEntries().forEach(entry -> {
                Player player = Bukkit.getPlayerExact(entry);
                if (player != null) {
                    player.setGlowing(false);
                }
            });
            crownTeam.unregister();
            crownTeam = null;
        }
    }
}
