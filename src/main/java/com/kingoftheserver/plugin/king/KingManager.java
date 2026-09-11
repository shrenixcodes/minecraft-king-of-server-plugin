package com.kingoftheserver.plugin.king;

import com.kingoftheserver.plugin.ability.AbilityManager;
import com.kingoftheserver.plugin.config.ConfigManager;
import com.kingoftheserver.plugin.config.PluginConfig;
import com.kingoftheserver.plugin.persistence.RoundStateRepository;
import com.kingoftheserver.plugin.persistence.RoundStateSnapshot;
import com.kingoftheserver.plugin.persistence.StatsRepository;
import com.kingoftheserver.plugin.ui.UiManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

/**
 * Orchestrates the King rotation: owns the heartbeat task, delegates the actual
 * selection decision to a {@link KingSelector}, and drives {@link AbilityManager} and
 * {@link UiManager} whenever the crown changes hands. This is the only class that talks
 * to the Bukkit scheduler for rotation purposes, keeping the timing logic in one place.
 */
public final class KingManager {

    private static final long HEARTBEAT_PERIOD_TICKS = 20L;
    private static final long ONE_MINUTE_SECONDS = 60L;

    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final AbilityManager abilityManager;
    private final UiManager uiManager;
    private final StatsRepository statsRepository;
    private final RoundStateRepository roundStateRepository;
    private final KingStats stats = new KingStats();
    private final KingRoundTracker tracker = new KingRoundTracker();
    private final Random random = new Random();

    private KingSelector selector = new WeightedKingSelector();
    private BukkitTask heartbeatTask;
    private boolean running;
    private Instant pausedAt;
    private boolean oneMinuteWarned;
    private int lastCountdownSecond = -1;

