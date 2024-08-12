package org.twipnetwork.rocketJumping;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public final class RocketJumping extends JavaPlugin implements Listener {

    private double boostPower;
    private boolean damageShooter;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigValues();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
    }

    private void loadConfigValues() {
        FileConfiguration config = getConfig();
        boostPower = config.getDouble("boostPower", 4.0);
        damageShooter = config.getBoolean("damageShooter", false);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();

        if (projectile instanceof Firework firework) {
            Entity shooter = (Entity) firework.getShooter();

            for (Entity entity : firework.getNearbyEntities(5, 5, 5)) {
                Vector boostDirection = entity.getLocation().toVector().subtract(firework.getLocation().toVector()).normalize();
                entity.setVelocity(entity.getVelocity().add(boostDirection.multiply(boostPower)));

                if (entity instanceof Player player && !damageShooter && player.equals(shooter)) {
                    player.setNoDamageTicks(1);
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("rocketlauncher")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                reloadConfig();
                loadConfigValues();
                sender.sendMessage("§aRocketLauncher config reloaded!");
                return true;
            }
        }
        return false;
    }

}
