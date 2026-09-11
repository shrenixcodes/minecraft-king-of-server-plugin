package com.kingoftheserver.plugin.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Loads {@code config.yml} into an immutable {@link PluginConfig} snapshot and swaps it
 * atomically on reload, so in-flight code always sees a fully consistent configuration.
 */
public final class ConfigManager {

    private final JavaPlugin plugin;
    private volatile PluginConfig current;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public PluginConfig load() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration configuration = plugin.getConfig();
        PluginConfig parsed = PluginConfig.parse(configuration);
        this.current = parsed;
        return parsed;
    }

    public PluginConfig current() {
        PluginConfig snapshot = current;
        if (snapshot == null) {
            throw new IllegalStateException("Configuration has not been loaded yet");
        }
        return snapshot;
    }
}
