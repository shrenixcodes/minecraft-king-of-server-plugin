package com.kingoftheserver.plugin.king;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

/**
 * Selects the next King at random, weighting candidates who have been King fewer times
 * more heavily. This keeps selection fair over the long run without ever completely
 * ruling a frequent winner out, and without the complexity of a full history-based
 * queueing system.
 */
public final class WeightedKingSelector implements KingSelector {

    @Override
    public Optional<UUID> select(List<Candidate> eligible, UUID currentKing, UUID previousKing,
                                  boolean preventConsecutive, Random random) {
        List<Candidate> pool = SelectionPool.narrow(eligible, currentKing, previousKing, preventConsecutive);
        if (pool.isEmpty()) {
            return Optional.empty();
        }
        if (pool.size() == 1) {
            return Optional.of(pool.get(0).uuid());
        }

        double[] weights = new double[pool.size()];
        double totalWeight = 0;
        for (int i = 0; i < pool.size(); i++) {
            double weight = 1.0 / (1 + pool.get(i).timesSelected());
            weights[i] = weight;
            totalWeight += weight;
        }

        double roll = random.nextDouble() * totalWeight;
        double cumulative = 0;
        for (int i = 0; i < pool.size(); i++) {
            cumulative += weights[i];
            if (roll < cumulative) {
                return Optional.of(pool.get(i).uuid());
            }
        }
        return Optional.of(pool.get(pool.size() - 1).uuid());
    }
}
