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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static net.blazinblaze.blazinUtils.BlazinUtils.*;

public class UnjailPlayerCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(args.length > 0) {
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
                if(jailedPlayers.contains(player.getUniqueId().toString())) {
                    for(String string : jailedPlayers) {
                        UUID uuid = UUID.fromString(string);
                        if(uuid.equals(player.getUniqueId())) {
                            jailedPlayers.remove(uuid.toString());
                            break;
                        }
                    }
                    File f = new File(BlazinUtils.getInstance().getDataPath() + "/jailed-players.yml");
                    FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                    cfg.set("jail.players", Objects.requireNonNull(jailedPlayers));
                    Location prevLocation = null;
                    if(jailAssigned != null && jailLocations != null) {
                        for(Location location : jailLocations) {
                            String string = null;
                            for(String stringi : jailAssigned.keySet()) {
                                if(jailAssigned.get(stringi).equals(location)) {
                                    string = stringi;
                                    prevLocation = location;
                                }
                            }
                            if(string != null) {
                                UUID uuid = UUID.fromString(string);
                                if(uuid != null && uuid.equals(player.getUniqueId())) {
                                    jailAssigned.remove(string);
                                    break;
                                }
                            }
                        }
                        cfg.set("jail.assigned", Objects.requireNonNull(jailAssigned));
                    }else {
                        if(sender instanceof Player) {
                            final Component component = Component.text("Could not find assigned cell for jailed player.").decorate(TextDecoration.BOLD)
                                    .color(TextColor.color(12873006));
                            sender.sendMessage(component);
                        }else {
                            Bukkit.getLogger().warning("Could not find assigned cell for jailed player.");
                        }
                        jailedPlayers.add(player.getUniqueId().toString());
                        return true;
                    }
                    if(luckPermsAPI != null) {
                        Group group = luckPermsAPI.getGroupManager().getGroup("default");
                        if(group != null) {
                            luckPermsAPI.getUserManager().modifyUser(player.getUniqueId(), user -> {
                                // Remove all other inherited groups the user had before.
                                user.data().clear(NodeType.INHERITANCE::matches);

                                // Create a node to add to the player.
                                Node node = InheritanceNode.builder(group).build();

                                // Add the node to the user.
                                user.data().add(node);
                            });
                        }else {
                            if(sender instanceof Player) {
                                final Component component1 = Component.text("Player could not be removed from jailed LuckPerms group").decorate(TextDecoration.BOLD)
                                        .color(TextColor.color(14430499));
                                sender.sendMessage(component1);
                            }else {
                                Bukkit.getLogger().warning("Player could not be removed from jailed LuckPerms group");
                            }
                        }
                    }else {
                        try {
                            LuckPerms api = LuckPermsProvider.get();
                            Group group = api.getGroupManager().getGroup("default");
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
                                    final Component component1 = Component.text("Player could not be removed from jailed LuckPerms group").decorate(TextDecoration.BOLD)
                                            .color(TextColor.color(14430499));
                                    sender.sendMessage(component1);
                                }else {
                                    Bukkit.getLogger().warning("Player could not be removed from jailed LuckPerms group");
                                }
                            }
                        }catch(Exception e) {
                            e.printStackTrace();
                            if(sender instanceof Player) {
                                final Component component1 = Component.text("Player could not be removed from jailed LuckPerms group").decorate(TextDecoration.BOLD)
                                        .color(TextColor.color(14430499));
                                sender.sendMessage(component1);
                            }else {
                                Bukkit.getLogger().warning("Player could not be removed from jailed LuckPerms group");
                            }
                        }
                    }
                    boolean wasTimed = false;
                    long prevTime = 0L;
                    if(jailTimed != null && jailTimed.containsKey(player.getUniqueId().toString()))  {
                        prevTime = (long)jailTimed.get(player.getUniqueId().toString());
                        jailTimed.remove(player.getUniqueId().toString());
                        wasTimed = true;
                        cfg.set("jail.timed", Objects.requireNonNull(jailTimed));
                    }
                    if(player instanceof Player plr) {
                        plr.setGameMode(GameMode.SURVIVAL);
                        plr.teleport(new Location(Bukkit.getWorld("world"), 1406, 63, -527));
                    }
                    try{
                        cfg.save(f);
                        if(sender instanceof Player) {
                            final Component component = Component.text("Unjail successful!").decorate(TextDecoration.BOLD)
                                    .color(TextColor.color(3134257));
                            sender.sendMessage(component);
                        }else {
                            Bukkit.getLogger().info("Unjail successful!");
                        }
                        if(player instanceof Player plr) {
                            final Component component2 = Component.text("You have been unjailed!").decorate(TextDecoration.BOLD)
                                    .color(TextColor.color(3134257));
                            plr.sendMessage(component2);
                        }
                    } catch (IOException eo) {
                        eo.printStackTrace();
                        jailedPlayers.add(player.getUniqueId().toString());
                        jailAssigned.put(player.getUniqueId().toString(), prevLocation);
                        if(player instanceof Player plr) {
                            plr.setGameMode(GameMode.ADVENTURE);
                            plr.teleport(prevLocation);
                        }
                        if(wasTimed) {
                            jailTimed.put(player.getUniqueId().toString(), prevTime);
                        }
                        if(sender instanceof Player) {
                            final Component component1 = Component.text("Unfail failed - Failed to save the removal of the player from the jailed list.").decorate(TextDecoration.BOLD)
                                    .color(TextColor.color(14430499));
                            sender.sendMessage(component1);
                        }else {
                            Bukkit.getLogger().warning("Unfail failed - Failed to save the removal of the player from the jailed list.");
                        }
                    }
                }else {
                    if(sender instanceof Player) {
                        final Component component = Component.text("Unjail failed - Specified player is not in jail.").decorate(TextDecoration.BOLD)
                                .color(TextColor.color(14430499));
                        sender.sendMessage(component);
                    }else {
                        Bukkit.getLogger().warning("Unjail failed - Specified player is not in jail.");
                    }
                }
            }else {
                if(sender instanceof Player) {
                    final Component component = Component.text("Unjail failed - Could not find the jailed list.").decorate(TextDecoration.BOLD)
                            .color(TextColor.color(14430499));
                    sender.sendMessage(component);
                }else {
                    Bukkit.getLogger().warning("Unjail failed - Could not find the jailed list.");
                }
            }
        }else {
            if(sender instanceof Player) {
                final Component component = Component.text("Unjail failed - You must specify a player.").decorate(TextDecoration.BOLD)
                        .color(TextColor.color(14430499));
                sender.sendMessage(component);
            }else {
                Bukkit.getLogger().warning("Unjail failed - You must specify a player.");
            }
        }
        return true;
    }
}
