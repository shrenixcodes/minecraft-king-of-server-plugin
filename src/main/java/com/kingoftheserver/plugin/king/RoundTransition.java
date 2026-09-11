package com.kingoftheserver.plugin.king;

import java.time.Duration;
import java.util.UUID;

/**
 * Describes what happened when {@link KingRoundTracker#rotate} was called: who was
 * deposed (if anyone), how long they reigned, who the new King is, and whether this was
 * a genuine change or the same player simply continuing because no one else was eligible.
 */
public record RoundTransition(int roundNumber, UUID previousKing, Duration previousReignLength,
                               UUID newKing, boolean sameKingContinuation) {
}