    public KingManager(JavaPlugin plugin, ConfigManager configManager, AbilityManager abilityManager,
                        UiManager uiManager, StatsRepository statsRepository,
                        RoundStateRepository roundStateRepository) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.abilityManager = abilityManager;
        this.uiManager = uiManager;
        this.statsRepository = statsRepository;
        this.roundStateRepository = roundStateRepository;
    }

    public void initialize() {
        statsRepository.load(stats);
        selector = KingSelectorFactory.create(configManager.current().selectionMode());

        Optional<RoundStateSnapshot> snapshot = roundStateRepository.load();
        running = configManager.current().autoStart();
        if (snapshot.isPresent()) {
            RoundStateSnapshot state = snapshot.get();
            running = state.running();
            if (state.king() != null && state.reignStartedAt() != null) {
                tracker.restore(state.king(), state.previousKing(), state.roundNumber(),
                        state.reignStartedAt(), state.roundDuration());
            }
        }

        if (heartbeatTask == null) {
            heartbeatTask = Bukkit.getScheduler().runTaskTimer(plugin, this::heartbeat, HEARTBEAT_PERIOD_TICKS, HEARTBEAT_PERIOD_TICKS);
        }
    }

    public void reloadSelector() {
        selector = KingSelectorFactory.create(configManager.current().selectionMode());
    }

    public void shutdown() {
        if (heartbeatTask != null) {
            heartbeatTask.cancel();
            heartbeatTask = null;
        }
        persistRoundState();
        statsRepository.save(stats);
    }

    public boolean start() {
        if (running) {
            return false;
        }
        if (pausedAt != null) {
            tracker.shiftReignStart(Duration.between(pausedAt, Instant.now()));
            pausedAt = null;
        }
        running = true;
        if (tracker.currentKing().isEmpty()) {
            tryBeginInitialRound(Instant.now());
        }
        persistRoundState();
        return true;
    }

    public boolean stop() {
        if (!running) {
            return false;
        }
        running = false;
        pausedAt = Instant.now();
        persistRoundState();
        return true;
    }

    public boolean isRunning() {
        return running;
    }

    /** Ends the current reign immediately and selects the next King, as if the timer had expired. */
    public boolean forceNext() {
        performRotationAttempt(Instant.now());
        return true;
    }

    /** Directly assigns a player as King, bypassing normal selection and eligibility rules. */
    public void setKing(Player target) {
        applyRotation(target.getUniqueId(), Instant.now());
    }

    public Optional<UUID> currentKing() {
        return tracker.currentKing();
    }

    public Duration remaining() {
        return tracker.remaining(Instant.now());
    }

    public Duration roundDuration() {
        return tracker.roundDuration();
    }

    public int roundNumber() {
        return tracker.roundNumber();
    }

    public KingStats stats() {
        return stats;
    }

    public void handlePlayerQuit(Player player) {
        abilityManager.forgetPlayer(player.getUniqueId());
        if (tracker.currentKing().map(king -> king.equals(player.getUniqueId())).orElse(false)
                && configManager.current().offlineKingPolicy() == OfflineKingPolicy.REPLACE) {
            performRotationAttempt(Instant.now());
        }
    }

    public void handlePlayerJoin(Player player) {
        if (running && tracker.currentKing().isEmpty()) {
            tryBeginInitialRound(Instant.now());
        }
    }

    public String displayNameOf(UUID uuid) {
        Player online = Bukkit.getPlayer(uuid);
        if (online != null) {
            return online.getName();
        }
        OfflinePlayer offline = Bukkit.getOfflinePlayer(uuid);
        String name = offline.getName();
        return name != null ? name : uuid.toString();
    }

    private void heartbeat() {
        if (!running) {
            return;
        }
        Instant now = Instant.now();

        if (tracker.currentKing().isEmpty()) {
            tryBeginInitialRound(now);
            return;
        }

        if (tracker.isExpired(now)) {
            performRotationAttempt(now);
            return;
        }

        UUID kingUuid = tracker.currentKing().orElseThrow();
        Player kingPlayer = Bukkit.getPlayer(kingUuid);
        String kingName = displayNameOf(kingUuid);
        Duration remaining = tracker.remaining(now);

        uiManager.tick(kingName, kingPlayer, remaining, tracker.roundDuration(), tracker.roundNumber());

        long secondsLeft = remaining.getSeconds();
        if (!oneMinuteWarned && secondsLeft <= ONE_MINUTE_SECONDS) {
            oneMinuteWarned = true;
            uiManager.announceOneMinuteWarning(kingName);
        }

        int countdownSeconds = configManager.current().messages().countdownSeconds();
        if (countdownSeconds > 0 && secondsLeft >= 1 && secondsLeft <= countdownSeconds
                && secondsLeft != lastCountdownSecond) {
            lastCountdownSecond = (int) secondsLeft;
            uiManager.countdownTick((int) secondsLeft);
        }
    }

    private void tryBeginInitialRound(Instant now) {
        PluginConfig config = configManager.current();
        List<Candidate> eligible = collectEligible(config);
        if (eligible.size() < config.minimumPlayers()) {
            return;
        }
        selector.select(eligible, null, tracker.previousKing().orElse(null), config.preventConsecutiveKing(), random)
                .ifPresent(uuid -> applyRotation(uuid, now));
    }

    private void performRotationAttempt(Instant now) {
        PluginConfig config = configManager.current();
        List<Candidate> eligible = collectEligible(config);
        if (eligible.size() < config.minimumPlayers()) {
            tracker.extend(now);
            return;
        }
        Optional<UUID> next = selector.select(eligible, tracker.currentKing().orElse(null),
                tracker.previousKing().orElse(null), config.preventConsecutiveKing(), random);
        if (next.isEmpty()) {
            tracker.extend(now);
            return;
        }
        applyRotation(next.get(), now);
    }

    private void applyRotation(UUID nextKingUuid, Instant now) {
        PluginConfig config = configManager.current();
        RoundTransition transition = tracker.rotate(nextKingUuid, now, config.kingDuration());

        if (transition.sameKingContinuation()) {
            uiManager.announceContinuedReign(displayNameOf(nextKingUuid), config.kingDuration());
        } else {
            if (transition.previousKing() != null) {
                Player previous = Bukkit.getPlayer(transition.previousKing());
                if (previous != null) {
                    abilityManager.depose(previous);
                    uiManager.onDeposed(previous);
                }
                stats.recordReignEnded(transition.previousKing(), transition.previousReignLength().toMillis());
            }

            Player king = Bukkit.getPlayer(nextKingUuid);
            if (king != null) {
                abilityManager.crown(king);
                uiManager.onCrowned(king);
            }
            stats.recordSelected(nextKingUuid);
            stats.recordRotation();
            uiManager.announceNewKing(displayNameOf(nextKingUuid), transition.previousReignLength());
        }

        oneMinuteWarned = false;
        lastCountdownSecond = -1;
        persistRoundState();
        statsRepository.save(stats);
    }

    private List<Candidate> collectEligible(PluginConfig config) {
        List<Candidate> candidates = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            boolean spectator = player.getGameMode() == GameMode.SPECTATOR;
            boolean vanished = isVanished(player);
            boolean bypass = player.hasPermission("king.bypass");
            boolean hasUsePermission = player.hasPermission("king.use");

            boolean eligible = EligibilityRules.isEligible(true, hasUsePermission, spectator, vanished, bypass,
                    config.selection().excludeSpectators(), config.selection().excludeVanished());
            if (eligible) {
                int timesSelected = stats.get(player.getUniqueId()).map(PlayerKingStats::timesSelected).orElse(0);
                candidates.add(new Candidate(player.getUniqueId(), player.getName(), timesSelected));
            }
        }
        return candidates;
    }

    /**
     * The "vanished" metadata key is a long-standing cross-plugin convention (Essentials,
     * SuperVanish/PremiumVanish, CMI) for signaling that a player is invisible to others.
     * It predates, and is independent of, any single plugin's persistent data, so the
     * deprecated {@link org.bukkit.metadata.Metadatable} API is the only way to read it.
     */
    @SuppressWarnings("deprecation")
    private boolean isVanished(Player player) {
        for (MetadataValue value : player.getMetadata("vanished")) {
            if (value.asBoolean()) {
                return true;
            }
        }
        return false;
    }

    private void persistRoundState() {
        roundStateRepository.save(tracker, running);
    }
}
