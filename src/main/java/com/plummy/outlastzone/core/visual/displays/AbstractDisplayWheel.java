package com.plummy.outlastzone.core.visual.displays;

import com.plummy.outlastzone.core.players.ActivePlayer;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Display;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.plummy.outlastzone.OutlastZone.getInstance;

public abstract class AbstractDisplayWheel<T extends Displayable<D>, D extends Display> implements DisplayWheel<T, D> {

    private static final int DISPLAY_COUNT = 20;
    private static final int DURATION_TICKS = 100;
    private static final double ROTATION_SPEED = 10;
    private static final double WHEEL_RADIUS = 3;
    private static final double WHEEL_OFFSET = 4.25;

    private static final Vector3f DISPLAY_SCALE = new Vector3f(0.5f, 0.5f, 0.5f);

    public abstract List<T> getAvailableDisplays();

    public abstract String getRevealMessage(T item);

    @Override
    public void reveal(ActivePlayer player, T item, Runnable onComplete) {
        if (!player.isOnline()) return;

        List<D> displays = spawnDisplays(player, getAvailableDisplays(), item);

        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (tick <= DURATION_TICKS) {
                    float progress = (float) tick / DURATION_TICKS;
                    float rotation = (float) (ROTATION_SPEED * Math.pow(1 - progress, 2.5));

                    for (int index = 0; index < displays.size(); index++) {
                        displays.get(index).setTransformation(calculateDisplayTransformation(rotation, index, displays.size()));
                    }

                    if (tick < DURATION_TICKS) {
                        int wholeSpeed = (int) (DURATION_TICKS * 1.13) / (DURATION_TICKS - tick);

                        if (tick % wholeSpeed == 0) {
                            player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.UI_BUTTON_CLICK, 0.8f, 1.5f);
                        }
                    }
                } else if (tick == DURATION_TICKS + 10) {
                    displays.getFirst().setGlowing(true);
                    player.messageToActionBar(getRevealMessage(item));
                    player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.UI_BUTTON_CLICK, 0.8f, 1.5f);
                    player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.8f, 1f);
                }

                tick++;

                if (tick > DURATION_TICKS + 30) {
                    displays.forEach(D::remove);
                    cancel();
                    onComplete.run();
                }
            }
        }.runTaskTimer(getInstance(), 20L, 1L);
    }

    protected List<D> spawnDisplays(ActivePlayer player, List<T> availableDisplays, T initialDisplay) {
        if (!player.isOnline()) return List.of();

        List<T> displays = new ArrayList<>(List.of(initialDisplay));
        Location spawnLocation = player.getBukkitPlayer().getEyeLocation();

        while (displays.size() < DISPLAY_COUNT) {
            List<T> newDisplays = new ArrayList<>(availableDisplays);
            Collections.shuffle(newDisplays, ThreadLocalRandom.current());
            displays.addAll(newDisplays);
        }

        displays = displays.subList(0, DISPLAY_COUNT);

        List<D> spawnedDisplays = new ArrayList<>();

        for (T display : displays) {
            D spawnedDisplay = display.spawn(spawnLocation);
            player.getBukkitPlayer().showEntity(getInstance(), spawnedDisplay);
            spawnedDisplays.add(spawnedDisplay);
        }

        return spawnedDisplays;
    }

    protected Transformation calculateDisplayTransformation(float rotation, int index, int total) {
        float angle = (float) (rotation + index * (2 * Math.PI / total) - Math.PI / 2);
        float y = (float) (Math.cos(angle) * WHEEL_RADIUS);
        float z = (float) (Math.sin(angle) * WHEEL_RADIUS + WHEEL_OFFSET);

        Vector3f desiredCenter = new Vector3f(0, y, z);
        Quaternionf offsetRotation = new Quaternionf().rotateX(rotation);

        Vector3f halfExtent = new Vector3f(DISPLAY_SCALE).mul(0.5f);
        Vector3f rotatedHalf = offsetRotation.transform(new Vector3f(halfExtent));
        Vector3f translation = new Vector3f(desiredCenter).sub(rotatedHalf);

        return new Transformation(translation, offsetRotation, DISPLAY_SCALE, new Quaternionf());
    }
}
