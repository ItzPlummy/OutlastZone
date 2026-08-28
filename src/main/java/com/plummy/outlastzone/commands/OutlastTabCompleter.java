package com.plummy.outlastzone.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.plummy.outlastzone.OutlastZone.getTerrains;

public class OutlastTabCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return List.of(
                    "start",
                    "stop",
                    "reload",
                    "help"
            );
        }

        switch (args[0]) {
            case "start" -> {
                if (args.length == 2) {
                    List<String> terrains = new ArrayList<>(List.of("*"));
                    getTerrains().getAllTerrains().forEach(terrain -> terrains.add(terrain.getKey()));
                    return terrains;
                }
            }
        }

        return null;
    }
}
