package com.kingoftheserver.plugin.king;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomKingSelectorTest {

    private final RandomKingSelector selector = new RandomKingSelector();

    @Test
    void returnsEmptyWhenNoCandidates() {
        Optional<UUID> result = selector.select(List.of(), null, null, true, new Random());
        assertTrue(result.isEmpty());
    }

    @Test
    void avoidsCurrentKingWhenAlternativeExists() {
        Candidate current = new Candidate(UUID.randomUUID(), "Alex", 0);
        Candidate other = new Candidate(UUID.randomUUID(), "Steve", 0);

        for (int seed = 0; seed < 20; seed++) {
            Optional<UUID> result = selector.select(List.of(current, other), current.uuid(), null, true, new Random(seed));
            assertEquals(other.uuid(), result.orElseThrow());
        }
    }

    @Test
    void avoidsPreviousKingWhenAlternativeExists() {
        Candidate current = new Candidate(UUID.randomUUID(), "Alex", 0);
        Candidate previous = new Candidate(UUID.randomUUID(), "Steve", 0);
        Candidate third = new Candidate(UUID.randomUUID(), "Jamie", 0);

        for (int seed = 0; seed < 20; seed++) {
            Optional<UUID> result = selector.select(List.of(current, previous, third), null, previous.uuid(), true, new Random(seed));
            assertEquals(third.uuid(), result.orElseThrow());
        }
    }

    @Test
    void reselectsSolePlayerWhenNoAlternativeExists() {
        Candidate onlyPlayer = new Candidate(UUID.randomUUID(), "Alex", 5);
        Optional<UUID> result = selector.select(List.of(onlyPlayer), onlyPlayer.uuid(), onlyPlayer.uuid(), true, new Random());
        assertEquals(onlyPlayer.uuid(), result.orElseThrow());
    }

    @Test
    void ignoresConsecutivePreventionWhenDisabled() {
        Candidate current = new Candidate(UUID.randomUUID(), "Alex", 0);
        Candidate previous = new Candidate(UUID.randomUUID(), "Steve", 0);

        // With prevention disabled and current excluded, previous king must remain a valid pick.
        boolean previousWasPicked = false;
        for (int seed = 0; seed < 50; seed++) {
            Optional<UUID> result = selector.select(List.of(current, previous), current.uuid(), previous.uuid(), false, new Random(seed));
            if (result.orElseThrow().equals(previous.uuid())) {
                previousWasPicked = true;
                break;
            }
        }
        assertTrue(previousWasPicked);
    }
}
