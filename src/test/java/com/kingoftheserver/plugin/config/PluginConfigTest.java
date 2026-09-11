package com.kingoftheserver.plugin.config;

import com.kingoftheserver.plugin.king.OfflineKingPolicy;
import com.kingoftheserver.plugin.king.SelectionMode;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PluginConfigTest {

    @Test
    void emptyConfigFallsBackToDocumentedDefaults() {
        PluginConfig config = PluginConfig.parse(new MemoryConfiguration());

        assertEquals(Duration.ofMinutes(7), config.kingDuration());
        assertEquals(SelectionMode.WEIGHTED, config.selectionMode());
        assertTrue(config.preventConsecutiveKing());
        assertEquals(1, config.minimumPlayers());
        assertTrue(config.autoStart());
        assertEquals(OfflineKingPolicy.KEEP, config.offlineKingPolicy());
        assertTrue(config.ui().bossBarEnabled());
        assertTrue(config.messages().announceNewKing());
        assertEquals(10, config.messages().countdownSeconds());
        assertTrue(config.selection().excludeSpectators());
        assertTrue(config.ability("royal-speed").enabled());
    }

    @Test
    void parsesCustomDuration() {
        MemoryConfiguration raw = new MemoryConfiguration();
        raw.set("king.duration", "3m30s");

        PluginConfig config = PluginConfig.parse(raw);

        assertEquals(Duration.ofMinutes(3).plusSeconds(30), config.kingDuration());
    }

    @Test
    void invalidDurationFallsBackToDefault() {
        MemoryConfiguration raw = new MemoryConfiguration();
        raw.set("king.duration", "not-a-duration");

        PluginConfig config = PluginConfig.parse(raw);

        assertEquals(PluginConfig.DEFAULT_DURATION, config.kingDuration());
    }

    @Test
    void parsesSelectionModeCaseInsensitively() {
        MemoryConfiguration raw = new MemoryConfiguration();
        raw.set("king.selection-mode", "Random");

        assertEquals(SelectionMode.RANDOM, PluginConfig.parse(raw).selectionMode());
    }

    @Test
    void invalidSelectionModeFallsBackToWeighted() {
        MemoryConfiguration raw = new MemoryConfiguration();
        raw.set("king.selection-mode", "chaotic");

        assertEquals(SelectionMode.WEIGHTED, PluginConfig.parse(raw).selectionMode());
    }

    @Test
    void minimumPlayersCannotGoBelowOne() {
        MemoryConfiguration raw = new MemoryConfiguration();
        raw.set("king.minimum-players", 0);

        assertEquals(1, PluginConfig.parse(raw).minimumPlayers());
    }

    @Test
    void disabledAbilityIsRespected() {
        MemoryConfiguration raw = new MemoryConfiguration();
        raw.set("abilities.royal-shield.enabled", false);

        PluginConfig config = PluginConfig.parse(raw);

        assertFalse(config.ability("royal-shield").enabled());
        assertTrue(config.ability("royal-speed").enabled());
    }

    @Test
    void abilityOptionsAreExposedToAbilities() {
        MemoryConfiguration raw = new MemoryConfiguration();
        raw.set("abilities.royal-speed.amplifier", 3);

        PluginConfig config = PluginConfig.parse(raw);

        assertEquals(3, config.ability("royal-speed").options().getInt("amplifier"));
    }

    @Test
    void countdownSecondsIsClampedToReasonableRange() {
        MemoryConfiguration raw = new MemoryConfiguration();
        raw.set("messages.countdown-seconds", 999);

        assertEquals(60, PluginConfig.parse(raw).messages().countdownSeconds());
    }

    @Test
    void offlineKingPolicyParsesReplace() {
        MemoryConfiguration raw = new MemoryConfiguration();
        raw.set("selection.offline-king-policy", "replace");

        assertEquals(OfflineKingPolicy.REPLACE, PluginConfig.parse(raw).offlineKingPolicy());
    }

    @Test
    void uiTogglesCanBeDisabledIndependently() {
        MemoryConfiguration raw = new MemoryConfiguration();
        raw.set("ui.boss-bar.enabled", false);
        raw.set("ui.scoreboard.enabled", true);

        PluginConfig config = PluginConfig.parse(raw);

        assertFalse(config.ui().bossBarEnabled());
        assertTrue(config.ui().scoreboardEnabled());
    }
}
