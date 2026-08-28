package com.plummy.outlastzone;

import com.plummy.outlastzone.commands.OutlastCommand;
import com.plummy.outlastzone.commands.OutlastTabCompleter;
import com.plummy.outlastzone.games.GameManager;
import com.plummy.outlastzone.listeners.*;
import com.plummy.outlastzone.listeners.enhancement.EnchantmentEnhancementListener;
import com.plummy.outlastzone.listeners.enhancement.FoodEnhancementListener;
import com.plummy.outlastzone.listeners.enhancement.LootEnhancementListener;
import com.plummy.outlastzone.listeners.enhancement.SmeltingEnhancementListener;
import com.plummy.outlastzone.players.PersistentPlayerRepository;
import com.plummy.outlastzone.pools.LocationPool;
import com.plummy.outlastzone.terrain.TerrainRepository;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static com.plummy.outlastzone.listeners.enhancement.EnchantmentEnhancementListener.registerRecipe;
import static com.plummy.outlastzone.populators.PopulatorRegistry.ORE_ENHANCEMENT_POPULATOR;

public final class OutlastZone extends JavaPlugin {

    private static final NamespacedKey namespacedKey = Objects.requireNonNull(NamespacedKey.fromString("outlastzone"));

    private static OutlastZone instance;
    private static Logger logger;

    private static GameManager gameManager;
    private static PersistentPlayerRepository persistentPlayers;

    private static TerrainRepository terrains;
    private static LocationPool locationPool;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();

        gameManager = new GameManager();
        persistentPlayers = PersistentPlayerRepository.load();

        terrains = TerrainRepository.load();
        locationPool = new LocationPool();

        saveDefaultConfig();

        for (World world : Bukkit.getWorlds()) {
            world.getPopulators().add(ORE_ENHANCEMENT_POPULATOR);
        }

        registerRecipe();

        Objects.requireNonNull(getCommand("outlastzone")).setExecutor(new OutlastCommand());
        Objects.requireNonNull(getCommand("outlastzone")).setTabCompleter(new OutlastTabCompleter());

        List<Listener> listeners = List.of(
                new PlayerJoinListener(),
                new PlayerMovementListener(),
                new PlayerDamageListener(),
                new PlayerDeathListener(),
                new PopulationListener(),
                new LootEnhancementListener(),
                new SmeltingEnhancementListener(),
                new EnchantmentEnhancementListener(),
                new FoodEnhancementListener()
        );

        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }
    }

    @Override
    public void onDisable() {
        persistentPlayers.save();
        Bukkit.getScheduler().cancelTasks(getInstance());
    }

    public static NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    public static OutlastZone getInstance() {
        return instance;
    }

    public static Logger logger() {
        return logger;
    }

    public static GameManager getGameManager() {
        return gameManager;
    }

    public static PersistentPlayerRepository getPersistentPlayers() {
        return persistentPlayers;
    }

    public static TerrainRepository getTerrains() {
        return terrains;
    }

    public static LocationPool getLocationPool() {
        return locationPool;
    }
}
