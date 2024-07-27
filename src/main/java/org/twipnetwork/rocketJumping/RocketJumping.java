package org.twipnetwork.rocketJumping;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public final class RocketJumping extends JavaPlugin implements Listener {

    private double boostPower;
    private boolean damageShooter;
    private boolean onlyCrossbow;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        boostPower = config.getDouble("boostPower", 4.0);
        damageShooter = config.getBoolean("damageShooter", false);
        onlyCrossbow = config.getBoolean("onlyCrossbow", true);
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();

        if (projectile instanceof Firework firework) {
            Entity shooter = (Entity) firework.getShooter();

            if (onlyCrossbow && shooter instanceof Player player) {
                ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
                ItemStack itemInOffHand = player.getInventory().getItemInOffHand();
                boolean shotFromCrossbow = false;

                if (itemInMainHand.getItemMeta() instanceof CrossbowMeta meta) {
                    if (meta.getChargedProjectiles().contains(firework)) {
                        shotFromCrossbow = true;
                    }
                }

                if (itemInOffHand.getItemMeta() instanceof CrossbowMeta meta) {
                    if (meta.getChargedProjectiles().contains(firework)) {
                        shotFromCrossbow = true;
                    }
                }

                if (!shotFromCrossbow) {
                    return;
                }
            }

            for (Entity entity : firework.getNearbyEntities(5, 5, 5)) {
                if (entity instanceof Player player) {
                    Vector boostDirection = player.getLocation().toVector().subtract(firework.getLocation().toVector()).normalize();
                    player.setVelocity(player.getVelocity().add(boostDirection.multiply(boostPower)));

                    if (!damageShooter && player.equals(shooter)) {
                        player.setNoDamageTicks(1);
                    }
                } else {
                    Vector boostDirection = entity.getLocation().toVector().subtract(firework.getLocation().toVector()).normalize();
                    entity.setVelocity(entity.getVelocity().add(boostDirection.multiply(boostPower)));
                }
            }
        }
    }
}