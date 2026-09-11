package com.kingoftheserver.plugin.king;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Shared candidate-pool filtering used by every {@link KingSelector} implementation, so
 * "don't repeat the current or previous King" logic only lives in one place.
 */
final class SelectionPool {

    private SelectionPool() {
    }

    static List<Candidate> narrow(List<Candidate> eligible, UUID currentKing, UUID previousKing,
                                   boolean preventConsecutive) {
        if (eligible.isEmpty()) {
            return List.of();
        }

        List<Candidate> withoutCurrent = filterOut(eligible, currentKing);
        List<Candidate> pool = withoutCurrent.isEmpty() ? eligible : withoutCurrent;

        if (preventConsecutive && previousKing != null) {
            List<Candidate> withoutPrevious = filterOut(pool, previousKing);
            if (!withoutPrevious.isEmpty()) {
                pool = withoutPrevious;
            }
        }

        return pool;
    }

    private static List<Candidate> filterOut(List<Candidate> candidates, UUID excluded) {
        if (excluded == null) {
            return candidates;
        }
        List<Candidate> result = new ArrayList<>(candidates.size());
        for (Candidate candidate : candidates) {
            if (!candidate.uuid().equals(excluded)) {
                result.add(candidate);
            }
        }
        return result;
    }
}
