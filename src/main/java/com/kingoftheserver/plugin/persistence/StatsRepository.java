package com.kingoftheserver.plugin.persistence;

import com.kingoftheserver.plugin.king.KingStats;
import com.kingoftheserver.plugin.king.PlayerKingStats;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Persists {@link KingStats} to a simple YAML file. A lightweight data file is more
 * than sufficient for the small amount of data this plugin tracks, so no database is
 * used.
 */
public final class StatsRepository {

    private final JavaPlugin plugin;
    private final File file;

    public StatsRepository(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "stats.yml");
    }

    public void load(KingStats stats) {
        if (!file.exists()) {
            return;
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        stats.restoreTotalRotations(yaml.getInt("total-rotations", 0));

        ConfigurationSection playersSection = yaml.getConfigurationSection("players");
        if (playersSection == null) {
            return;
        }
        for (String key : playersSection.getKeys(false)) {
            UUID uuid;
            try {
                uuid = UUID.fromString(key);
            } catch (IllegalArgumentException e) {
                continue;
            }
            ConfigurationSection playerSection = playersSection.getConfigurationSection(key);
            if (playerSection == null) {
                continue;
            }
            stats.restore(uuid,
                    playerSection.getInt("times-selected", 0),
                    playerSection.getLong("total-reign-millis", 0L),
                    playerSection.getLong("longest-reign-millis", 0L));
        }
    }

    public void save(KingStats stats) {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("total-rotations", stats.totalRotations());

        for (PlayerKingStats playerStats : stats.allPlayerStats()) {
            String base = "players." + playerStats.uuid();
            yaml.set(base + ".times-selected", playerStats.timesSelected());
            yaml.set(base + ".total-reign-millis", playerStats.totalReignMillis());
            yaml.set(base + ".longest-reign-millis", playerStats.longestReignMillis());
        }

        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            yaml.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to save King statistics", e);
        }
    }
}
