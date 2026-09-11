package com.kingoftheserver.plugin.persistence;

import com.kingoftheserver.plugin.king.KingRoundTracker;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Persists the in-progress round so a server restart does not silently reset or corrupt
 * the seven-minute timer.
 *
 * <p>On restart: if the saved reign had not yet expired, the countdown resumes from
 * where it left off. If it had already expired while the server was offline, a
 * rotation is triggered as soon as an eligible player is online. If rotation had been
 * stopped with {@code /king stop} before shutdown, it stays stopped.</p>
 */
public final class RoundStateRepository {

    private final JavaPlugin plugin;
    private final File file;

    public RoundStateRepository(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "state.yml");
    }

    public Optional<RoundStateSnapshot> load() {
        if (!file.exists()) {
            return Optional.empty();
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        String kingRaw = yaml.getString("king");
        if (kingRaw == null) {
            return Optional.of(new RoundStateSnapshot(null, null, yaml.getInt("round-number", 0),
                    null, Duration.ZERO, yaml.getBoolean("running", true)));
        }

        UUID king = UUID.fromString(kingRaw);
        UUID previousKing = yaml.contains("previous-king") ? UUID.fromString(yaml.getString("previous-king")) : null;
        int roundNumber = yaml.getInt("round-number", 0);
        Instant reignStartedAt = Instant.ofEpochMilli(yaml.getLong("reign-started-at"));
        Duration roundDuration = Duration.ofMillis(yaml.getLong("round-duration-millis"));
        boolean running = yaml.getBoolean("running", true);

        return Optional.of(new RoundStateSnapshot(king, previousKing, roundNumber, reignStartedAt, roundDuration, running));
    }

    public void save(KingRoundTracker tracker, boolean running) {
        YamlConfiguration yaml = new YamlConfiguration();
        tracker.currentKing().ifPresent(king -> yaml.set("king", king.toString()));
        tracker.previousKing().ifPresent(prev -> yaml.set("previous-king", prev.toString()));
        yaml.set("round-number", tracker.roundNumber());
        tracker.reignStartedAt().ifPresent(instant -> yaml.set("reign-started-at", instant.toEpochMilli()));
        yaml.set("round-duration-millis", tracker.roundDuration().toMillis());
        yaml.set("running", running);

        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            yaml.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to save King round state", e);
        }
    }
}
