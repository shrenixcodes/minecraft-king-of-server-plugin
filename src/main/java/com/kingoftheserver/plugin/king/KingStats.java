package com.kingoftheserver.plugin.king;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Aggregates global and per-player King statistics. Thread-safe for the simple case of
 * the main server thread reading while an async save task serializes a snapshot.
 */
public final class KingStats {

    private final Map<UUID, PlayerKingStats> playerStats = new ConcurrentHashMap<>();
    private int totalRotations;

    public void recordSelected(UUID uuid) {
        statsFor(uuid).recordSelected();
    }

    public void recordReignEnded(UUID uuid, long reignMillis) {
        statsFor(uuid).recordReignEnded(reignMillis);
    }

    public void recordRotation() {
        totalRotations++;
    }

    public PlayerKingStats statsFor(UUID uuid) {
        return playerStats.computeIfAbsent(uuid, PlayerKingStats::new);
    }

    public Optional<PlayerKingStats> get(UUID uuid) {
        return Optional.ofNullable(playerStats.get(uuid));
    }

    public void restore(UUID uuid, int timesSelected, long totalReignMillis, long longestReignMillis) {
        playerStats.put(uuid, new PlayerKingStats(uuid, timesSelected, totalReignMillis, longestReignMillis));
    }

    public void restoreTotalRotations(int totalRotations) {
        this.totalRotations = totalRotations;
    }

    public int totalRotations() {
        return totalRotations;
    }

    public Collection<PlayerKingStats> allPlayerStats() {
        return playerStats.values();
    }

    public Optional<PlayerKingStats> longestReign() {
        return playerStats.values().stream()
                .max((a, b) -> Long.compare(a.longestReignMillis(), b.longestReignMillis()))
                .filter(stats -> stats.longestReignMillis() > 0);
    }

    public Optional<PlayerKingStats> mostSelected() {
        return playerStats.values().stream()
                .max((a, b) -> Integer.compare(a.timesSelected(), b.timesSelected()))
                .filter(stats -> stats.timesSelected() > 0);
    }
}
