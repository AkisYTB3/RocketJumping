package org.notionsmp.rocketJumping;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.notionsmp.rocketJumping.commands.RocketJumpingCommand;
import org.notionsmp.rocketJumping.listeners.ProjectileHit;

import java.io.File;

@Getter
public final class RocketJumping extends JavaPlugin implements Listener {

    @Getter
    private static RocketJumping instance;
    private static final double CURRENT_CONFIG_VERSION = 2.0;
    private PaperCommandManager commandManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        migrateConfig();

        registerListener(new ProjectileHit());

        commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new RocketJumpingCommand());
    }

    private void migrateConfig() {
        FileConfiguration config = getConfig();

        if (!config.contains("config-version") || config.getDouble("config-version") != CURRENT_CONFIG_VERSION) {
            double oldBoostPower = config.getDouble("boostPower", 4.0);
            boolean oldDamageShooter = config.getBoolean("damageShooter", false);

            File configFile = new File(getDataFolder(), "config.yml");
            configFile.delete();

            saveDefaultConfig();
            reloadConfig();

            config.set("boostPower", oldBoostPower);
            config.set("damageShooter", oldDamageShooter);

            config.set("config-version", CURRENT_CONFIG_VERSION);
            saveConfig();
        }
    }

    private void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    public boolean reloadPluginConfig() {
        try {
            reloadConfig();
            saveDefaultConfig();
            getLogger().info("Config reloaded successfully!");
            return true;
        } catch (Exception e) {
            getLogger().severe("Failed to reload config: " + e.getMessage());
            return false;
        }
    }
}