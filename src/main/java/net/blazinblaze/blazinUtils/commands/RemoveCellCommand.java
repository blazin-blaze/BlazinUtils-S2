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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static net.blazinblaze.blazinUtils.BlazinUtils.*;

public class RemoveCellCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(args.length == 1) {
            try {
                int locationNumber = Integer.parseInt(args[0]);

                if(locationNumber >= 0) {
                    if(jailLocations != null) {
                        if(jailLocations.size() >= locationNumber) {
                            File f = new File(BlazinUtils.getInstance().getDataPath() + "/jailed-players.yml");
                            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                            Location location = jailLocations.get(locationNumber);
                            jailLocations.remove(locationNumber);
                            cfg.set("jail.locations", Objects.requireNonNull(jailLocations));

                            if(location != null) {
                                if(jailAssigned.containsValue(location)) {
                                    for(String string : jailAssigned.keySet()) {
                                        if(jailAssigned.get(string).equals(location)) {
                                            jailAssigned.remove(string);
                                        }
                                    }
                                }
                                //jailAssigned.remove(location);
                                cfg.set("jail.assigned", Objects.requireNonNull(jailAssigned));
                            }else {
                                final Component component1 = Component.text("Failed to remove cell assignment.").decorate(TextDecoration.BOLD)
                                        .color(TextColor.color(14430499));
                                sender.sendMessage(component1);
                            }

                            try {
                                cfg.save(f);
                                final Component component1 = Component.text("Cell location removed.").decorate(TextDecoration.BOLD)
                                        .color(TextColor.color(3134257));
                                sender.sendMessage(component1);
                            } catch (IOException eo) {
                                eo.printStackTrace();
                                final Component component1 = Component.text("Failed to remove cell location.").decorate(TextDecoration.BOLD)
                                        .color(TextColor.color(14430499));
                                sender.sendMessage(component1);
                            }
                        }else {
                            final Component component1 = Component.text("Failed to remove cell location.").decorate(TextDecoration.BOLD)
                                    .color(TextColor.color(14430499));
                            sender.sendMessage(component1);
                        }
                    }else {
                        final Component component1 = Component.text("Failed to remove cell location.").decorate(TextDecoration.BOLD)
                                .color(TextColor.color(14430499));
                        sender.sendMessage(component1);
                    }
                }else {
                    holdingCell = null;
                    File f = new File(BlazinUtils.getInstance().getDataPath() + "/jailed-players.yml");
                    FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                    cfg.set("jail.holdingCell", holdingCell);
                    try {
                        cfg.save(f);
                        final Component component1 = Component.text("Holding cell location removed.").decorate(TextDecoration.BOLD)
                                .color(TextColor.color(3134257));
                        sender.sendMessage(component1);
                    } catch (IOException eo) {
                        eo.printStackTrace();
                        final Component component1 = Component.text("Failed to remove holding cell location.").decorate(TextDecoration.BOLD)
                                .color(TextColor.color(14430499));
                        sender.sendMessage(component1);
                    }
                }
            }catch (NumberFormatException e) {
                final Component component1 = Component.text("Agrument provided is not a number!").decorate(TextDecoration.BOLD)
                        .color(TextColor.color(14430499));
                sender.sendMessage(component1);
            }
        }else {
            final Component component1 = Component.text("Agruments incorrect! Proivde the number of the location by using /listCells or -1 for the holding cell").decorate(TextDecoration.BOLD)
                    .color(TextColor.color(14430499));
            sender.sendMessage(component1);
        }
        return true;
    }
}
