package com.kingoftheserver.plugin.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

/**
 * Per-ability configuration: whether it is enabled, plus a free-form options section so
 * individual abilities can define their own tunables (amplifier, cooldown, duration...)
 * without {@link PluginConfig} needing to know about them.
 */
public record AbilitySettings(boolean enabled, ConfigurationSection options) {

    public static AbilitySettings disabled() {
        return new AbilitySettings(false, new MemoryConfiguration());
    }
}
