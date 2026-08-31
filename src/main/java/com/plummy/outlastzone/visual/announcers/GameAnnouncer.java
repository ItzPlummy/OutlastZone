package com.plummy.outlastzone.visual.announcers;

import com.plummy.outlastzone.players.ActivePlayer;

public interface GameAnnouncer {

    void locationNotReady();

    void gameStopped();

    void gameOver(ActivePlayer winner);
}
