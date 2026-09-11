package com.kingoftheserver.plugin.ability;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Grants the King increased melee damage for the duration of their reign.
 */
public final class RoyalStrength implements KingAbility {

    public static final String ID = "royal-strength";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public Component displayName() {
        return Component.text("Royal Strength", NamedTextColor.GOLD);
    }

    @Override
    public Component description() {
        return Component.text("Hit harder while wearing the crown.", NamedTextColor.GRAY);
    }

    @Override
    public void onCrowned(Player king, ConfigurationSection options) {
        int amplifier = Math.max(0, options.getInt("amplifier", 1));
        king.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, PotionEffect.INFINITE_DURATION,
                amplifier, true, false, false));
    }

    @Override
    public void onDeposed(Player king, ConfigurationSection options) {
        king.removePotionEffect(PotionEffectType.STRENGTH);
    }
}
