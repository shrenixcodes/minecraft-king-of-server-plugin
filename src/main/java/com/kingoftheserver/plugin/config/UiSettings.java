package com.kingoftheserver.plugin.config;

public record UiSettings(boolean bossBarEnabled, String bossBarColor, String bossBarStyle,
                          boolean actionBarEnabled, boolean scoreboardEnabled,
                          boolean particlesEnabled, boolean soundsEnabled) {
}
