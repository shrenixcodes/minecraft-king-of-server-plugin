package com.kingoftheserver.plugin.king;

/**
 * Pure eligibility decision so the rules for who can become King are covered by unit
 * tests without needing a running server.
 */
public final class EligibilityRules {

    private EligibilityRules() {
    }

    public static boolean isEligible(boolean online, boolean hasUsePermission, boolean spectator, boolean vanished,
                                      boolean bypass, boolean excludeSpectators, boolean excludeVanished) {
        if (!online || !hasUsePermission) {
            return false;
        }
        if (bypass) {
            return false;
        }
        if (excludeSpectators && spectator) {
            return false;
        }
        return !(excludeVanished && vanished);
    }
}
