package com.kingoftheserver.plugin.king;

import java.util.UUID;

/**
 * Cumulative King-related statistics for a single player. Mutable by design since it is
 * updated incrementally as rounds happen and persisted to disk periodically.
 */
public final class PlayerKingStats {

    private final UUID uuid;
    private int timesSelected;
    private long totalReignMillis;
    private long longestReignMillis;

    public PlayerKingStats(UUID uuid) {
        this.uuid = uuid;
    }

    public PlayerKingStats(UUID uuid, int timesSelected, long totalReignMillis, long longestReignMillis) {
        this.uuid = uuid;
        this.timesSelected = timesSelected;
        this.totalReignMillis = totalReignMillis;
        this.longestReignMillis = longestReignMillis;
    }

    void recordSelected() {
        timesSelected++;
    }

    void recordReignEnded(long reignMillis) {
        totalReignMillis += reignMillis;
        if (reignMillis > longestReignMillis) {
            longestReignMillis = reignMillis;
        }
    }

    public UUID uuid() {
        return uuid;
    }

    public int timesSelected() {
        return timesSelected;
    }

    public long totalReignMillis() {
        return totalReignMillis;
    }

    public long longestReignMillis() {
        return longestReignMillis;
    }
}
