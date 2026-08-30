package com.plummy.outlastzone.workers.spawnLocation;

import com.plummy.outlastzone.games.Game;
import com.plummy.outlastzone.games.GamePhase;
import com.plummy.outlastzone.terrain.Terrain;
import com.plummy.outlastzone.workers.AbstractWorker;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.structure.Structure;
import org.bukkit.util.BiomeSearchResult;
import org.bukkit.util.StructureSearchResult;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.plummy.outlastzone.OutlastZone.*;

public class SpawnLocationWorker extends AbstractWorker {

    private static final int OPTIMAL_Y = 128;
    private static final int EXECUTION_TIMEOUT_MILLIS = 5;
    private static final int MAX_LOCATIONS_PER_TERRAIN = 3;

    private static final int OUTER_SEARCH_RADIUS = 100000;
    private static final int BIOME_SEARCH_RADIUS = 2048;

    private static final int STRUCTURE_SEARCH_RADIUS = 2048;
    private static final int STRUCTURE_SEARCH_DELTA = 128;

    private static final int OPTIMAL_SELECT_COUNT = 4;
    private static final int OPTIMAL_SELECT_MIN_RADIUS = 16;
    private static final int OPTIMAL_SELECT_MAX_RADIUS = 48;
    private static final int OPTIMAL_SELECT_SEARCH_RADIUS = 128;
    private static final int OPTIMAL_SELECT_SEARCH_DELTA = 4;
    private static final double OPTIMAL_SELECT_THRESHOLD = 0.5;

    private final World world;

    private long executionStartTime = 0;
    private SpawnLocationWorkerPhase phase = SpawnLocationWorkerPhase.IDLE;
    private Terrain terrain = null;

    private OriginSearchContext originSearchContext = new OriginSearchContext();
    private StructureSearchContext structureSearchContext = new StructureSearchContext();
    private OptimalLocationSelectContext optimalLocationSelectContext = new OptimalLocationSelectContext();

    public SpawnLocationWorker(World world) {
        super();
        this.world = world;
    }

    public SpawnLocationWorker(World world, double intervalTicks) {
        super(intervalTicks);
        this.world = world;
    }

    @Override
    public void execute() {
        executionStartTime = System.nanoTime();

        while (true) {
            if (getPhase() == SpawnLocationWorkerPhase.IDLE) {
                idle();
            }

            if (getPhase() != SpawnLocationWorkerPhase.IDLE) {
                switch (getPhase()) {
                    case SEARCHING_FOR_ORIGIN -> searchForOrigin();
                    case SEARCHING_FOR_STRUCTURE -> searchForStructure();
                    case SELECTING_OPTIMAL_LOCATION -> selectOptimalLocation();
                }

                if (System.nanoTime() - executionStartTime >= EXECUTION_TIMEOUT_MILLIS * 1000000) {
                    return;
                }

                continue;
            }

            return;
        }
    }

    protected SpawnLocationWorkerPhase getPhase() {
        return phase;
    }

    protected void setPhase(SpawnLocationWorkerPhase phase) {
        this.phase = phase;
    }

    protected void idle() {
        Game game = getGameManager().getGame();

        if (game != null && game.getPhase() != GamePhase.GRINDING) {
            return;
        }

        NavigableMap<Integer, Collection<Terrain>> terrainsByFoundLocationsCount = new TreeMap<>();

        for (Terrain terrain : getTerrains().getAllTerrains()) {
            int foundLocationsCount = getSpawnLocationPool().size(terrain);
            terrainsByFoundLocationsCount.computeIfAbsent(foundLocationsCount, key -> new ArrayList<>()).add(terrain);
        }

        Map.Entry<Integer, Collection<Terrain>> lowestCountEntry = terrainsByFoundLocationsCount.firstEntry();

        if (lowestCountEntry.getKey() >= MAX_LOCATIONS_PER_TERRAIN) {
            return;
        }

        terrain = lowestCountEntry.getValue().stream().findAny().orElse(null);

        if (terrain == null) {
            return;
        }

        resetContexts();
        setPhase(SpawnLocationWorkerPhase.SEARCHING_FOR_ORIGIN);
    }

    protected void searchForOrigin() {
        if (originSearchContext.getRandomOrigin() == null) {
            int x = ThreadLocalRandom.current().nextInt(-OUTER_SEARCH_RADIUS, OUTER_SEARCH_RADIUS + 1);
            int z = ThreadLocalRandom.current().nextInt(-OUTER_SEARCH_RADIUS, OUTER_SEARCH_RADIUS + 1);
            originSearchContext.setRandomOrigin(new Location(world, x, OPTIMAL_Y, z));
        }

        if (originSearchContext.getBiomes() == null) {
            Map.Entry<Structure, Set<Biome>> structures = terrain.getStructures()
                    .entrySet()
                    .stream()
                    .skip(ThreadLocalRandom.current().nextInt(terrain.getStructures().size()))
                    .findFirst()
                    .orElseThrow();

            structureSearchContext.setStructure(structures.getKey());
            originSearchContext.setBiomes(structures.getValue());
        }

        while (true) {
            BiomeSearchResult biomeSearchResult = world.locateNearestBiome(
                    originSearchContext.getRandomOrigin(),
                    BIOME_SEARCH_RADIUS,
                    32,
                    384,
                    originSearchContext.getBiomes().toArray(Biome[]::new)
            );

            if (System.nanoTime() - executionStartTime >= EXECUTION_TIMEOUT_MILLIS * 1000000) {
                setPhase(SpawnLocationWorkerPhase.IDLE);
                return;
            }

            if (biomeSearchResult == null) {
                continue;
            }

            structureSearchContext.setOrigin(new Location(world, biomeSearchResult.getLocation().getBlockX(), OPTIMAL_Y, biomeSearchResult.getLocation().getBlockZ()));
            structureSearchContext.resetX(STRUCTURE_SEARCH_RADIUS);
            structureSearchContext.resetZ(STRUCTURE_SEARCH_RADIUS);
            setPhase(SpawnLocationWorkerPhase.SEARCHING_FOR_STRUCTURE);
            return;
        }
    }

