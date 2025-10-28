package net.blazinblaze.blazinUtils.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static net.blazinblaze.blazinUtils.BlazinUtils.shouldAbleTPA;

public class RemoveTPACommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(args.length == 1) {
            String playerName = args[0];
            Object tmpPlayer = Bukkit.getPlayer(playerName);
            if (tmpPlayer != null) {
                tmpPlayer = Bukkit.getOfflinePlayer(playerName);
            }
            if (tmpPlayer instanceof OfflinePlayer player) {
                UUID uuid = player.getUniqueId();
                String str = uuid.toString();
                if(shouldAbleTPA != null) {
                    if(shouldAbleTPA.contains(str)) {
                        shouldAbleTPA.remove(str);
                        if(sender instanceof Player) {
                            final Component component = Component.text("Removed player from TPA list!").decorate(TextDecoration.BOLD)
                                    .color(TextColor.color(3134257));
                            sender.sendMessage(component);
                        }
                    }else {
                        if(sender instanceof Player) {
                            final Component component = Component.text("Removing TPA failed - Player is not in TPA list.").decorate(TextDecoration.BOLD)
                                    .color(TextColor.color(14430499));
                            sender.sendMessage(component);
                        }
                    }
                }else {
                    if(sender instanceof Player) {
                        final Component component = Component.text("Removing TPA failed - Could not find TPA list.").decorate(TextDecoration.BOLD)
                                .color(TextColor.color(14430499));
                        sender.sendMessage(component);
                    }
                }
            }else {
                if(sender instanceof Player) {
                    final Component component = Component.text("Removing TPA failed - Could not find specified player.").decorate(TextDecoration.BOLD)
                            .color(TextColor.color(14430499));
                    sender.sendMessage(component);
                }
            }
        }else {
            if(sender instanceof Player) {
                final Component component = Component.text("Removing TPA failed - You must specify a player.").decorate(TextDecoration.BOLD)
                        .color(TextColor.color(14430499));
                sender.sendMessage(component);
            }
        }
        return true;
    }
}
