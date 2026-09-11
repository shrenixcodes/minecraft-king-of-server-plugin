package com.kingoftheserver.plugin.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Small helpers for consistently formatted command feedback, so every subcommand looks
 * and feels the same without copy-pasting the same prefix everywhere.
 */
public final class Messages {

    private static final Component PREFIX = Component.text("[", NamedTextColor.GOLD)
            .append(Component.text("King", NamedTextColor.YELLOW))
            .append(Component.text("] ", NamedTextColor.GOLD));

    private Messages() {
    }

    public static Component error(String message) {
        return PREFIX.append(Component.text(message, NamedTextColor.RED));
    }

    public static Component success(String message) {
        return PREFIX.append(Component.text(message, NamedTextColor.GREEN));
    }

    public static Component info(String message) {
        return PREFIX.append(Component.text(message, NamedTextColor.YELLOW));
    }

    public static Component plain(String message) {
        return Component.text(message, NamedTextColor.GRAY);
    }
}