    protected void searchForStructure() {
        while (structureSearchContext.xIsWithinSearchRadius(STRUCTURE_SEARCH_RADIUS)) {
            while (structureSearchContext.zIsWithinSearchRadius(STRUCTURE_SEARCH_RADIUS)) {
                StructureSearchResult structureSearchResult = world.locateNearestStructure(
                        new Location(world, structureSearchContext.getX(), OPTIMAL_Y, structureSearchContext.getZ()),
                        structureSearchContext.getStructure(),
                        4,
                        false
                );

                structureSearchContext.setZ(structureSearchContext.getZ() + STRUCTURE_SEARCH_DELTA);

                if (System.nanoTime() - executionStartTime >= EXECUTION_TIMEOUT_MILLIS * 1000000) {
                    return;
                }

                if (structureSearchResult == null) {
                    continue;
                }

                optimalLocationSelectContext.setOrigin(new Location(world, structureSearchResult.getLocation().getBlockX(), OPTIMAL_Y, structureSearchResult.getLocation().getBlockZ()));
                setPhase(SpawnLocationWorkerPhase.SELECTING_OPTIMAL_LOCATION);
                return;
            }

            structureSearchContext.setX(structureSearchContext.getX() + STRUCTURE_SEARCH_DELTA);
            structureSearchContext.resetZ(STRUCTURE_SEARCH_RADIUS);
        }

        setPhase(SpawnLocationWorkerPhase.IDLE);
    }

    protected void selectOptimalLocation() {
        while (optimalLocationSelectContext.getCheckedLocationsCount() < OPTIMAL_SELECT_COUNT) {
            if (optimalLocationSelectContext.getCurrentSelection() == null) {
                int offsetX = ThreadLocalRandom.current().nextInt(OPTIMAL_SELECT_MIN_RADIUS, OPTIMAL_SELECT_MAX_RADIUS + 1);
                int offsetZ = ThreadLocalRandom.current().nextInt(OPTIMAL_SELECT_MIN_RADIUS, OPTIMAL_SELECT_MAX_RADIUS + 1);

                if (ThreadLocalRandom.current().nextBoolean()) {
                    offsetX = -offsetX;
                }
                if (ThreadLocalRandom.current().nextBoolean()) {
                    offsetZ = -offsetZ;
                }

                int x = optimalLocationSelectContext.getOrigin().getBlockX() + offsetX;
                int z = optimalLocationSelectContext.getOrigin().getBlockZ() + offsetZ;

                x = x - x % OPTIMAL_SELECT_SEARCH_DELTA;
                z = z - z % OPTIMAL_SELECT_SEARCH_DELTA;

                optimalLocationSelectContext.setCurrentSelection(new Location(world, x, OPTIMAL_Y, z));
                optimalLocationSelectContext.resetX(OPTIMAL_SELECT_SEARCH_RADIUS);
                optimalLocationSelectContext.resetZ(OPTIMAL_SELECT_SEARCH_RADIUS);
                optimalLocationSelectContext.setSuitableCount(0);
                optimalLocationSelectContext.setWholeCount(0);
            }

            while (optimalLocationSelectContext.xIsWithinSearchRadius(OPTIMAL_SELECT_SEARCH_RADIUS)) {
                while (optimalLocationSelectContext.zIsWithinSearchRadius(OPTIMAL_SELECT_SEARCH_RADIUS)) {
                    Boolean isSuitable = optimalLocationSelectContext.getBiomeSuitability(OPTIMAL_Y, terrain.getBiomes());

                    optimalLocationSelectContext.setWholeCount(optimalLocationSelectContext.getWholeCount() + 1);
                    if (isSuitable != null && isSuitable) {
                        optimalLocationSelectContext.setSuitableCount(optimalLocationSelectContext.getSuitableCount() + 1);
                    }

                    optimalLocationSelectContext.setZ(optimalLocationSelectContext.getZ() + OPTIMAL_SELECT_SEARCH_DELTA);

                    if (System.nanoTime() - executionStartTime >= EXECUTION_TIMEOUT_MILLIS * 1000000) {
                        return;
                    }
                }

                optimalLocationSelectContext.setX(optimalLocationSelectContext.getX() + OPTIMAL_SELECT_SEARCH_DELTA);
                optimalLocationSelectContext.resetZ(OPTIMAL_SELECT_SEARCH_RADIUS);
            }

            Double score;

            if (optimalLocationSelectContext.getSuitableCount() > 0) {
                score = (double) optimalLocationSelectContext.getSuitableCount() / optimalLocationSelectContext.getWholeCount();
            } else {
                score = null;
            }

            optimalLocationSelectContext.addSuitabilityScore(optimalLocationSelectContext.getCurrentSelection(), score);
            optimalLocationSelectContext.setCurrentSelection(null);
        }

        setPhase(SpawnLocationWorkerPhase.IDLE);

        Location optimalLocation = optimalLocationSelectContext.getOptimalLocation(OPTIMAL_SELECT_THRESHOLD);

        if (optimalLocation != null) {
            getSpawnLocationPool().push(terrain, optimalLocation);
        }
    }

    protected void resetContexts() {
        originSearchContext = new OriginSearchContext();
        structureSearchContext = new StructureSearchContext();
        optimalLocationSelectContext = new OptimalLocationSelectContext();
    }
}
