package com.kingoftheserver.plugin.king;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KingStatsTest {

    @Test
    void newPlayerHasZeroedStats() {
        KingStats stats = new KingStats();
        UUID player = UUID.randomUUID();

        assertEquals(0, stats.statsFor(player).timesSelected());
        assertEquals(0L, stats.statsFor(player).totalReignMillis());
        assertEquals(0L, stats.statsFor(player).longestReignMillis());
    }

    @Test
    void recordSelectedIncrementsCount() {
        KingStats stats = new KingStats();
        UUID player = UUID.randomUUID();

        stats.recordSelected(player);
        stats.recordSelected(player);

        assertEquals(2, stats.statsFor(player).timesSelected());
    }

    @Test
    void recordReignEndedAccumulatesTotalAndTracksLongest() {
        KingStats stats = new KingStats();
        UUID player = UUID.randomUUID();

        stats.recordReignEnded(player, 60_000L);
        stats.recordReignEnded(player, 90_000L);
        stats.recordReignEnded(player, 30_000L);

        PlayerKingStats playerStats = stats.statsFor(player);
        assertEquals(180_000L, playerStats.totalReignMillis());
        assertEquals(90_000L, playerStats.longestReignMillis());
    }

    @Test
    void mostSelectedFindsTheTopPlayer() {
        KingStats stats = new KingStats();
        UUID frequent = UUID.randomUUID();
        UUID rare = UUID.randomUUID();

        stats.recordSelected(frequent);
        stats.recordSelected(frequent);
        stats.recordSelected(rare);

        assertEquals(frequent, stats.mostSelected().orElseThrow().uuid());
    }

    @Test
    void mostSelectedIsEmptyWhenNoOneHasBeenSelected() {
        KingStats stats = new KingStats();
        assertTrue(stats.mostSelected().isEmpty());
    }

    @Test
    void longestReignFindsTheTopPlayer() {
        KingStats stats = new KingStats();
        UUID shortReign = UUID.randomUUID();
        UUID longReign = UUID.randomUUID();

        stats.recordReignEnded(shortReign, 10_000L);
        stats.recordReignEnded(longReign, 500_000L);

        assertEquals(longReign, stats.longestReign().orElseThrow().uuid());
    }

    @Test
    void restoreRebuildsPersistedValues() {
        KingStats stats = new KingStats();
        UUID player = UUID.randomUUID();

        stats.restore(player, 4, 120_000L, 80_000L);

        PlayerKingStats playerStats = stats.statsFor(player);
        assertEquals(4, playerStats.timesSelected());
        assertEquals(120_000L, playerStats.totalReignMillis());
        assertEquals(80_000L, playerStats.longestReignMillis());
    }

    @Test
    void recordRotationIncrementsTotalRotations() {
        KingStats stats = new KingStats();
        stats.recordRotation();
        stats.recordRotation();
        assertEquals(2, stats.totalRotations());
    }
}
