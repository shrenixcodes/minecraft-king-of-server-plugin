package com.kingoftheserver.plugin.king;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KingRoundTrackerTest {

    private final Instant start = Instant.parse("2026-01-01T00:00:00Z");

    @Test
    void firstRotationHasNoPreviousKingOrReignLength() {
        KingRoundTracker tracker = new KingRoundTracker();
        UUID alex = UUID.randomUUID();

        RoundTransition transition = tracker.rotate(alex, start, Duration.ofMinutes(7));

        assertEquals(1, transition.roundNumber());
        assertNull(transition.previousKing());
        assertEquals(Duration.ZERO, transition.previousReignLength());
        assertEquals(alex, transition.newKing());
        assertFalse(transition.sameKingContinuation());
        assertEquals(alex, tracker.currentKing().orElseThrow());
    }

    @Test
    void secondRotationReportsPreviousKingAndReignLength() {
        KingRoundTracker tracker = new KingRoundTracker();
        UUID alex = UUID.randomUUID();
        UUID steve = UUID.randomUUID();

        tracker.rotate(alex, start, Duration.ofMinutes(7));
        Instant sevenMinutesLater = start.plus(Duration.ofMinutes(7));
        RoundTransition transition = tracker.rotate(steve, sevenMinutesLater, Duration.ofMinutes(7));

        assertEquals(2, transition.roundNumber());
        assertEquals(alex, transition.previousKing());
        assertEquals(Duration.ofMinutes(7), transition.previousReignLength());
        assertEquals(steve, transition.newKing());
        assertEquals(alex, tracker.previousKing().orElseThrow());
    }

    @Test
    void rotatingToSameKingIsFlaggedAsContinuation() {
        KingRoundTracker tracker = new KingRoundTracker();
        UUID solePlayer = UUID.randomUUID();

        tracker.rotate(solePlayer, start, Duration.ofMinutes(7));
        RoundTransition transition = tracker.rotate(solePlayer, start.plus(Duration.ofMinutes(7)), Duration.ofMinutes(7));

        assertTrue(transition.sameKingContinuation());
        assertEquals(2, tracker.roundNumber());
    }

    @Test
    void isExpiredOnlyAfterDurationElapses() {
        KingRoundTracker tracker = new KingRoundTracker();
        tracker.rotate(UUID.randomUUID(), start, Duration.ofMinutes(7));

        assertFalse(tracker.isExpired(start.plus(Duration.ofMinutes(6))));
        assertTrue(tracker.isExpired(start.plus(Duration.ofMinutes(7))));
        assertTrue(tracker.isExpired(start.plus(Duration.ofMinutes(8))));
    }

    @Test
    void remainingCountsDownAndClampsToZero() {
        KingRoundTracker tracker = new KingRoundTracker();
        tracker.rotate(UUID.randomUUID(), start, Duration.ofMinutes(7));

        assertEquals(Duration.ofMinutes(4), tracker.remaining(start.plus(Duration.ofMinutes(3))));
        assertEquals(Duration.ZERO, tracker.remaining(start.plus(Duration.ofMinutes(10))));
    }

    @Test
    void remainingIsZeroWithNoCurrentKing() {
        KingRoundTracker tracker = new KingRoundTracker();
        assertEquals(Duration.ZERO, tracker.remaining(start));
    }

    @Test
    void manualAssignmentEndsReignJustLikeARotation() {
        KingRoundTracker tracker = new KingRoundTracker();
        UUID alex = UUID.randomUUID();
        UUID steve = UUID.randomUUID();

        tracker.rotate(alex, start, Duration.ofMinutes(7));
        // An admin runs "/king set steve" after only two minutes.
        RoundTransition transition = tracker.rotate(steve, start.plus(Duration.ofMinutes(2)), Duration.ofMinutes(7));

        assertEquals(alex, transition.previousKing());
        assertEquals(Duration.ofMinutes(2), transition.previousReignLength());
        assertEquals(steve, tracker.currentKing().orElseThrow());
        assertEquals(Duration.ofMinutes(7), tracker.remaining(start.plus(Duration.ofMinutes(2))));
    }

    @Test
    void extendKeepsSameKingWithoutStartingNewRound() {
        KingRoundTracker tracker = new KingRoundTracker();
        UUID alex = UUID.randomUUID();
        tracker.rotate(alex, start, Duration.ofMinutes(7));
        int roundBefore = tracker.roundNumber();

        Instant expiredAt = start.plus(Duration.ofMinutes(7));
        tracker.extend(expiredAt);

        assertEquals(roundBefore, tracker.roundNumber());
        assertEquals(alex, tracker.currentKing().orElseThrow());
        assertFalse(tracker.isExpired(expiredAt.plus(Duration.ofMinutes(1))));
    }

    @Test
    void shiftReignStartPausesTheCountdown() {
        KingRoundTracker tracker = new KingRoundTracker();
        tracker.rotate(UUID.randomUUID(), start, Duration.ofMinutes(7));

        Instant checkpoint = start.plus(Duration.ofMinutes(2));
        Duration remainingBeforePause = tracker.remaining(checkpoint);

        // Ten minutes pass while rotation is stopped; shifting by that pause should mean
        // the remaining time at the same relative offset is unchanged.
        tracker.shiftReignStart(Duration.ofMinutes(10));
        Duration remainingAfterPause = tracker.remaining(checkpoint.plus(Duration.ofMinutes(10)));

        assertEquals(remainingBeforePause, remainingAfterPause);
    }

    @Test
    void restoreRebuildsStateWithoutTreatingItAsARotation() {
        KingRoundTracker tracker = new KingRoundTracker();
        UUID alex = UUID.randomUUID();
        UUID steve = UUID.randomUUID();

        tracker.restore(alex, steve, 5, start, Duration.ofMinutes(7));

        assertEquals(alex, tracker.currentKing().orElseThrow());
        assertEquals(steve, tracker.previousKing().orElseThrow());
        assertEquals(5, tracker.roundNumber());
        assertEquals(Duration.ofMinutes(7), tracker.roundDuration());
    }
}
