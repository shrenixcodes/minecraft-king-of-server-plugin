package com.kingoftheserver.plugin.king;

/**
 * Resolves a {@link SelectionMode} into its {@link KingSelector} implementation.
 */
public final class KingSelectorFactory {

    private KingSelectorFactory() {
    }

    public static KingSelector create(SelectionMode mode) {
        return switch (mode) {
            case RANDOM -> new RandomKingSelector();
            case WEIGHTED -> new WeightedKingSelector();
        };
    }
}
