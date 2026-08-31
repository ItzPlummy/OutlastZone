package com.plummy.outlastzone.core.visual.displays;

import com.plummy.outlastzone.core.players.ActivePlayer;
import org.bukkit.entity.Display;

public interface DisplayWheel<T extends Displayable<D>, D extends Display> {

    void reveal(ActivePlayer player, T item, Runnable onComplete);
}
