package com.plummy.outlastzone.commands;

import com.plummy.outlastzone.core.games.Game;
import com.plummy.outlastzone.core.games.GameFinishReason;
import com.plummy.outlastzone.core.keyed.Terrain;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.plummy.outlastzone.OutlastZone.*;

public class OutlastCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            help(sender);
            return true;
        }

        switch (args[0]) {
            case "start" -> start(sender, args);
            case "help" -> help(sender);
            case "reload" -> reload(sender);
            case "stop" -> stop(sender);
            default -> sender.sendMessage("§cInvalid keyword: " + args[0] + ". Usage: /outlast <start | help>");
        }

        return true;
    }

    protected void start(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can start the game");
            return;
        }

        Game game = getGameManager().getGame();

        if (game != null && !game.hasFinished()) {
            sender.sendMessage("§cThe game is already in progress");
            return;
        }

        if (Bukkit.getOnlinePlayers().size() < getSettings().getMinPlayers()) {
            sender.sendMessage("§cNot enough players are online to start the game");
            return;
        }

        Terrain terrain = args.length > 1 ? getTerrains().get(args[1]) : null;

        getGameManager().startGame(player, terrain);
    }

    protected void stop(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can stop the game");
            return;
        }

        Game game = getGameManager().getGame();

        if (game == null || game.hasFinished()) {
            sender.sendMessage("§cThe game has already finished");
            return;
        }

        getGameManager().finishGame(GameFinishReason.STOP_COMMAND_EXECUTED);
    }

    protected void reload(CommandSender sender) {
        getInstance().reloadConfig();
        getSettings().reload(getInstance().getConfig());
        getTerrains().load();
    }

    protected void help(CommandSender sender) {

    }
}
