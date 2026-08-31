package com.plummy.outlastzone.core.players;

public interface PersistentPlayer extends BasePlayer {

    int getGamesPlayed();

    void setGamesPlayed(int gamesPlayed);

    int getWins();

    void setWins(int wins);

    int getLosses();

    void setLosses(int losses);

    int getKills();

    void setKills(int kills);

    int getDeaths();

    void setDeaths(int deaths);
}
