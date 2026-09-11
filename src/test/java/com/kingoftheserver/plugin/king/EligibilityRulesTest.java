package com.kingoftheserver.plugin.king;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EligibilityRulesTest {

    @Test
    void offlinePlayersAreNeverEligible() {
        assertFalse(EligibilityRules.isEligible(false, true, false, false, false, true, true));
    }

    @Test
    void playersWithoutUsePermissionAreIneligible() {
        assertFalse(EligibilityRules.isEligible(true, false, false, false, false, true, true));
    }

    @Test
    void bypassPlayersAreAlwaysIneligible() {
        assertFalse(EligibilityRules.isEligible(true, true, false, false, true, true, true));
    }

    @Test
    void spectatorsExcludedWhenConfigured() {
        assertFalse(EligibilityRules.isEligible(true, true, true, false, false, true, true));
    }

    @Test
    void spectatorsAllowedWhenNotExcluded() {
        assertTrue(EligibilityRules.isEligible(true, true, true, false, false, false, true));
    }

    @Test
    void vanishedExcludedWhenConfigured() {
        assertFalse(EligibilityRules.isEligible(true, true, false, true, false, true, true));
    }

    @Test
    void vanishedAllowedWhenNotExcluded() {
        assertTrue(EligibilityRules.isEligible(true, true, false, true, false, true, false));
    }

    @Test
    void ordinaryOnlinePlayerIsEligible() {
        assertTrue(EligibilityRules.isEligible(true, true, false, false, false, true, true));
    }
}
