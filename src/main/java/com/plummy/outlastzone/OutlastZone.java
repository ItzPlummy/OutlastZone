package com.plummy.outlastzone;

import com.plummy.outlastzone.core.data.catalogs.TerrainCatalog;
import com.plummy.outlastzone.commands.OutlastCommand;
import com.plummy.outlastzone.commands.OutlastTabCompleter;
import com.plummy.outlastzone.core.Settings;
import com.plummy.outlastzone.core.games.GameManager;
import com.plummy.outlastzone.listeners.*;
import com.plummy.outlastzone.listeners.enhancement.EnchantmentEnhancementListener;
import com.plummy.outlastzone.listeners.enhancement.AppleEnhancementListener;
import com.plummy.outlastzone.listeners.enhancement.LootEnhancementListener;
import com.plummy.outlastzone.listeners.enhancement.SmeltingEnhancementListener;
import com.plummy.outlastzone.core.pools.SpawnLocationPool;
import com.plummy.outlastzone.core.data.repositories.PersistentPlayerRepository;
import com.plummy.outlastzone.core.workers.spawnLocation.SpawnLocationWorker;
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
import static org.bukkit.Bukkit.getPluginManager;
import static org.bukkit.Bukkit.getScheduler;

public final class OutlastZone extends JavaPlugin {

    private static final NamespacedKey namespacedKey = Objects.requireNonNull(NamespacedKey.fromString("outlastzone"));

    private static OutlastZone instance;
    private static Settings settings;
    private static Logger logger;

    private static final GameManager gameManager = new GameManager();
    private static final PersistentPlayerRepository persistentPlayers = new PersistentPlayerRepository();
    private static final TerrainCatalog terrains = new TerrainCatalog();
    private static final SpawnLocationPool spawnLocationPool = new SpawnLocationPool();

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        settings = new Settings(getConfig());

        persistentPlayers.load();
        terrains.load();

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
                new AppleEnhancementListener(),
                new SmeltingEnhancementListener(),
                new EnchantmentEnhancementListener()
        );

        for (Listener listener : listeners) {
            getPluginManager().registerEvents(listener, this);
        }

        SpawnLocationWorker spawnLocationWorker = new SpawnLocationWorker(Bukkit.getWorlds().getFirst(), 2);
        getScheduler().runTaskTimer(this, spawnLocationWorker::step, 1L, 1L);
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        getPersistentPlayers().save();
    }

    public static NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    public static OutlastZone getInstance() {
        return instance;
    }

    public static Settings getSettings() {
        return settings;
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

    public static TerrainCatalog getTerrains() {
        return terrains;
    }

    public static SpawnLocationPool getSpawnLocationPool() {
        return spawnLocationPool;
    }
}
