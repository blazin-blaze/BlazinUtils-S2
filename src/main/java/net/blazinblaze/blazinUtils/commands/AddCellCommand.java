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
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static net.blazinblaze.blazinUtils.BlazinUtils.*;

public class AddCellCommand implements CommandExecutor {

    private void addNormalCell(CommandSender sender) {
        Player player = Bukkit.getPlayer(sender.getName());
        if(player != null) {
            if(jailLocations != null) {
                if(!jailLocations.contains(player.getLocation())) {
                    jailLocations.add(player.getLocation());
                    File f = new File(BlazinUtils.getInstance().getDataPath() + "/jailed-players.yml");
                    FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                    cfg.set("jail.locations", Objects.requireNonNull(jailLocations));
                    try{
                        cfg.save(f);
                        final Component component1 = Component.text("Cell location created!").decorate(TextDecoration.BOLD)
                                .color(TextColor.color(3134257));
                        sender.sendMessage(component1);
                    } catch (IOException eo) {
                        eo.printStackTrace();
                        final Component component1 = Component.text("Failed to add a normal cell location.").decorate(TextDecoration.BOLD)
                                .color(TextColor.color(14430499));
                        sender.sendMessage(component1);
                    }
                }else {
                    final Component component1 = Component.text("Cell location already exists!").decorate(TextDecoration.BOLD)
                            .color(TextColor.color(14430499));
                    sender.sendMessage(component1);
                }
            }else {
                final Component component1 = Component.text("Failed to add a normal cell location.").decorate(TextDecoration.BOLD)
                        .color(TextColor.color(14430499));
                sender.sendMessage(component1);
            }
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(sender instanceof Player) {
            if(args.length == 1) {
                if(Boolean.parseBoolean(args[0])) {
                    Player player = Bukkit.getPlayer(sender.getName());
                    if(player != null) {
                        holdingCell = player.getLocation();
                        File f = new File(BlazinUtils.getInstance().getDataPath() + "/jailed-players.yml");
                        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                        cfg.set("jail.holdingCell", Objects.requireNonNull(holdingCell));
                        try{
                            cfg.save(f);
                            final Component component1 = Component.text("Holding cell location created!").decorate(TextDecoration.BOLD)
                                    .color(TextColor.color(3134257));
                            sender.sendMessage(component1);
                        } catch (IOException eo) {
                            eo.printStackTrace();
                            final Component component1 = Component.text("Failed to add holding cell location.").decorate(TextDecoration.BOLD)
                                    .color(TextColor.color(14430499));
                            sender.sendMessage(component1);
                        }
                    }
                }else {
                    addNormalCell(sender);
                }
            }else {
                addNormalCell(sender);
            }
        }else {
            BlazinUtils.getInstance().getLogger().warning("This command must be run by a player.");
        }
        return true;
    }
}
