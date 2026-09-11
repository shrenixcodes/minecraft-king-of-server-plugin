package com.kingoftheserver.plugin.king;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Pure bookkeeping for the current King and round timing. Contains no server API calls
 * so the seven-minute rotation logic can be exercised directly in unit tests; all
 * in-game side effects (announcements, abilities, UI) are triggered by
 * {@link KingManager} in response to the {@link RoundTransition} this class returns.
 */
public final class KingRoundTracker {

    private int roundNumber = 0;
    private UUID currentKing;
    private UUID previousKing;
    private Instant reignStartedAt;
    private Duration roundDuration = Duration.ZERO;

    public RoundTransition rotate(UUID nextKing, Instant now, Duration duration) {
        if (nextKing == null) {
            throw new IllegalArgumentException("nextKing cannot be null");
        }

        boolean sameKingContinuation = nextKing.equals(currentKing);
        UUID depoedKing = currentKing;
        Duration reignLength = (depoedKing != null && reignStartedAt != null)
                ? Duration.between(reignStartedAt, now)
                : Duration.ZERO;

        if (!sameKingContinuation) {
            previousKing = currentKing;
        }
        currentKing = nextKing;
        reignStartedAt = now;
        roundDuration = duration;
        roundNumber++;

        return new RoundTransition(roundNumber, depoedKing, reignLength, nextKing, sameKingContinuation);
    }

    /**
     * Pushes the reign start forward to {@code now} without starting a new round.
     * Used when no eligible player is available at expiry, so the plugin quietly waits
     * instead of repeatedly attempting (and failing) a rotation every second.
     */
    /**
     * Shifts the reign start forward by {@code pausedFor}, so time spent with rotation
     * stopped (via {@code /king stop}) is not counted against the current reign.
     */
    public void shiftReignStart(Duration pausedFor) {
        if (reignStartedAt != null) {
            reignStartedAt = reignStartedAt.plus(pausedFor);
        }
    }

    public void extend(Instant now) {
        if (currentKing != null) {
            reignStartedAt = now;
        }
    }

    public void clearKing() {
        currentKing = null;
        reignStartedAt = null;
    }

    public boolean isExpired(Instant now) {
        return currentKing != null && reignStartedAt != null
                && !now.isBefore(reignStartedAt.plus(roundDuration));
    }

    public Duration remaining(Instant now) {
        if (currentKing == null || reignStartedAt == null) {
            return Duration.ZERO;
        }
        Duration remaining = Duration.between(now, reignStartedAt.plus(roundDuration));
        return remaining.isNegative() ? Duration.ZERO : remaining;
    }

    public Optional<UUID> currentKing() {
        return Optional.ofNullable(currentKing);
    }

    public Optional<UUID> previousKing() {
        return Optional.ofNullable(previousKing);
    }

    public int roundNumber() {
        return roundNumber;
    }

    public Duration roundDuration() {
        return roundDuration;
    }

    public Optional<Instant> reignStartedAt() {
        return Optional.ofNullable(reignStartedAt);
    }

    /**
     * Restores state after a server restart, without treating it as a new rotation.
     */
    public void restore(UUID king, UUID previousKing, int roundNumber, Instant reignStartedAt, Duration roundDuration) {
        this.currentKing = king;
        this.previousKing = previousKing;
        this.roundNumber = roundNumber;
        this.reignStartedAt = reignStartedAt;
        this.roundDuration = roundDuration;
    }
}
