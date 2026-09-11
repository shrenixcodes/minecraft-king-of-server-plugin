package com.kingoftheserver.plugin.command;

import com.kingoftheserver.plugin.ability.AbilityManager;
import com.kingoftheserver.plugin.ability.KingAbility;
import com.kingoftheserver.plugin.config.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

public final class KingAbilitiesSubcommand implements KingSubcommand {

    private final AbilityManager abilityManager;
    private final ConfigManager configManager;

    public KingAbilitiesSubcommand(AbilityManager abilityManager, ConfigManager configManager) {
        this.abilityManager = abilityManager;
        this.configManager = configManager;
    }

    @Override
    public String name() {
        return "abilities";
    }

    @Override
    public String permission() {
        return null;
    }

    @Override
    public String usage() {
        return "/king abilities";
    }

    @Override
    public String description() {
        return "List the abilities available to the King.";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(Component.text("♕ King Abilities", NamedTextColor.GOLD));
        for (KingAbility ability : abilityManager.abilities()) {
            boolean enabled = configManager.current().ability(ability.id()).enabled();
            Component status = enabled
                    ? Component.text(" [enabled]", NamedTextColor.GREEN)
                    : Component.text(" [disabled]", NamedTextColor.RED);
            sender.sendMessage(Component.text(" - ", NamedTextColor.GRAY)
                    .append(ability.displayName())
                    .append(status));
            sender.sendMessage(Component.text("     ", NamedTextColor.GRAY).append(ability.description()));
        }
    }
}
