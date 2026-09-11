package com.kingoftheserver.plugin.listener;

import com.kingoftheserver.plugin.ability.AbilityManager;
import com.kingoftheserver.plugin.king.KingManager;
import com.kingoftheserver.plugin.ui.UiManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Handles player-state transitions that matter to the King system but do not change
 * who the King is:
 *
 * <ul>
 *   <li>Death never ends a reign, so no death handler exists here at all. Vanilla Minecraft
 *       does, however, clear potion effects on death, so this listener reapplies the
 *       King's abilities and crown visuals one tick after respawn.</li>
 *   <li>Sneaking gives triggered abilities (Royal Shield) a chance to activate.</li>
 * </ul>
 *
 * <p>Game mode and world changes intentionally have no handlers: potion effects, the
 * glowing crown indicator and the scoreboard/boss bar all persist automatically across
 * both, and eligibility is only enforced when the next King is chosen, not retroactively
 * against the player currently on the throne.</p>
 */
public final class PlayerStateListener implements Listener {

    private final JavaPlugin plugin;
    private final KingManager kingManager;
    private final AbilityManager abilityManager;
    private final UiManager uiManager;

    public PlayerStateListener(JavaPlugin plugin, KingManager kingManager, AbilityManager abilityManager,
                                UiManager uiManager) {
        this.plugin = plugin;
        this.kingManager = kingManager;
        this.abilityManager = abilityManager;
        this.uiManager = uiManager;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (!isCurrentKing(player)) {
            return;
        }
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (player.isOnline() && isCurrentKing(player)) {
                abilityManager.crown(player);
                uiManager.onCrowned(player);
            }
        });
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if (event.isSneaking() && isCurrentKing(event.getPlayer())) {
            abilityManager.handleSneakTrigger(event.getPlayer());
        }
    }

    private boolean isCurrentKing(Player player) {
        return kingManager.currentKing().map(uuid -> uuid.equals(player.getUniqueId())).orElse(false);
    }
}
