package com.kingoftheserver.plugin.ui;

import org.bukkit.Particle;
import org.bukkit.entity.Player;

/**
 * A restrained particle effect above the King's head, refreshed once per second by the
 * same heartbeat that updates the rest of the UI rather than its own repeating task.
 */
public final class ParticleService {

    public void pulse(Player king) {
        king.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, king.getLocation().add(0, 2.3, 0),
                2, 0.25, 0.1, 0.25, 0.0);
    }
}
