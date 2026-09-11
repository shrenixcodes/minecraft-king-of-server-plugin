package com.kingoftheserver.plugin.config;

import com.kingoftheserver.plugin.king.OfflineKingPolicy;
import com.kingoftheserver.plugin.king.SelectionMode;
import com.kingoftheserver.plugin.util.DurationUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Immutable snapshot of the plugin's configuration. A fresh instance is built every time
 * {@code config.yml} is (re)loaded, so a {@code /king reload} can never leave the plugin
 * in a half-updated state.
 */
public final class PluginConfig {

    public static final Duration DEFAULT_DURATION = Duration.ofMinutes(7);
    public static final Set<String> ABILITY_IDS = Set.of(
            "royal-speed", "royal-strength", "royal-jump", "royal-vision", "royal-shield");

    private final Duration kingDuration;
    private final SelectionMode selectionMode;
    private final boolean preventConsecutiveKing;
    private final int minimumPlayers;
    private final boolean autoStart;
    private final OfflineKingPolicy offlineKingPolicy;
    private final Map<String, AbilitySettings> abilities;
    private final UiSettings ui;
    private final MessageSettings messages;
    private final SelectionSettings selection;

    private PluginConfig(Duration kingDuration, SelectionMode selectionMode, boolean preventConsecutiveKing,
                          int minimumPlayers, boolean autoStart, OfflineKingPolicy offlineKingPolicy,
                          Map<String, AbilitySettings> abilities, UiSettings ui, MessageSettings messages,
                          SelectionSettings selection) {
        this.kingDuration = kingDuration;
        this.selectionMode = selectionMode;
        this.preventConsecutiveKing = preventConsecutiveKing;
        this.minimumPlayers = minimumPlayers;
        this.autoStart = autoStart;
        this.offlineKingPolicy = offlineKingPolicy;
        this.abilities = abilities;
        this.ui = ui;
        this.messages = messages;
        this.selection = selection;
    }

    public static PluginConfig parse(ConfigurationSection root) {
        ConfigurationSection kingSection = sectionOrEmpty(root, "king");
        ConfigurationSection abilitiesSection = sectionOrEmpty(root, "abilities");
        ConfigurationSection uiSection = sectionOrEmpty(root, "ui");
        ConfigurationSection messagesSection = sectionOrEmpty(root, "messages");
        ConfigurationSection selectionSection = sectionOrEmpty(root, "selection");

        Duration duration;
        String rawDuration = kingSection.getString("duration", "7m");
        try {
            duration = DurationUtil.parse(rawDuration);
        } catch (IllegalArgumentException e) {
            duration = DEFAULT_DURATION;
        }

        SelectionMode selectionMode = SelectionMode.parse(kingSection.getString("selection-mode"), SelectionMode.WEIGHTED);
        boolean preventConsecutive = kingSection.getBoolean("prevent-consecutive-king", true);
        int minimumPlayers = Math.max(1, kingSection.getInt("minimum-players", 1));
        boolean autoStart = kingSection.getBoolean("auto-start", true);
        OfflineKingPolicy offlineKingPolicy = OfflineKingPolicy.parse(
                selectionSection.getString("offline-king-policy"), OfflineKingPolicy.KEEP);

        Map<String, AbilitySettings> abilities = new LinkedHashMap<>();
        for (String abilityId : ABILITY_IDS) {
            ConfigurationSection abilitySection = abilitiesSection.getConfigurationSection(abilityId);
            if (abilitySection == null) {
                abilitySection = new MemoryConfiguration();
            }
            boolean enabled = abilitySection.getBoolean("enabled", true);
            abilities.put(abilityId, new AbilitySettings(enabled, abilitySection));
        }

        UiSettings ui = new UiSettings(
                sectionOrEmpty(uiSection, "boss-bar").getBoolean("enabled", true),
                sectionOrEmpty(uiSection, "boss-bar").getString("color", "YELLOW"),
                sectionOrEmpty(uiSection, "boss-bar").getString("style", "SEGMENTED_10"),
                sectionOrEmpty(uiSection, "action-bar").getBoolean("enabled", true),
                sectionOrEmpty(uiSection, "scoreboard").getBoolean("enabled", true),
                sectionOrEmpty(uiSection, "particles").getBoolean("enabled", true),
                sectionOrEmpty(uiSection, "sounds").getBoolean("enabled", true)
        );

        MessageSettings messages = new MessageSettings(
                messagesSection.getBoolean("announce-new-king", true),
                messagesSection.getBoolean("announce-one-minute-warning", true),
                clampCountdown(messagesSection.getInt("countdown-seconds", 10))
        );

        SelectionSettings selectionSettings = new SelectionSettings(
                selectionSection.getBoolean("exclude-spectators", true),
                selectionSection.getBoolean("exclude-vanished", true)
        );

        return new PluginConfig(duration, selectionMode, preventConsecutive, minimumPlayers, autoStart,
                offlineKingPolicy, abilities, ui, messages, selectionSettings);
    }

    private static int clampCountdown(int seconds) {
        return Math.max(0, Math.min(seconds, 60));
    }

    private static ConfigurationSection sectionOrEmpty(ConfigurationSection parent, String path) {
        ConfigurationSection section = parent.getConfigurationSection(path);
        return section != null ? section : new MemoryConfiguration();
    }

    public Duration kingDuration() {
        return kingDuration;
    }

    public SelectionMode selectionMode() {
        return selectionMode;
    }

    public boolean preventConsecutiveKing() {
        return preventConsecutiveKing;
    }

    public int minimumPlayers() {
        return minimumPlayers;
    }

    public boolean autoStart() {
        return autoStart;
    }

    public OfflineKingPolicy offlineKingPolicy() {
        return offlineKingPolicy;
    }

    public AbilitySettings ability(String id) {
        return abilities.getOrDefault(id, AbilitySettings.disabled());
    }

    public UiSettings ui() {
        return ui;
    }

    public MessageSettings messages() {
        return messages;
    }

    public SelectionSettings selection() {
        return selection;
    }
}
