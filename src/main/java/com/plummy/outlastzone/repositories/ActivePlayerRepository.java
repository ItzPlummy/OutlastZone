package com.plummy.outlastzone.repositories;

import com.plummy.outlastzone.core.data.AbstractRepository;
import com.plummy.outlastzone.core.data.Loadable;
import com.plummy.outlastzone.players.ActivePlayer;
import com.plummy.outlastzone.players.ActivePlayerRole;
import com.plummy.outlastzone.players.DefaultActivePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

import static com.plummy.outlastzone.OutlastZone.getPersistentPlayers;

public class ActivePlayerRepository extends AbstractRepository<UUID, ActivePlayer> implements PlayerRepository<ActivePlayer>, Loadable {

    @Override
    public void load() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            add(new DefaultActivePlayer(getPersistentPlayers().get(player.getUniqueId()), ActivePlayerRole.PLAYER));
        }
    }

    public Collection<ActivePlayer> allParticipants() {
        return all().stream().filter(player -> player.getRole() == ActivePlayerRole.PLAYER).toList();
    }

    public Collection<ActivePlayer> allSpectators() {
        return all().stream().filter(player -> player.getRole() == ActivePlayerRole.SPECTATOR).toList();
    }

    public Collection<ActivePlayer> allOnlineParticipants() {
        return allOnline().stream().filter(player -> player.getRole() == ActivePlayerRole.PLAYER).toList();
    }

    public Collection<ActivePlayer> allOnlineSpectators() {
        return allOnline().stream().filter(player -> player.getRole() == ActivePlayerRole.SPECTATOR).toList();
    }

    public int participantsCount() {
        return allParticipants().size();
    }

    public int onlineParticipantsCount() {
        return allOnlineParticipants().size();
    }
}
