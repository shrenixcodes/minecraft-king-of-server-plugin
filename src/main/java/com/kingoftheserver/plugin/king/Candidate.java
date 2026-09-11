package com.kingoftheserver.plugin.king;

import java.util.UUID;

/**
 * A player eligible to be selected as King, along with just enough history for a
 * selection strategy to make a fair choice.
 */
public record Candidate(UUID uuid, String name, int timesSelected) {

    public Candidate {
        if (uuid == null) {
            throw new IllegalArgumentException("uuid cannot be null");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be blank");
        }
        if (timesSelected < 0) {
            throw new IllegalArgumentException("timesSelected cannot be negative");
        }
    }
}
