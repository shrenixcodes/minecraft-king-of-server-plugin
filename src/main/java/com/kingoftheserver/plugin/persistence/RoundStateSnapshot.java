package com.kingoftheserver.plugin.persistence;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * The persisted round state as it existed the moment the server last shut down.
 */
public record RoundStateSnapshot(UUID king, UUID previousKing, int roundNumber, Instant reignStartedAt,
                                  Duration roundDuration, boolean running) {
}
