package net.blazinblaze.blazinUtils.commands;

import net.blazinblaze.blazinUtils.BlazinUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

import static net.blazinblaze.blazinUtils.BlazinUtils.*;

public class JailPlayerCommand implements CommandExecutor {

    private void checkHoldingCellOrDefault(Player player) {
        if(holdingCell != null) {
            player.teleport(holdingCell);
        }else {
            player.teleport(BlazinUtils.getInstance().getServer().getWorld("world").getSpawnLocation());
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(args.length >= 1) {
            String playerName = args[0];
            Object tmpPlayer = Bukkit.getPlayer(playerName);
            if(tmpPlayer == null) {
                tmpPlayer = Bukkit.getOfflinePlayer(playerName);
            }
            if(!((OfflinePlayer)tmpPlayer).hasPlayedBefore()) {
                if(sender instanceof Player) {
                    final Component component = Component.text("Player has not played before; you might have mistyped their name.").decorate(TextDecoration.BOLD)
                            .color(TextColor.color(12873006));
                    sender.sendMessage(component);
                }else {
                    Bukkit.getLogger().warning("Player has not played before; you might have mistyped their name.");
                }
            }
            OfflinePlayer player = (OfflinePlayer)tmpPlayer;
            if(jailedPlayers != null) {
                if(!jailedPlayers.contains(player.getUniqueId().toString())) {
                    jailedPlayers.add(player.getUniqueId().toString());
                    if(player instanceof Player plr) {
                        plr.setGameMode(GameMode.ADVENTURE);
                    }
                    File f = new File(BlazinUtils.getInstance().getDataPath() + "/jailed-players.yml");
                    FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                    cfg.set("jail.players", Objects.requireNonNull(jailedPlayers));
                    if(jailLocations != null && !jailLocations.isEmpty()) {
                        Location jailLocation = null;
                        for(Location possibleLocation : jailLocations) {
                            if(jailAssigned != null) {
                                if(!jailAssigned.containsValue(possibleLocation)) {
                                    jailLocation = possibleLocation;
                                }
                            }
                        }
                        if(jailLocation != null) {
                            jailAssigned.put(player.getUniqueId().toString(), jailLocation);
                            cfg.set("jail.assigned", Objects.requireNonNull(jailAssigned));
                            if(player instanceof Player plr) {
                                plr.teleport(jailLocation);
                            }
                            /*/}else if(jailAssigned == null && !jailLocations.isEmpty()) {
                                Map<String, Location> temp = new HashMap<>();
                                temp.put(player.getUniqueId().toString(), jailLocations.getFirst());
                                cfg.set("jail.assigned", Objects.requireNonNull(temp));
                                try {
                                    cfg.save(f);
                                    if(cfg.get("jail.assigned") instanceof ConfigurationSection map) {
                                        jailAssigned = map;
                                    }else {
                                        throw new IOException();
                                    }
                                }catch (IOException eo) {
                                    final Component component = Component.text("Unable to jail player. Aborting.").decorate(TextDecoration.BOLD)
                                            .color(TextColor.color(14430499));
                                    player.sendMessage(component);
                                    shouldAbort = true;
                                }/*/
                        }else {
                            if(player instanceof Player plr) {
                                checkHoldingCellOrDefault(plr);
                            }
                        }
                    }else {
                        if(player instanceof Player plr) {
                            checkHoldingCellOrDefault(plr);
                        }
                    }
                    final Component component = Component.text("You have been jailed!").decorate(TextDecoration.BOLD)
                            .color(TextColor.color(14430499));
                    if(player instanceof Player plr) {
                        plr.sendMessage(component);
                    }
                    if(luckPermsAPI != null) {
                        Group group = luckPermsAPI.getGroupManager().getGroup("jailed");
                        if(group != null) {
                            luckPermsAPI.getUserManager().modifyUser(player.getUniqueId(), user -> {
                                // Remove all other inherited groups the user had before.
                                user.data().clear(NodeType.INHERITANCE::matches);

                                // Create a node to add to the player.
                                Node node = InheritanceNode.builder(group).build();

                                // Add the node to the user.
                                user.data().add(node);

                                luckPermsAPI.getUserManager().saveUser(user);
                            });
                        }else {
                            if(sender instanceof Player) {
                                final Component component1 = Component.text("Player could not be added to jailed LuckPerms group").decorate(TextDecoration.BOLD)
                                        .color(TextColor.color(14430499));
                                sender.sendMessage(component1);
                            }else {
                                Bukkit.getLogger().warning("Player could not be added to jailed LuckPerms group");
                            }
                        }
                    }else {
                        try {
                            LuckPerms api = LuckPermsProvider.get();
                            Group group = api.getGroupManager().getGroup("jailed");
                            if(group != null) {
                                api.getUserManager().modifyUser(player.getUniqueId(), user -> {
                                    // Remove all other inherited groups the user had before.
                                    user.data().clear(NodeType.INHERITANCE::matches);

                                    // Create a node to add to the player.
                                    Node node = InheritanceNode.builder(group).build();

                                    // Add the node to the user.
                                    user.data().add(node);
                                });
                            }else {
                                if(sender instanceof Player) {
                                    final Component component1 = Component.text("Player could not be added to jailed LuckPerms group").decorate(TextDecoration.BOLD)
                                            .color(TextColor.color(14430499));
                                    sender.sendMessage(component1);
                                }else {
                                    Bukkit.getLogger().warning("Player could not be added to jailed LuckPerms group");
                                }
                            }
                        }catch(Exception e) {
                            e.printStackTrace();
                            if(sender instanceof Player) {
                                final Component component1 = Component.text("Player could not be added to jailed LuckPerms group").decorate(TextDecoration.BOLD)
                                        .color(TextColor.color(14430499));
                                sender.sendMessage(component1);
                            }else {
                                Bukkit.getLogger().warning("Player could not be added to jailed LuckPerms group");
                            }
                        }
                    }
                    if(args.length >= 2) {
                        if(jailTimed != null && !jailTimed.containsKey(player.getUniqueId().toString())) {
                            String time = args[1];
                            int stLength = time.length();
                            if(stLength > 0) {
                                try {
                                    int timeLength = Integer.parseInt(time.substring(0, time.length() - 1));
                                    char timeType = time.charAt(stLength - 1);
                                    long currentTime = Instant.now().getEpochSecond();
                                    if(timeType == 's')  {
                                        jailTimed.put(player.getUniqueId().toString(), currentTime + timeLength);
                                        cfg.set("jail.timed", Objects.requireNonNull(jailAssigned));
                                    }else if(timeType == 'm') {
                                        jailTimed.put(player.getUniqueId().toString(), currentTime + timeLength*60L);
                                        cfg.set("jail.timed", Objects.requireNonNull(jailAssigned));
                                    }else if(timeType == 'h') {
                                        jailTimed.put(player.getUniqueId().toString(), currentTime + timeLength*60L*60L);
                                        cfg.set("jail.timed", Objects.requireNonNull(jailAssigned));
                                    }else if(timeType == 'd') {
                                        jailTimed.put(player.getUniqueId().toString(), currentTime + timeLength*60L*60L*24L);
                                        cfg.set("jail.timed", Objects.requireNonNull(jailAssigned));
                                    }else {
                                        if(sender instanceof Player) {
                                            final Component componentTimeErr = Component.text("Time jail failed - resorting to permanent jail - did not specify a proper time type (s,m,h,d)").decorate(TextDecoration.BOLD)
                                                    .color(TextColor.color(14430499));
                                            sender.sendMessage(componentTimeErr);
                                        }else {
                                            Bukkit.getLogger().warning("Time jail failed - resorting to permanent jail - did not specify a proper time type (s,m,h,d)");
                                        }
                                    }
                                }catch(NumberFormatException e) {
                                    if(sender instanceof Player) {
                                        final Component componentTimeErr = Component.text("Time jail failed - resorting to permanent jail - please specify a number of time, with one letter at the end to specify the type").decorate(TextDecoration.BOLD)
                                                .color(TextColor.color(14430499));
                                        sender.sendMessage(componentTimeErr);
                                    }else {
                                        Bukkit.getLogger().warning("Time jail failed - resorting to permanent jail - please specify a number of time, with one letter at the end to specify the type");
                                    }
                                }
                            }else {
                                if(sender instanceof Player) {
                                    final Component componentTimeErr = Component.text("Time jail failed - resorting to permanent jail - please specify a time type (s,m,h,d)").decorate(TextDecoration.BOLD)
                                            .color(TextColor.color(14430499));
                                    sender.sendMessage(componentTimeErr);
                                }else {
                                    Bukkit.getLogger().warning("Time jail failed - resorting to permanent jail - please specify a time type (s,m,h,d)");
                                }
                            }
                        }else {
                            if(sender instanceof Player) {
                                final Component componentTimeErr = Component.text("Time jail failed - resorting to permanent jail - timed list could not be found / player already in list").decorate(TextDecoration.BOLD)
                                        .color(TextColor.color(14430499));
                                sender.sendMessage(componentTimeErr);
                            }else {
                                Bukkit.getLogger().warning("Time jail failed - resorting to permanent jail - timed list could not be found / player already in list");
                            }
                        }
                    }
                    try{
                        cfg.save(f);
                        if(sender instanceof Player) {
                            final Component component1 = Component.text("Player jailed!").decorate(TextDecoration.BOLD)
                                    .color(TextColor.color(3134257));
                            sender.sendMessage(component1);
                        }else {
                            Bukkit.getLogger().info("Player jailed!");
                        }
                    } catch (IOException eo) {
                        eo.printStackTrace();
                        jailedPlayers.remove(player.getUniqueId().toString());
                        if(player instanceof Player plr) {
                            plr.teleport(new Location(Bukkit.getWorld("world"), 1406, 63, -527));
                            plr.setGameMode(GameMode.SURVIVAL);
                        }
                        if(sender instanceof Player) {
                            final Component component1 = Component.text("Jail failed - Failed to save the player to the jailed list.").decorate(TextDecoration.BOLD)
                                    .color(TextColor.color(14430499));
                            sender.sendMessage(component1);
                        }else {
                            Bukkit.getLogger().warning("Jail failed - Failed to save the player to the jailed list.");
                        }
                    }
                }else {
                    if(sender instanceof Player) {
                        final Component component = Component.text("Jail failed - Player has already been jailed.").decorate(TextDecoration.BOLD)
                                .color(TextColor.color(14430499));
                        sender.sendMessage(component);
                    }else {
                        Bukkit.getLogger().warning("Jail failed - Player has already been jailed.");
                    }
                }
            }else {
                if(sender instanceof Player) {
                    final Component component = Component.text("Jail failed - Could not find the jailed list.").decorate(TextDecoration.BOLD)
                            .color(TextColor.color(14430499));
                    sender.sendMessage(component);
                }else {
                    Bukkit.getLogger().warning("Jail failed - Could not find the jailed list.");
                }
            }
        }else {
            if(sender instanceof Player) {
                final Component component = Component.text("Jail failed - You must specify a player.").decorate(TextDecoration.BOLD)
                        .color(TextColor.color(14430499));
                sender.sendMessage(component);
            }else {
                Bukkit.getLogger().warning("Jail failed - You must specify a player.");
            }
        }
        return true;
    }
}
