package net.blazinblaze.blazinUtils.commands;

import net.blazinblaze.blazinUtils.BlazinUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static net.blazinblaze.blazinUtils.BlazinUtils.*;

public class ToggleCourtCommand implements CommandExecutor {

    private void sendHoldingOrDefault(Player player) {
        if(holdingCell != null) {
            player.teleport(holdingCell);
            player.setRotation(-90, 0);
        }else {
            player.teleport(BlazinUtils.getInstance().getServer().getWorld("world").getSpawnLocation());
            player.setRotation(-90, 0);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(args.length > 0) {
            String playerName = args[0];
            Player player = Bukkit.getPlayer(playerName);
            if(player != null) {
                UUID uuid = player.getUniqueId();
                String str = uuid.toString();
                if(shouldBeAtCourt != null && courtLocation != null) {
                    if(shouldBeAtCourt.contains(uuid)) {
                        shouldBeAtCourt.remove(uuid);
                        if(jailAssigned != null) {
                            if(jailAssigned.containsKey(str)) {
                                player.teleport((Location)jailAssigned.get(str));
                                player.setRotation(0, 0);
                            }else {
                                sendHoldingOrDefault(player);
                            }
                        }else {
                            sendHoldingOrDefault(player);
                        }
                        final Component component = Component.text("You have been removed from court!").decorate(TextDecoration.BOLD)
                                .color(TextColor.color(49855));
                        player.sendMessage(component);
                    }else {
                        if(jailedPlayers != null && jailedPlayers.contains(str)) {
                            shouldBeAtCourt.add(uuid);
                            player.teleport(courtLocation);
                            final Component component = Component.text("You have been sent to court!").decorate(TextDecoration.BOLD)
                                    .color(TextColor.color(49855));
                            player.sendMessage(component);
                        }else {
                            final Component component = Component.text("Court toggle failed - Player must be jailed.").decorate(TextDecoration.BOLD)
                                    .color(TextColor.color(14430499));
                            sender.sendMessage(component);
                        }
                    }
                    final Component component = Component.text("Successfully toggled court teleport for: " + playerName).decorate(TextDecoration.BOLD)
                            .color(TextColor.color(3134257));
                    sender.sendMessage(component);
                }else {
                    final Component component = Component.text("Court toggle failed - Court list/location not found.").decorate(TextDecoration.BOLD)
                            .color(TextColor.color(14430499));
                    sender.sendMessage(component);
                }
            }else {
                final Component component = Component.text("Court toggle failed - Player not found. Are they online?").decorate(TextDecoration.BOLD)
                        .color(TextColor.color(14430499));
                sender.sendMessage(component);
            }
        }else {
            final Component component = Component.text("Court toggle failed - You must specify a player.").decorate(TextDecoration.BOLD)
                    .color(TextColor.color(14430499));
            sender.sendMessage(component);
        }
        return true;
    }
}
