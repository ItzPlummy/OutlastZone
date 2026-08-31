package com.plummy.outlastzone.core.visual.announcers;

import com.plummy.outlastzone.core.players.ActivePlayer;

public interface GameAnnouncer {

    void locationNotReady();

    void gameStopped();

    void gameOver(ActivePlayer winner);
}
