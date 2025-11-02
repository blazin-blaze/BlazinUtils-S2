package net.blazinblaze.blazinUtils.commands;

import net.blazinblaze.blazinUtils.BlazinUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
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
import java.util.Objects;
import java.util.UUID;

import static net.blazinblaze.blazinUtils.BlazinUtils.*;

public class AddTPACommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(args.length > 0) {
            String playerName = args[0];
            Object tmpPlayer = Bukkit.getPlayer(playerName);
            if (tmpPlayer == null) {
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
            UUID uuid = player.getUniqueId();
            String str = uuid.toString();
            if(shouldAbleTPA != null && exemptedTPAPLayers != null) {
                if(!shouldAbleTPA.contains(str)) {
                    shouldAbleTPA.add(str);
                    boolean wasExempted = false;
                    if(exemptedTPAPLayers.contains(str)) {
                        wasExempted = true;
                    }
                    exemptedTPAPLayers.remove(str);
                    if(player instanceof Player plr) {
                        try {
                            if(!plr.hasPermission("group.jailed") && !plr.hasPermission("essentials.tpa")) {
                                if(luckPermsAPI != null) {
                                    luckPermsAPI.getUserManager().modifyUser(uuid, user -> {
                                        // Create a node to add to the player.
                                        Node node = Node.builder("essentials.tpa").build();
                                        Node node2 = Node.builder("essentials.tpaall").build();
                                        Node node3 = Node.builder("essentials.tpacancel").build();
                                        Node node4 = Node.builder("essentials.tpaccept").build();
                                        Node node5 = Node.builder("essentials.tpahere").build();
                                        Node node6 = Node.builder("essentials.tpdeny").build();
                                        Node node7 = Node.builder("essentials.tptoggle").build();

                                        // Add the node to the user.
                                        user.data().add(node);
                                        user.data().add(node2);
                                        user.data().add(node3);
                                        user.data().add(node4);
                                        user.data().add(node5);
                                        user.data().add(node6);
                                        user.data().add(node7);
                                    });
                                    if(BlazinUtils.getInstance() != null) {
                                        File f2 = new File(BlazinUtils.getInstance().getDataFolder() + "/players-playtime.yml");
                                        FileConfiguration cfg2 = YamlConfiguration.loadConfiguration(f2);
                                        cfg2.set("players.tpaEnabled", Objects.requireNonNull(shouldAbleTPA));
                                        try{cfg2.save(f2);} catch (IOException eo){eo.printStackTrace();}
                                    }
                                }
                            }
                        }catch(Exception e) {
                            e.printStackTrace();
                            if(sender instanceof Player) {
                                final Component component = Component.text("Could not add TPA perms to LuckPerms; will try when the person connects to the server.").decorate(TextDecoration.BOLD)
                                        .color(TextColor.color(14430499));
                                sender.sendMessage(component);
                            }else {
                                Bukkit.getLogger().warning("Could not add TPA perms to LuckPerms; will try when the person connects to the server.");
                            }
                        }
                    }else {
                        if(sender instanceof Player) {
                            final Component component = Component.text("Could not add TPA perms to LuckPerms; will try when the person connects to the server.").decorate(TextDecoration.BOLD)
                                    .color(TextColor.color(14430499));
                            sender.sendMessage(component);
                        }else {
                            Bukkit.getLogger().warning("Could not add TPA perms to LuckPerms; will try when the person connects to the server.");
                        }
                    }
                    File f = new File(BlazinUtils.getInstance().getDataPath() + "/players-playtime.yml");
                    FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                    cfg.set("players.tpaEnabled", Objects.requireNonNull(shouldAbleTPA));
                    cfg.set("players.tpaEnabled", Objects.requireNonNull(exemptedTPAPLayers));
                    try {
                        cfg.save(f);
                        if(sender instanceof Player) {
                            final Component component = Component.text("Added player to TPA list!").decorate(TextDecoration.BOLD)
                                    .color(TextColor.color(3134257));
                            sender.sendMessage(component);
                        }else {
                            Bukkit.getLogger().info("Added player to TPA list!");
                        }
                    } catch (IOException e) {
                        shouldAbleTPA.remove(str);
                        if(wasExempted) {
                            exemptedTPAPLayers.add(str);
                        }
                        if(sender instanceof Player) {
                            final Component component = Component.text("Adding TPA failed - Could not save to TPA list.").decorate(TextDecoration.BOLD)
                                    .color(TextColor.color(14430499));
                            sender.sendMessage(component);
                        }else {
                            Bukkit.getLogger().warning("Adding TPA failed - Could not save to TPA list.");
                        }
                    }
                }else {
                    if(sender instanceof Player) {
                        final Component component = Component.text("Adding TPA failed - Player is already in TPA list.").decorate(TextDecoration.BOLD)
                                .color(TextColor.color(14430499));
                        sender.sendMessage(component);
                    }else {
                        Bukkit.getLogger().warning("Adding TPA failed - Player is already in TPA list.");
                    }
                }
            }else {
                if(sender instanceof Player) {
                    final Component component = Component.text("Adding TPA failed - Could not find TPA list.").decorate(TextDecoration.BOLD)
                            .color(TextColor.color(14430499));
                    sender.sendMessage(component);
                }else {
                    Bukkit.getLogger().warning("Adding TPA failed - Could not find TPA list.");
                }
            }
        }else {
            if(sender instanceof Player) {
                final Component component = Component.text("Adding TPA failed - You must specify a player.").decorate(TextDecoration.BOLD)
                        .color(TextColor.color(14430499));
                sender.sendMessage(component);
            }else {
                Bukkit.getLogger().warning("Adding TPA failed - You must specify a player.");
            }
        }
        return true;
    }
}
