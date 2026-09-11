package com.kingoftheserver.plugin;

import com.kingoftheserver.plugin.ability.AbilityManager;
import com.kingoftheserver.plugin.command.KingAbilitiesSubcommand;
import com.kingoftheserver.plugin.command.KingCommandExecutor;
import com.kingoftheserver.plugin.command.KingInfoSubcommand;
import com.kingoftheserver.plugin.command.KingNextSubcommand;
import com.kingoftheserver.plugin.command.KingReloadSubcommand;
import com.kingoftheserver.plugin.command.KingSetSubcommand;
import com.kingoftheserver.plugin.command.KingStartSubcommand;
import com.kingoftheserver.plugin.command.KingStatsSubcommand;
import com.kingoftheserver.plugin.command.KingStatusSubcommand;
import com.kingoftheserver.plugin.command.KingStopSubcommand;
import com.kingoftheserver.plugin.command.KingSubcommand;
import com.kingoftheserver.plugin.config.ConfigManager;
import com.kingoftheserver.plugin.king.KingManager;
import com.kingoftheserver.plugin.listener.PlayerConnectionListener;
import com.kingoftheserver.plugin.listener.PlayerStateListener;
import com.kingoftheserver.plugin.persistence.RoundStateRepository;
import com.kingoftheserver.plugin.persistence.StatsRepository;
import com.kingoftheserver.plugin.ui.UiManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class KingOfTheServerPlugin extends JavaPlugin {

    private ConfigManager configManager;
    private AbilityManager abilityManager;
    private UiManager uiManager;
    private KingManager kingManager;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        configManager.load();

        abilityManager = new AbilityManager(configManager);
        uiManager = new UiManager(configManager);

        StatsRepository statsRepository = new StatsRepository(this);
        RoundStateRepository roundStateRepository = new RoundStateRepository(this);

        kingManager = new KingManager(this, configManager, abilityManager, uiManager, statsRepository, roundStateRepository);
        kingManager.initialize();

        for (Player player : getServer().getOnlinePlayers()) {
            uiManager.registerPlayer(player);
        }
        kingManager.currentKing().ifPresent(uuid -> {
            Player king = getServer().getPlayer(uuid);
            if (king != null) {
                abilityManager.crown(king);
                uiManager.onCrowned(king);
            }
        });

        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(kingManager, uiManager), this);
        getServer().getPluginManager().registerEvents(
                new PlayerStateListener(this, kingManager, abilityManager, uiManager), this);

        registerCommands();

        getLogger().info("King of the Server enabled. Rotation is " + (kingManager.isRunning() ? "running." : "stopped."));
    }

    @Override
    public void onDisable() {
        if (kingManager != null) {
            kingManager.shutdown();
        }
        if (uiManager != null) {
            uiManager.shutdown();
        }
    }

    private void registerCommands() {
        List<KingSubcommand> subcommands = List.of(
                new KingInfoSubcommand(kingManager, configManager, this),
                new KingStatusSubcommand(kingManager),
                new KingAbilitiesSubcommand(abilityManager, configManager),
                new KingStatsSubcommand(kingManager),
                new KingStartSubcommand(kingManager),
                new KingStopSubcommand(kingManager),
                new KingNextSubcommand(kingManager),
                new KingSetSubcommand(kingManager),
                new KingReloadSubcommand(configManager, kingManager)
        );

        KingCommandExecutor executor = new KingCommandExecutor(kingManager, subcommands);
        PluginCommand command = getCommand("king");
        if (command != null) {
            command.setExecutor(executor);
            command.setTabCompleter(executor);
        } else {
            getLogger().severe("Failed to register /king command: not found in plugin.yml");
        }
    }
}
