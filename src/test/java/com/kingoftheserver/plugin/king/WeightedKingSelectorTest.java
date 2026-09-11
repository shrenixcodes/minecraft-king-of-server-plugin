package com.kingoftheserver.plugin.king;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WeightedKingSelectorTest {

    private final WeightedKingSelector selector = new WeightedKingSelector();

    @Test
    void returnsEmptyWhenNoCandidates() {
        assertTrue(selector.select(List.of(), null, null, true, new Random()).isEmpty());
    }

    @Test
    void avoidsCurrentKingWhenAlternativeExists() {
        Candidate current = new Candidate(UUID.randomUUID(), "Alex", 3);
        Candidate other = new Candidate(UUID.randomUUID(), "Steve", 3);

        for (int seed = 0; seed < 20; seed++) {
            Optional<UUID> result = selector.select(List.of(current, other), current.uuid(), null, true, new Random(seed));
            assertEquals(other.uuid(), result.orElseThrow());
        }
    }

    @Test
    void avoidsPreviousKingWhenAlternativeExists() {
        Candidate current = new Candidate(UUID.randomUUID(), "Alex", 1);
        Candidate previous = new Candidate(UUID.randomUUID(), "Steve", 1);
        Candidate third = new Candidate(UUID.randomUUID(), "Jamie", 1);

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
    void favorsPlayersWithFewerPastSelections() {
        Candidate frequentlyKing = new Candidate(UUID.randomUUID(), "Frequent", 50);
        Candidate rarelyKing = new Candidate(UUID.randomUUID(), "Rare", 0);

        AtomicInteger frequentWins = new AtomicInteger();
        AtomicInteger rareWins = new AtomicInteger();
        Random random = new Random(42);

        for (int i = 0; i < 2000; i++) {
            UUID picked = selector.select(List.of(frequentlyKing, rarelyKing), null, null, true, random).orElseThrow();
            if (picked.equals(frequentlyKing.uuid())) {
                frequentWins.incrementAndGet();
            } else {
                rareWins.incrementAndGet();
            }
        }

        assertTrue(rareWins.get() > frequentWins.get(),
                "Expected the rarely-selected player to win more often; frequent=" + frequentWins + " rare=" + rareWins);
    }

    @Test
    void everyCandidateCanStillBeChosen() {
        Candidate frequentlyKing = new Candidate(UUID.randomUUID(), "Frequent", 50);
        Candidate rarelyKing = new Candidate(UUID.randomUUID(), "Rare", 0);
        Random random = new Random(7);

        boolean frequentEverWon = false;
        for (int i = 0; i < 500 && !frequentEverWon; i++) {
            UUID picked = selector.select(List.of(frequentlyKing, rarelyKing), null, null, true, random).orElseThrow();
            frequentEverWon = picked.equals(frequentlyKing.uuid());
        }
        assertTrue(frequentEverWon, "A frequently selected player should still occasionally be chosen");
    }
}
