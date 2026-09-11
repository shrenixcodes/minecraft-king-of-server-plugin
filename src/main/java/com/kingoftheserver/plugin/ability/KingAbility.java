package com.kingoftheserver.plugin.ability;

import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * A single perk granted to the current King. Implementations should be self-contained:
 * adding a new ability means adding a new class here and registering it in
 * {@link AbilityManager}, without touching any other ability.
 */
public interface KingAbility {

    /** Stable identifier matching the ability's key under {@code abilities:} in config.yml. */
    String id();

    /** Human-readable name shown in {@code /king abilities}. */
    Component displayName();

    /** Short description shown in {@code /king abilities}. */
    Component description();

    /**
     * Whether this ability reacts to the King sneaking (an active, cooldown-gated
     * trigger) rather than being applied passively for the whole reign.
     */
    default boolean isTriggered() {
        return false;
    }

    /** Called once when a player becomes King, after any previous King has been deposed. */
    void onCrowned(Player king, ConfigurationSection options);

    /** Called once when a player stops being King, for any reason. Must clean up fully. */
    void onDeposed(Player king, ConfigurationSection options);

    /**
     * Called when the King triggers a {@link #isTriggered()} ability. Implementations
     * are responsible for their own cooldown bookkeeping.
     */
    default void onTrigger(Player king, ConfigurationSection options) {
    }

    /** Releases any per-player bookkeeping (such as cooldown timers) for the given player. */
    default void forget(java.util.UUID uuid) {
    }
}
