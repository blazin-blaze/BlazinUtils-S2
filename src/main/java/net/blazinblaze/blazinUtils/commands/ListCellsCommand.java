package net.blazinblaze.blazinUtils.commands;

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

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static net.blazinblaze.blazinUtils.BlazinUtils.*;

public class ListCellsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(jailLocations != null && !jailLocations.isEmpty()) {
            final Component component1 = Component.text("--List of Jail Cells--").decorate(TextDecoration.BOLD).decorate(TextDecoration.UNDERLINED)
                    .color(TextColor.color(6579300));
            sender.sendMessage(component1);
            AtomicInteger numCells = new AtomicInteger(0);
            for(Location location : jailLocations) {
                String worldName = location.getWorld().getName();
                int x = location.getBlockX();
                int y = location.getBlockY();
                int z = location.getBlockZ();
                int num = numCells.getAndIncrement();
                String withoutPlayer = String.format("#%s: (%s) %s, %s, %s", num, worldName, x, y, z);
                if(jailAssigned != null && !jailAssigned.isEmpty()) {
                    String string = null;
                    for(String stringi : jailAssigned.keySet()) {
                        if(jailAssigned.get(stringi).equals(location)) {
                            string = stringi;
                        }
                    }
                    if(string != null) {
                        UUID uuid = UUID.fromString(string);
                        if(uuid != null && Bukkit.getPlayer(uuid) != null) {
                            Player player = Bukkit.getPlayer(uuid);
                            String playerName = player.getName();
                            final Component component = Component.text(String.format("%s - Player Assigned: %s", withoutPlayer, playerName))
                                    .color(TextColor.color(35775));
                            sender.sendMessage(component);
                        }else {
                            final Component component = Component.text(withoutPlayer)
                                    .color(TextColor.color(35775));
                            sender.sendMessage(component);
                        }
                    }else {
                        final Component component = Component.text(withoutPlayer)
                                .color(TextColor.color(26559));
                        sender.sendMessage(component);
                    }
                }else {
                    final Component component = Component.text(withoutPlayer)
                            .color(TextColor.color(26559));
                    sender.sendMessage(component);
                }
            }
            final Component component2 = Component.text("--End of Cell List--").decorate(TextDecoration.BOLD)
                    .color(TextColor.color(6579300));
            sender.sendMessage(component2);
        }else {
            final Component component1 = Component.text("Could not find any normal cell locations.").decorate(TextDecoration.BOLD)
                    .color(TextColor.color(14430499));
            sender.sendMessage(component1);
        }
        if(holdingCell != null) {
            String worldName = holdingCell.getWorld().getName();
            int x = holdingCell.getBlockX();
            int y = holdingCell.getBlockY();
            int z = holdingCell.getBlockZ();
            final Component component = Component.text(String.format("Holding Cell: (%s) %s, %s, %s", worldName, x, y, z)).decorate(TextDecoration.BOLD)
                    .color(TextColor.color(49855));
            sender.sendMessage(component);
        }else {
            final Component component1 = Component.text("No assigned holding cell.").decorate(TextDecoration.BOLD)
                    .color(TextColor.color(14430499));
            sender.sendMessage(component1);
        }
        return true;
    }
}
