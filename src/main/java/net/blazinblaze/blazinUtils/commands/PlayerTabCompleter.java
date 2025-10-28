package net.blazinblaze.blazinUtils.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        // Only provide completions for the player argument (e.g., the first argument)
        if (args.length == 1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                String playerName = player.getName();
                // Add the player's name if it starts with what the player has typed
                if (playerName.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(playerName);
                }
            }
        }
        return completions;
    }
}
