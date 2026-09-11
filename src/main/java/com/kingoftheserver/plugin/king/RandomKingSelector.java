package com.kingoftheserver.plugin.king;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

/**
 * Selects the next King uniformly at random from the eligible pool. Simple and fully
 * predictable, useful for servers that want every eligible player to have an equal
 * chance regardless of history.
 */
public final class RandomKingSelector implements KingSelector {

    @Override
    public Optional<UUID> select(List<Candidate> eligible, UUID currentKing, UUID previousKing,
                                  boolean preventConsecutive, Random random) {
        List<Candidate> pool = SelectionPool.narrow(eligible, currentKing, previousKing, preventConsecutive);
        if (pool.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(pool.get(random.nextInt(pool.size())).uuid());
    }
}
