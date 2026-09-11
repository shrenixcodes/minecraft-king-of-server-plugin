package com.kingoftheserver.plugin.ability;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Grants the King night vision for the duration of their reign.
 */
public final class RoyalVision implements KingAbility {

    public static final String ID = "royal-vision";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public Component displayName() {
        return Component.text("Royal Vision", NamedTextColor.GOLD);
    }

    @Override
    public Component description() {
        return Component.text("See clearly in the dark while wearing the crown.", NamedTextColor.GRAY);
    }

    @Override
    public void onCrowned(Player king, ConfigurationSection options) {
        king.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION,
                0, true, false, false));
    }

    @Override
    public void onDeposed(Player king, ConfigurationSection options) {
        king.removePotionEffect(PotionEffectType.NIGHT_VISION);
    }
}
