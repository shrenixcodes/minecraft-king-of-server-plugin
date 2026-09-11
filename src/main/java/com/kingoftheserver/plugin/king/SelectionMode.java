package com.kingoftheserver.plugin.king;

/**
 * Available King selection strategies. New strategies can be added here and in
 * {@link KingSelectorFactory} without touching {@link KingManager}.
 */
public enum SelectionMode {
    RANDOM,
    WEIGHTED;

    public static SelectionMode parse(String raw, SelectionMode fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        try {
            return SelectionMode.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }
}
