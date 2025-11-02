package net.blazinblaze.blazinUtils.commands;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import net.blazinblaze.blazinUtils.BlazinUtils;
import net.blazinblaze.blazinUtils.data.PlayerMemory;
import net.blazinblaze.blazinUtils.utility.PlayerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static net.blazinblaze.blazinUtils.BlazinUtils.*;

public class BlazinListener implements Listener {

    private int tickCount = 0;

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {

        Player player = e.getPlayer();

        PlayerMemory memory = PlayerUtil.getPlayerMemory(player);
        File f = new File(PlayerUtil.getFolderPath(player) + "/general.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        //cfg.set("data.team", memory.getTeam());
        //cfg.set("data.secondsPlayed", memory.getSecondsPlayed());
        cfg.set("data.locationTP", memory.getLastLocationBeforeTP());

        try{cfg.save(f);} catch (IOException eo){eo.printStackTrace();}
        PlayerUtil.setPlayerMemory(player, null);

        //e.quitMessage(player.displayName().color(TextColor.color(4862243)).append(Component.text("'s flame has dwindled out.").color(TextColor.color(4862243))));

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player player = e.getPlayer();

        //player.teleport(new Location(player.getWorld(), 0, -43, 0));
        //player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 10.0F, 1.0F);
        //player.setRotation(-90, 0);

        PlayerMemory memory = new PlayerMemory();
        File f = new File(PlayerUtil.getFolderPath(player) + "/general.yml");

        if(f.exists()) {
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            //memory.setTeam(cfg.getString("data.team"));
            //memory.setSecondsPlayed(cfg.getInt("data.secondsPlayed"));
            memory.setLastLocationBeforeTP(cfg.getLocation("data.locationTP"));
        }else {
            //memory.setTeam("none");
            //memory.setSecondsPlayed(0);
            BlazinUtils instance = BlazinUtils.getInstance();
            if(instance != null) {
                memory.setLastLocationBeforeTP(new Location(instance.getServer().getWorlds().getFirst(), 0, 0, 0));
            }
        }

        PlayerUtil.setPlayerMemory(player, memory);

        if(shouldAbleTPA != null) {
            if(shouldAbleTPA.contains(player.getUniqueId().toString())) {
                try {
                    if(!player.hasPermission("group.jailed") && !player.hasPermission("essentials.tpa")) {
                        if(luckPermsAPI != null) {
                            luckPermsAPI.getUserManager().modifyUser(player.getUniqueId(), user -> {
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
                }catch(Exception ex) {
                    ex.printStackTrace();
                }
            }else {
                try {
                    if(player.hasPermission("essentials.tpa")) {
                        if(luckPermsAPI != null) {
                            luckPermsAPI.getUserManager().modifyUser(player.getUniqueId(), user -> {
                                // Create a node to add to the player.
                                Node node = Node.builder("essentials.tpa").build();
                                Node node2 = Node.builder("essentials.tpaall").build();
                                Node node3 = Node.builder("essentials.tpacancel").build();
                                Node node4 = Node.builder("essentials.tpaccept").build();
                                Node node5 = Node.builder("essentials.tpahere").build();
                                Node node6 = Node.builder("essentials.tpdeny").build();
                                Node node7 = Node.builder("essentials.tptoggle").build();

                                // Add the node to the user.
                                user.data().remove(node);
                                user.data().remove(node2);
                                user.data().remove(node3);
                                user.data().remove(node4);
                                user.data().remove(node5);
                                user.data().remove(node6);
                                user.data().remove(node7);
                            });
                            if(BlazinUtils.getInstance() != null) {
                                File f2 = new File(BlazinUtils.getInstance().getDataFolder() + "/players-playtime.yml");
                                FileConfiguration cfg2 = YamlConfiguration.loadConfiguration(f2);
                                cfg2.set("players.tpaEnabled", Objects.requireNonNull(shouldAbleTPA));
                                try{cfg2.save(f2);} catch (IOException eo){eo.printStackTrace();}
                            }
                        }
                    }
                }catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        if(jailedPlayers != null && jailedPlayers.contains(e.getPlayer().getUniqueId().toString())) {
            if(e.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
                e.getPlayer().setGameMode(GameMode.ADVENTURE);
            }
        }else {
            if(e.getPlayer().getGameMode().equals(GameMode.ADVENTURE)) {
                e.getPlayer().setGameMode(GameMode.SURVIVAL);
            }
        }

        //if(player.hasPlayedBefore()) {
            //final Component component = Component.text("Welcome back!").decorate(TextDecoration.BOLD)
                    //.color(TextColor.color(13657113))
                    //.append(Component.text(" Blazin!", NamedTextColor.RED));
            //player.sendMessage(component);
            //player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 10.0F, 1.0F);
        //}else {
            //final Component component = Component.text("Welcome to the Blazin MC Server!").decorate(TextDecoration.BOLD)
                    //.color(TextColor.color(13657113))
                    //.append(Component.text(" I hope you enjoy your stay!", NamedTextColor.RED));
            //player.sendMessage(component);
            //player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 10.0F, 1.0F);
            //SendMessageLater laterMessage = new SendMessageLater(player);
            //laterMessage.runTaskLater(BlazinUtils.getInstance(), 20L * 5);
        //}

        //e.joinMessage(player.displayName().color(TextColor.color(11350273)).append(Component.text("'s flame has ignited!")).color(TextColor.color(11350273)));

    }

    private void sendHoldingOrDefault(Player player) {
        if(holdingCell != null) {
            player.teleport(holdingCell);
            player.setRotation(-90, 0);
        }else {
            player.teleport(BlazinUtils.getInstance().getServer().getWorld("world").getSpawnLocation());
            player.setRotation(-90, 0);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        ArrayList<String> jailedPlayers = BlazinUtils.jailedPlayers;
        if(jailedPlayers != null) {
            for(String string : jailedPlayers) {
                UUID uuid = UUID.fromString(string);
                if(event.getPlayer().getUniqueId().equals(uuid)) {
                    if(courtLocation != null && shouldBeAtCourt != null && shouldBeAtCourt.contains(uuid)) {
                        final Component component = Component.text("You are not allowed to move, you are in court!").decorate(TextDecoration.BOLD)
                                .color(TextColor.color(14430499));
                        event.getPlayer().sendMessage(component);
                        event.getPlayer().teleport(courtLocation);
                        event.getPlayer().setRotation(0, 0);
                    }else {
                        final Component component = Component.text("You are not allowed to move, you are jailed!").decorate(TextDecoration.BOLD)
                                .color(TextColor.color(14430499));
                        event.getPlayer().sendMessage(component);
                        if(jailAssigned != null) {
                            if(jailAssigned.containsKey(string)) {
                                event.getPlayer().teleport((Location)jailAssigned.get(string));
                                event.getPlayer().setRotation(-90, 0);
                            }else {
                                sendHoldingOrDefault(event.getPlayer());
                            }
                        }else {
                            sendHoldingOrDefault(event.getPlayer());
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onTickEnd(ServerTickEndEvent event) {
        if(tickCount == 20) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                UUID uuid = player.getUniqueId();
                String str = uuid.toString();
                if(playTime != null && playTime.containsKey(str)) {
                    int oldPlaytime = (int)playTime.get(str);
                    playTime.put(str, oldPlaytime + 1);
                    if((int)playTime.get(str) >= 20*60*60*24) {
                        if(exemptedTPAPLayers != null && !exemptedTPAPLayers.contains(str)) {
                            if(shouldAbleTPA != null) {
                                if(!shouldAbleTPA.contains(str)) {
                                    shouldAbleTPA.add(str);
                                    if(!player.hasPermission("group.jailed") && !player.hasPermission("essentials.tpa")) {
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
                                }
                            }
                        }
                    }
                }
            }
            tickCount = 0;
        }else {
            tickCount++;
        }
        if(jailedPlayers != null && jailTimed != null) {
            for(String str : jailedPlayers) {
                if(jailTimed.containsKey(str)) {
                    long timeUntilUnJailed = (long)jailTimed.get(str);
                    if(timeUntilUnJailed <= Instant.now().getEpochSecond()) {
                        Object tmpPlayer = Bukkit.getPlayer(UUID.fromString(str));
                        if(tmpPlayer != null) {
                            tmpPlayer = Bukkit.getOfflinePlayer(UUID.fromString(str));
                        }
                        if(tmpPlayer instanceof OfflinePlayer player) {
                            removePlayerFromTimedJail(str, player);
                        }else {
                            Bukkit.getLogger().warning("Timed Player could not be found from their UUID.");
                        }
                    }
                }
            }
        }
    }

    private static void removePlayerFromTimedJail(String str, OfflinePlayer player) {
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
            Bukkit.getLogger().warning("Could not find assigned cell for timed jailed player.");
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
                Bukkit.getLogger().warning("Timed Player could not be removed from jailed LuckPerms group.");
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
                    Bukkit.getLogger().warning("Timed Player could not be removed from jailed LuckPerms group");
                }
            }catch(Exception e) {
                e.printStackTrace();
                Bukkit.getLogger().warning("Timed Player could not be removed from jailed LuckPerms group");
            }
        }
        if(jailTimed != null) {
            jailTimed.remove(player.getUniqueId().toString());
            cfg.set("jail.timed", Objects.requireNonNull(jailTimed));
        }
        if(player instanceof Player plr) {
            plr.setGameMode(GameMode.SURVIVAL);
            plr.teleport(new Location(Bukkit.getWorld("world"), 1406, 63, -527));
        }
        try{
            cfg.save(f);
            Bukkit.getLogger().info("Timed unjail successful for player with unique id " + str);
            if(player instanceof Player plr) {
                final Component component = Component.text("You have been unjailed!").decorate(TextDecoration.BOLD)
                        .color(TextColor.color(3134257));
                plr.sendMessage(component);
            }
        } catch (IOException eo) {
            eo.printStackTrace();
            jailedPlayers.add(player.getUniqueId().toString());
            jailAssigned.put(player.getUniqueId().toString(), prevLocation);
            jailTimed.put(player.getUniqueId().toString(), 0L);
            if(player instanceof Player plr) {
                plr.setGameMode(GameMode.ADVENTURE);
                plr.teleport(prevLocation);
            }
            Bukkit.getLogger().warning("Timed unjail failed for player with unique id " + str);
        }
    }

    private static class SendMessageLater extends BukkitRunnable {

        Player playerInstance;

        private SendMessageLater(Player player) {
            playerInstance = player;
        }

        @Override
        public void run() {
            if(playerInstance != null && playerInstance.isOnline()) {
                final Component component2 = Component.text("Make sure to check out the information board at spawn for rules, useful commands, and various features!").decorate(TextDecoration.BOLD)
                        .color(TextColor.color(8355711));
                playerInstance.sendMessage(component2);
                final Component component = Component.text("Proximity voice chat is available if you're interested! - ")
                        .decorate(TextDecoration.BOLD)
                        .color(TextColor.color(16777215))
                        .append(Component.text("Click here to install Simple Voice Chat (fabric/forge/etc)!", NamedTextColor.AQUA).decorate(TextDecoration.ITALIC).decorate(TextDecoration.UNDERLINED))
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL,"https://modrinth.com/plugin/simple-voice-chat"));
                playerInstance.sendMessage(component);
                playerInstance.playSound(playerInstance.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10.0F, 1.0F);
            }
        }
    }

}
