package com.plummy.outlastzone.players;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static com.plummy.outlastzone.OutlastZone.getInstance;

public class DefaultActivePlayer implements ActivePlayer {

    private static final int GLOW_DURATION_TICKS = 9999999;

    @NotNull
    private final PersistentPlayer persistentPlayer;

    @NotNull
    private ActivePlayerRole role;

    public DefaultActivePlayer(@NotNull PersistentPlayer persistentPlayer, @NotNull ActivePlayerRole role) {
        this.persistentPlayer = persistentPlayer;
        this.role = role;
    }

    @Override
    public @NotNull PersistentPlayer getPersistentPlayer() {
        return persistentPlayer;
    }

    @Override
    public @NotNull ActivePlayerRole getRole() {
        return role;
    }

    @Override
    public boolean isPlaying() {
        return getRole() == ActivePlayerRole.PLAYER;
    }

    @Override
    public boolean isSpectating() {
        return getRole() == ActivePlayerRole.SPECTATOR;
    }

    @Override
    public void prepareForSetup() {
        Player player = getBukkitPlayer();

        if (player == null) return;

        prepareBasic();

        player.setAllowFlight(true);
        player.setFlySpeed(0);
        Bukkit.getScheduler().runTask(getInstance(), () -> player.setFlying(true));

        player.addPotionEffects(
                List.of(
                        new PotionEffect(PotionEffectType.BLINDNESS, 9999999, 0, false, false, false),
                        new PotionEffect(PotionEffectType.INVISIBILITY, 9999999, 0, false, false, false)
                )
        );
    }

    @Override
    public void prepareForGame() {
        Player player = getBukkitPlayer();

        if (player == null) return;

        prepareBasic();

        player.setAllowFlight(false);
        player.setFlying(false);
        player.setFlySpeed(0.1f);

        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1200, 0, false, false, false));
    }

    @Override
    public void prepareForGrind() {
        Player player = getBukkitPlayer();

        if (player == null) return;

        player.removePotionEffect(PotionEffectType.GLOWING);
    }

    @Override
    public void prepareForFight() {
        Player player = getBukkitPlayer();

        if (player == null) return;

        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, GLOW_DURATION_TICKS, 0, false, false, false));
    }

    @Override
    public void eliminate(Location spectateLocation) {
        setRole(ActivePlayerRole.SPECTATOR);

        Player player = getBukkitPlayer();

        if (player == null) return;

        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(spectateLocation);
    }

    protected void setRole(@NotNull ActivePlayerRole role) {
        this.role = role;
    }

    protected void prepareBasic() {
        Player player = getBukkitPlayer();

        if (player == null) return;

        player.setGameMode(GameMode.SURVIVAL);

        player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getValue());
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setExp(0);
        player.setLevel(0);
        player.setExhaustion(0);
        player.setFireTicks(0);
        player.setRemainingAir(300);
        player.setVelocity(new Vector(0, 0, 0));

        player.getInventory().clear();

        for (Iterator<Advancement> iterator = Bukkit.advancementIterator(); iterator.hasNext(); ) {
            Advancement advancement = iterator.next();
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            new ArrayList<>(progress.getAwardedCriteria()).forEach(progress::revokeCriteria);
        }

        for (Statistic statistic : Statistic.values()) {
            switch (statistic.getType()) {
                case UNTYPED -> player.setStatistic(statistic, 0);
                case ITEM, BLOCK -> {
                    for (Material material : Material.values()) {
                        try {
                            player.setStatistic(statistic, material, 0);
                        } catch (IllegalArgumentException ignored) {}
                    }
                }
                case ENTITY -> {
                    for (EntityType entityType : EntityType.values()) {
                        try {
                            player.setStatistic(statistic, entityType, 0);
                        } catch (IllegalArgumentException ignored) {}
                    }
                }
            }
        }

        player.clearActivePotionEffects();
    }
}
