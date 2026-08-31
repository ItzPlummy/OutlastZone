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

                if (System.nanoTime() - executionStartTime >= getSettings().getTerrainSearchExecutionTimeoutMillis() * 1000000) {
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

        for (Terrain terrain : getTerrains().all()) {
            int foundLocationsCount = getSpawnLocationPool().size(terrain);
            terrainsByFoundLocationsCount.computeIfAbsent(foundLocationsCount, key -> new ArrayList<>()).add(terrain);
        }

        Map.Entry<Integer, Collection<Terrain>> lowestCountEntry = terrainsByFoundLocationsCount.firstEntry();

        if (lowestCountEntry.getKey() >= getSettings().getTerrainSearchLocationsPerTerrain()) {
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
            int x = ThreadLocalRandom.current().nextInt(-getSettings().getOriginSearchOuterRadius(), getSettings().getOriginSearchOuterRadius() + 1);
            int z = ThreadLocalRandom.current().nextInt(-getSettings().getOriginSearchOuterRadius(), getSettings().getOriginSearchOuterRadius() + 1);
            originSearchContext.setRandomOrigin(new Location(world, x, getSettings().getTerrainSearchOptimalY(), z));
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
                    getSettings().getOriginSearchInnerRadius(),
                    32,
                    384,
                    originSearchContext.getBiomes().toArray(Biome[]::new)
            );

            if (System.nanoTime() - executionStartTime >= getSettings().getTerrainSearchExecutionTimeoutMillis() * 1000000) {
                setPhase(SpawnLocationWorkerPhase.IDLE);
                return;
            }

            if (biomeSearchResult == null) {
                continue;
            }

            structureSearchContext.setOrigin(new Location(world, biomeSearchResult.getLocation().getBlockX(), getSettings().getTerrainSearchOptimalY(), biomeSearchResult.getLocation().getBlockZ()));
            structureSearchContext.resetX(getSettings().getStructureSearchRadius());
            structureSearchContext.resetZ(getSettings().getStructureSearchRadius());
            setPhase(SpawnLocationWorkerPhase.SEARCHING_FOR_STRUCTURE);
            return;
        }
    }

    protected void searchForStructure() {
        while (structureSearchContext.xIsWithinSearchRadius(getSettings().getStructureSearchRadius())) {
            while (structureSearchContext.zIsWithinSearchRadius(getSettings().getStructureSearchRadius())) {
                StructureSearchResult structureSearchResult = world.locateNearestStructure(
                        new Location(world, structureSearchContext.getX(), getSettings().getTerrainSearchOptimalY(), structureSearchContext.getZ()),
                        structureSearchContext.getStructure(),
                        2,
                        false
                );

                structureSearchContext.setZ(structureSearchContext.getZ() + getSettings().getStructureSearchDelta());

                if (System.nanoTime() - executionStartTime >= getSettings().getTerrainSearchExecutionTimeoutMillis() * 1000000) {
                    return;
                }

                if (structureSearchResult == null) {
                    continue;
                }

                optimalLocationSelectContext.setOrigin(new Location(world, structureSearchResult.getLocation().getBlockX(), getSettings().getTerrainSearchOptimalY(), structureSearchResult.getLocation().getBlockZ()));
                setPhase(SpawnLocationWorkerPhase.SELECTING_OPTIMAL_LOCATION);
                return;
            }

            structureSearchContext.setX(structureSearchContext.getX() + getSettings().getStructureSearchDelta());
            structureSearchContext.resetZ(getSettings().getStructureSearchRadius());
        }

        setPhase(SpawnLocationWorkerPhase.IDLE);
    }

    protected void selectOptimalLocation() {
        while (optimalLocationSelectContext.getCheckedLocationsCount() < getSettings().getOptimalSelectionAttempts()) {
            if (optimalLocationSelectContext.getCurrentSelection() == null) {
                int offsetX = ThreadLocalRandom.current().nextInt(getSettings().getOptimalSelectionMinOffset(), getSettings().getOptimalSelectionMaxOffset() + 1);
                int offsetZ = ThreadLocalRandom.current().nextInt(getSettings().getOptimalSelectionMinOffset(), getSettings().getOptimalSelectionMaxOffset() + 1);

                if (ThreadLocalRandom.current().nextBoolean()) {
                    offsetX = -offsetX;
                }
                if (ThreadLocalRandom.current().nextBoolean()) {
                    offsetZ = -offsetZ;
                }

                int x = optimalLocationSelectContext.getOrigin().getBlockX() + offsetX;
                int z = optimalLocationSelectContext.getOrigin().getBlockZ() + offsetZ;

                x = x - x % getSettings().getOptimalSelectionDelta();
                z = z - z % getSettings().getOptimalSelectionDelta();

                optimalLocationSelectContext.setCurrentSelection(new Location(world, x, getSettings().getTerrainSearchOptimalY(), z));
                optimalLocationSelectContext.resetX(getSettings().getOptimalSelectionRadius());
                optimalLocationSelectContext.resetZ(getSettings().getOptimalSelectionRadius());
                optimalLocationSelectContext.setSuitableCount(0);
                optimalLocationSelectContext.setWholeCount(0);
            }

            while (optimalLocationSelectContext.xIsWithinSearchRadius(getSettings().getOptimalSelectionRadius())) {
                while (optimalLocationSelectContext.zIsWithinSearchRadius(getSettings().getOptimalSelectionRadius())) {
                    Boolean isSuitable = optimalLocationSelectContext.getBiomeSuitability(getSettings().getTerrainSearchOptimalY(), terrain.getBiomes());

                    optimalLocationSelectContext.setWholeCount(optimalLocationSelectContext.getWholeCount() + 1);
                    if (isSuitable != null && isSuitable) {
                        optimalLocationSelectContext.setSuitableCount(optimalLocationSelectContext.getSuitableCount() + 1);
                    }

                    optimalLocationSelectContext.setZ(optimalLocationSelectContext.getZ() + getSettings().getOptimalSelectionDelta());

                    if (System.nanoTime() - executionStartTime >= getSettings().getTerrainSearchExecutionTimeoutMillis() * 1000000) {
                        return;
                    }
                }

                optimalLocationSelectContext.setX(optimalLocationSelectContext.getX() + getSettings().getOptimalSelectionDelta());
                optimalLocationSelectContext.resetZ(getSettings().getOptimalSelectionRadius());
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

        Location optimalLocation = optimalLocationSelectContext.getOptimalLocation(getSettings().getOptimalSelectionThreshold());

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
