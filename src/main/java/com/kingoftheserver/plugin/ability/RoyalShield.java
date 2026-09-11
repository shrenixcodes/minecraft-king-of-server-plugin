package com.kingoftheserver.plugin.ability;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An active, cooldown-gated defensive burst the King triggers by sneaking: a brief
 * window of damage resistance and absorption, useful for surviving a sudden ambush
 * without making the King invincible.
 */
public final class RoyalShield implements KingAbility {

    public static final String ID = "royal-shield";

    private final Map<UUID, Instant> lastTriggered = new ConcurrentHashMap<>();

    @Override
    public String id() {
        return ID;
    }

    @Override
    public Component displayName() {
        return Component.text("Royal Shield", NamedTextColor.GOLD);
    }

    @Override
    public Component description() {
        return Component.text("Sneak to trigger a brief burst of protection.", NamedTextColor.GRAY);
    }

    @Override
    public boolean isTriggered() {
        return true;
    }

    @Override
    public void onCrowned(Player king, ConfigurationSection options) {
        lastTriggered.remove(king.getUniqueId());
    }

    @Override
    public void onDeposed(Player king, ConfigurationSection options) {
        king.removePotionEffect(PotionEffectType.RESISTANCE);
        king.removePotionEffect(PotionEffectType.ABSORPTION);
    }

    @Override
    public void onTrigger(Player king, ConfigurationSection options) {
        int cooldownSeconds = Math.max(1, options.getInt("cooldown-seconds", 45));
        int durationSeconds = Math.max(1, options.getInt("duration-seconds", 5));

        Instant now = Instant.now();
        Instant last = lastTriggered.get(king.getUniqueId());
        if (last != null) {
            Duration remaining = Duration.ofSeconds(cooldownSeconds).minus(Duration.between(last, now));
            if (!remaining.isNegative() && !remaining.isZero()) {
                king.sendActionBar(Component.text(
                        "Royal Shield recharging: " + (remaining.getSeconds() + 1) + "s", NamedTextColor.RED));
                return;
            }
        }

        lastTriggered.put(king.getUniqueId(), now);
        int durationTicks = durationSeconds * 20;
        king.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, durationTicks, 1, true, true, true));
        king.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, durationTicks, 1, true, true, true));
        king.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, king.getLocation().add(0, 1, 0), 20, 0.4, 0.5, 0.4, 0.01);
        king.playSound(king.getLocation(), Sound.ITEM_TOTEM_USE, 0.6f, 1.4f);
        king.sendActionBar(Component.text("Royal Shield activated!", NamedTextColor.GOLD));
    }

    @Override
    public void forget(UUID uuid) {
        lastTriggered.remove(uuid);
    }
}
