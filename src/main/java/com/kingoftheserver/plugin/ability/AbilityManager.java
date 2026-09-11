package com.kingoftheserver.plugin.ability;

import com.kingoftheserver.plugin.config.AbilitySettings;
import com.kingoftheserver.plugin.config.ConfigManager;
import com.kingoftheserver.plugin.config.PluginConfig;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * Applies and removes {@link KingAbility} effects as the crown changes hands. New
 * abilities are added to {@link #abilities} only; nothing else in the plugin needs to
 * change.
 */
public final class AbilityManager {

    private final ConfigManager configManager;
    private final List<KingAbility> abilities = List.of(
            new RoyalSpeed(), new RoyalStrength(), new RoyalJump(), new RoyalVision(), new RoyalShield());

    public AbilityManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void crown(Player king) {
        PluginConfig config = configManager.current();
        for (KingAbility ability : abilities) {
            AbilitySettings settings = config.ability(ability.id());
            if (settings.enabled()) {
                ability.onCrowned(king, settings.options());
            }
        }
    }

    public void depose(Player king) {
        PluginConfig config = configManager.current();
        for (KingAbility ability : abilities) {
            ability.onDeposed(king, config.ability(ability.id()).options());
        }
    }

    /**
     * Called when a King sneaks, giving any triggered ability (such as Royal Shield)
     * a chance to activate.
     */
    public void handleSneakTrigger(Player king) {
        PluginConfig config = configManager.current();
        for (KingAbility ability : abilities) {
            if (!ability.isTriggered()) {
                continue;
            }
            AbilitySettings settings = config.ability(ability.id());
            if (settings.enabled()) {
                ability.onTrigger(king, settings.options());
            }
        }
    }

    public void forgetPlayer(UUID uuid) {
        for (KingAbility ability : abilities) {
            ability.forget(uuid);
        }
    }

    public List<KingAbility> abilities() {
        return abilities;
    }

    public List<KingAbility> enabledAbilities() {
        PluginConfig config = configManager.current();
        return abilities.stream().filter(a -> config.ability(a.id()).enabled()).toList();
    }
}
