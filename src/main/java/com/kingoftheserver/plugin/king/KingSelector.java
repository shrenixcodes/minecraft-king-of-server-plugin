package com.kingoftheserver.plugin.king;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

/**
 * Chooses the next King from a pool of eligible candidates. Implementations must be
 * deterministic given the same {@link Random} seed so behavior remains testable.
 */
public interface KingSelector {

    /**
     * @param eligible online, ineligible-filtered candidates to choose from
     * @param currentKing the player currently on the throne, or {@code null} if none
     * @param previousKing the player who most recently held the throne, or {@code null}
     * @param preventConsecutive whether the previous King should be avoided when possible
     * @param random source of randomness, injected for testability
     * @return the selected candidate's UUID, or empty if no eligible candidate exists
     */
    Optional<UUID> select(List<Candidate> eligible, UUID currentKing, UUID previousKing,
                           boolean preventConsecutive, Random random);
}
