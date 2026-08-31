package com.plummy.outlastzone.core.games;

import com.plummy.outlastzone.core.games.arena.Arena;
import com.plummy.outlastzone.core.games.arena.DefaultArena;
import com.plummy.outlastzone.core.games.phases.FightPhaseWorker;
import com.plummy.outlastzone.core.games.phases.GrindPhaseWorker;
import com.plummy.outlastzone.core.games.phases.SetupPhaseWorker;
import com.plummy.outlastzone.core.players.ActivePlayer;
import com.plummy.outlastzone.core.data.repositories.ActivePlayerRepository;
import com.plummy.outlastzone.core.keyed.Terrain;
import com.plummy.outlastzone.core.visual.bossBars.BossBarDisplay;
import com.plummy.outlastzone.core.visual.announcers.DefaultGameAnnouncer;
import com.plummy.outlastzone.core.visual.displays.DisplayWheelRegistry;
import com.plummy.outlastzone.core.visual.announcers.GameAnnouncer;
import org.bukkit.Location;

import static com.plummy.outlastzone.OutlastZone.*;

public class DefaultGame implements Game {

    private final Terrain terrain;
    private final ActivePlayerRepository activePlayers;
    private final GameAnnouncer announcer;

    private GamePhase phase = GamePhase.IDLE;

    private Arena arena = null;
    private SetupPhaseWorker setupWorker = null;
    private GrindPhaseWorker grindWorker = null;
    private FightPhaseWorker fightWorker = null;

    public DefaultGame(Terrain terrain) {
        this.terrain = terrain;
        this.activePlayers = new ActivePlayerRepository();
        this.announcer = new DefaultGameAnnouncer(activePlayers);

        activePlayers.load();
    }

    @Override
    public GamePhase getPhase() {
        return phase;
    }

    @Override
    public Terrain getTerrain() {
        return terrain;
    }

    @Override
    public ActivePlayerRepository getPlayers() {
        return activePlayers;
    }

    public GameAnnouncer getAnnouncer() {
        return announcer;
    }

    @Override
    public Arena getArena() {
        return arena;
    }

    @Override
    public BossBarDisplay getActiveBossBar() {
        if (grindWorker != null) {
            return grindWorker.getBossBar();
        }

        if (fightWorker != null) {
            return fightWorker.getBossBar();
        }

        return null;
    }

    @Override
    public void start(ActivePlayer host) {
        setPhase(GamePhase.LOCATING);

        Location spawnLocation = getSpawnLocationPool().pop(getTerrain());

        if (spawnLocation == null) {
            getAnnouncer().locationNotReady();
            finish(GameFinishReason.NO_LOCATION_FOUND);
            return;
        }

        setArena(new DefaultArena(spawnLocation));

        setPhase(GamePhase.SETUP);

        for (ActivePlayer player : getPlayers().allOnlineParticipants()) {
            player.prepareForSetup();
            player.getBukkitPlayer().teleport(getArena().scatterPoint(getPlayers().onlineParticipantsCount()));
        }

        getArena().prepareForSetup();

        setupWorker = new SetupPhaseWorker(this::revealTerrain, this::endSetup);
        setupWorker.start();
    }

    @Override
    public void startGrindPhase() {
        setPhase(GamePhase.GRINDING);

        stopWorkers();

        getPlayers().allOnline().forEach(ActivePlayer::prepareForGrind);

        grindWorker = new GrindPhaseWorker(getPlayers(), getSettings().getGrindStageDurationSeconds(), this::startFightPhase);
        grindWorker.start();
    }

    @Override
    public void startFightPhase() {
        setPhase(GamePhase.FIGHTING);

        int shrinkDuration = getSettings().getFightStageBorderNarrowingDurationSeconds() * 20;

        getArena().shrinkBorder(getPlayers().onlineParticipantsCount() - 1, shrinkDuration);

        stopWorkers();

        getPlayers().allOnline().forEach(ActivePlayer::prepareForFight);

        fightWorker = new FightPhaseWorker(getPlayers());
        fightWorker.start();
    }

    @Override
    public void eliminate(ActivePlayer player) {
        player.eliminate(getArena().getSpawnLocation());

        if (getPlayers().onlineParticipantsCount() <= 1) {
            finish(GameFinishReason.PLAYER_OUTLASTED);
            return;
        }

        if (getPhase() == GamePhase.FIGHTING) {
            startGrindPhase();
        }
    }

    @Override
    public void finish(GameFinishReason reason) {
        setPhase(GamePhase.FINISHED);

        stopWorkers();

        switch (reason) {
            case STOP_COMMAND_EXECUTED -> getAnnouncer().gameStopped();
            case PLAYER_OUTLASTED -> getAnnouncer().gameOver(getWinner());
            case NO_LOCATION_FOUND -> {}
        }

        getGameManager().removeGame();
    }

    protected void setPhase(GamePhase phase) {
        this.phase = phase;
    }

    protected void setArena(Arena arena) {
        this.arena = arena;
    }

    protected ActivePlayer getWinner() {
        if (getPlayers().onlineParticipantsCount() != 1) {
            return null;
        }

        return getPlayers().allOnlineParticipants().stream().findAny().orElseThrow();
    }

    protected void revealTerrain() {
        for (ActivePlayer player : getPlayers().allOnlineParticipants()) {
            DisplayWheelRegistry.TERRAIN_DISPLAY_WHEEL.reveal(player, getTerrain(), () -> {});
        }
    }

    protected void endSetup() {
        for (ActivePlayer player : getPlayers().allOnlineParticipants()) {
            player.prepareForGame();
        }

        getArena().closeBorder(getPlayers().onlineParticipantsCount());
        startGrindPhase();
    }

    protected void stopWorkers() {
        if (setupWorker != null) {
            setupWorker.stop();
            setupWorker = null;
        }

        if (grindWorker != null) {
            grindWorker.stop();
            grindWorker.getBossBar().hideFrom(getPlayers().allOnline());
            grindWorker = null;
        }

        if (fightWorker != null) {
            fightWorker.stop();
            fightWorker.getBossBar().hideFrom(getPlayers().allOnline());
            fightWorker = null;
        }
    }
}
