package com.kingoftheserver.plugin.king;

/**
 * What happens to the crown when the current King disconnects.
 *
 * <p>{@link #KEEP} is the default: the King keeps the crown while offline and the
 * seven-minute timer keeps running in the background, exactly as if they were still
 * playing. When the timer expires, only online eligible players are considered, so an
 * absent King is naturally replaced at the next rotation. This is the simplest policy
 * and avoids punishing a player for a short disconnect.</p>
 *
 * <p>{@link #REPLACE} immediately ends the reign and selects a new King as soon as the
 * current King disconnects, for servers that want the crown to always be held by an
 * active player.</p>
 */
public enum OfflineKingPolicy {
    KEEP,
    REPLACE;

    public static OfflineKingPolicy parse(String raw, OfflineKingPolicy fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        try {
            return OfflineKingPolicy.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }
}
