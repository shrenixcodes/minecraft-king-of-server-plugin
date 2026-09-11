package com.kingoftheserver.plugin.listener;

import com.kingoftheserver.plugin.king.KingManager;
import com.kingoftheserver.plugin.ui.UiManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Keeps per-player UI state (boss bar, scoreboard) and the King manager's bookkeeping in
 * sync as players come and go, so a leaving King never corrupts round state and a
 * joining player immediately sees the current status.
 */
public final class PlayerConnectionListener implements Listener {

    private final KingManager kingManager;
    private final UiManager uiManager;

    public PlayerConnectionListener(KingManager kingManager, UiManager uiManager) {
        this.kingManager = kingManager;
        this.uiManager = uiManager;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        uiManager.registerPlayer(event.getPlayer());
        kingManager.handlePlayerJoin(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        kingManager.handlePlayerQuit(event.getPlayer());
        uiManager.unregisterPlayer(event.getPlayer());
    }
}
