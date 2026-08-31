package com.plummy.outlastzone.visual.displays;

import com.plummy.outlastzone.players.ActivePlayer;
import org.bukkit.entity.Display;

public interface DisplayWheel<T extends Displayable<D>, D extends Display> {

    void reveal(ActivePlayer player, T item, Runnable onComplete);
}
