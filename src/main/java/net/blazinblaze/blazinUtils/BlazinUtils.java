package net.blazinblaze.blazinUtils;

import net.blazinblaze.blazinUtils.commands.*;
import net.blazinblaze.blazinUtils.data.PlayerMemory;
import net.blazinblaze.blazinUtils.utility.PlayerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

/*/TODO:
    - JAIL SYSTEM
    - Add timed jailed players (done)
    - Teleport to court command
    - TPA ACCESS
    - Add LuckPerms permissions code to TPA commands
    - Add TPA playtime exception so that playtime code doesn't override /removeTPAAccess
    - RESTRAINING ORDER COMMAND
    - MISC
    - Make certain commands usable from the console
    - Make certain commands usable when referencing offline players
    - COMMUNITY EVENTS (low priority)
    - Add command to create event
    - Add command to view events
    - Add webhook functionality that sends a webhook when the event is created and when it starts by using UNIX timestamp
 */

public final class BlazinUtils extends JavaPlugin {

    private static BlazinUtils instance;
    private static boolean purgeActive;
    private static boolean spawnEventActive;
    private static boolean isTimedPurgeActive;
    public static ArrayList<String> jailedPlayers;
    public static ArrayList<Location> jailLocations;
    //public static ConfigurationSection jailAssigned;
    public static Map<String, Object> jailAssigned;
    public static Map<String, Object> jailTimed;
    public static Location holdingCell;
    public static LuckPerms luckPermsAPI;
    public static Map<String, Object> playTime;
    public static ArrayList<String> shouldAbleTPA;

    private static final String webhookURL = "https://discord.com/api/webhooks/1378786444818120875/8kfXTdWIY6CO5lUqK6_E2TTAbi3c7G-Bug353pSK0SWkKBqMSzheru5tLDPwSOqxkfc_";

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        purgeActive = false;
        spawnEventActive = false;
        isTimedPurgeActive = false;

        getLogger().info("BlazinUtils is enabled!");
        getCommand("discord").setExecutor(new DiscordCommand());
        getCommand("youtube").setExecutor(new YouTubeCommand());
        getCommand("youtube2").setExecutor(new YouTube2Command());
        getCommand("twitch").setExecutor(new TwitchCommand());
        getCommand("servermap").setExecutor(new MapCommand());
        getCommand("serverip").setExecutor(new ServerIPCommand());
        getCommand("serverinfo").setExecutor(new InfoCommand());
        getCommand("servervc").setExecutor(new ServerVCCommand());
        getCommand("announcement").setExecutor(new AnnouncementCommand());
        getCommand("announcementchat").setExecutor(new ChatAnnouncementCommand());
        //getCommand("playtimeboard").setExecutor(new PlaytimeBoardCommand());
        //getCommand("playtimeuser").setExecutor(new PlaytimeUserCommand());
        getCommand("purgetoggle").setExecutor(new PurgeToggleCommand());
        getCommand("purgetimed").setExecutor(new PurgeTimedCommand());
        getCommand("tpspawn").setExecutor(new TPSpawnCommand());
        getCommand("tpreturn").setExecutor(new TPReturnCommand());
        getCommand("jail").setExecutor(new JailPlayerCommand());
        getCommand("jail").setTabCompleter(new PlayerTabCompleter());
        getCommand("addCell").setExecutor(new AddCellCommand());
        getCommand("unjail").setExecutor(new UnjailPlayerCommand());
        getCommand("unjail").setTabCompleter(new PlayerTabCompleter());
        getCommand("removeCell").setExecutor(new RemoveCellCommand());
        getCommand("listCells").setExecutor(new ListCellsCommand());
        getCommand("removeTPAAccess").setExecutor(new RemoveTPACommand());
        getCommand("addTPAAccess").setExecutor(new AddTPACommand());
        //getCommand("spawneventtoggle").setExecutor(new SpawnEventToggleCommand());

        //DiscordWebhook webhook = new DiscordWebhook(webhookURL);
        //webhook.addEmbed(new DiscordWebhook.EmbedObject().setTitle("Plugin connected").setDescription("Plugin connected to webhook successfully"));
        //try {
        //webhook.execute();
        //}
        //catch(java.io.IOException e) {
        //getLogger().severe(e.getStackTrace().toString());
        //}

        getServer().getPluginManager().registerEvents(new BlazinListener(), this);

        //new BukkitRunnable() {
        // @Override
        //public void run() {
        //List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

        //for(int i = 0; i < players.size(); i++) {
        //Player plr = players.get(i);
        //PlayerMemory memory = PlayerUtil.getPlayerMemory(plr);
        //memory.setSecondsPlayed(memory.getSecondsPlayed() + 1);
        //}
        //}
        //}.runTaskTimer(this, 0, 20);

        File f = new File(this.getDataPath() + "/jailed-players.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        if(cfg.get("jail.players") instanceof ArrayList list) {
            jailedPlayers = list;
        }else {
            jailedPlayers = new ArrayList<>();
        }
        if(cfg.get("jail.locations") instanceof ArrayList list) {
            jailLocations = list;
        }else {
            jailLocations = new ArrayList<>();
        }
        if(cfg.get("jail.assigned") instanceof ConfigurationSection map) {
            //getLogger().info("FOUND");
            jailAssigned = map.getValues(true);
        }else {
            jailAssigned = new HashMap<>();
            /*/Map<String, Location> temp = new HashMap<>();
            cfg.set("jail.assigned", Objects.requireNonNull(temp));
            try {
                cfg.save(f);
                if(cfg.get("jail.assigned") instanceof ConfigurationSection map) {
                    jailAssigned = map;
                }else {
                    throw new IOException();
                }
            }catch (IOException eo) {
                jailAssigned = null;
            }/*/
        }
        //getLogger().info(jailAssigned.toString());
        if(cfg.get("jail.holdingCell") instanceof Location location) {
            holdingCell = location;
        }else {
            holdingCell = null;
        }

        if(cfg.get("jail.timed") instanceof ConfigurationSection map) {
            jailTimed = map.getValues(true);
        }else {
            jailTimed = new HashMap<>();
        }

        File f2 = new File(this.getDataPath() + "/players-playtime.yml");
        FileConfiguration cfg2 = YamlConfiguration.loadConfiguration(f2);
        if(cfg2.get("players.playtime") instanceof ConfigurationSection map) {
            playTime = map.getValues(true);
        }else {
            playTime = new HashMap<>();
        }
        if(cfg2.get("players.tpaEnabled") instanceof ArrayList list) {
            shouldAbleTPA = list;
        }else {
            shouldAbleTPA = new ArrayList<>();
        }

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPermsAPI = provider.getProvider();
            if(jailedPlayers != null) {
                for(String str : jailedPlayers) {
                    UUID uuid = UUID.fromString(str);
                    Player player = Bukkit.getPlayer(uuid);
                    if(player != null) {
                        if(!player.hasPermission("group.jailed")) {
                            Group group = luckPermsAPI.getGroupManager().getGroup("jailed");
                            if(group != null) {
                                luckPermsAPI.getUserManager().modifyUser(uuid, user -> {
                                    // Remove all other inherited groups the user had before.
                                    user.data().clear(NodeType.INHERITANCE::matches);

                                    // Create a node to add to the player.
                                    Node node = InheritanceNode.builder(group).build();

                                    // Add the node to the user.
                                    user.data().add(node);
                                });
                            }
                        }
                    }
                }
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

                for(int i = 0; i < players.size(); i++) {
                    Player plr = players.get(i);
                    PlayerMemory memory = PlayerUtil.getPlayerMemory(plr);
                    File f = new File(PlayerUtil.getFolderPath(plr) + "/general.yml");
                    FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                    //cfg.set("data.team", memory.getTeam());
                    //cfg.set("data.secondsPlayed", memory.getSecondsPlayed());
                    cfg.set("data.locationTP", memory.getLastLocationBeforeTP());

                    try{cfg.save(f);} catch (IOException eo){eo.printStackTrace();}
                }
            }
        }.runTaskTimer(this, 20*300, 20*300);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

        for(int i = 0; i < players.size(); i++) {
            Player plr = players.get(i);
            PlayerMemory memory = PlayerUtil.getPlayerMemory(plr);
            File f = new File(PlayerUtil.getFolderPath(plr) + "/general.yml");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            //cfg.set("data.team", memory.getTeam());
            //cfg.set("data.secondsPlayed", memory.getSecondsPlayed());
            cfg.set("data.locationTP", memory.getLastLocationBeforeTP());

            try{cfg.save(f);} catch (IOException eo){eo.printStackTrace();}
            PlayerUtil.setPlayerMemory(plr, null);
        }

        File f = new File(this.getDataPath() + "/jailed-players.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        cfg.set("jail.players", Objects.requireNonNull(jailedPlayers));
        cfg.set("jail.locations", Objects.requireNonNull(jailLocations));
        /*/if(jailAssigned != null)  {
            cfg.set("jail.assigned", Objects.requireNonNull(jailAssigned));
        }else {
            cfg.set("jail.assigned", new HashMap<>());
        }/*/
        cfg.set("jail.assigned", Objects.requireNonNull(jailAssigned));
        cfg.set("jail.holdingCell", holdingCell);
        cfg.set("jail.timed", Objects.requireNonNull(jailTimed));
        try{cfg.save(f);} catch (IOException eo){eo.printStackTrace();}

        File f2 = new File(this.getDataPath() + "/players-playtime.yml");
        FileConfiguration cfg2 = YamlConfiguration.loadConfiguration(f2);
        cfg2.set("players.playtime", Objects.requireNonNull(playTime));
        cfg2.set("players.tpaEnabled", Objects.requireNonNull(shouldAbleTPA));
        try{cfg2.save(f2);} catch (IOException eo){eo.printStackTrace();}

        //DiscordWebhook webhook = new DiscordWebhook(webhookURL);
        //webhook.addEmbed(new DiscordWebhook.EmbedObject().setTitle("Plugin disconnected").setDescription("Plugin disconnected from webhook"));
        //try {
        //webhook.execute();
        //}
        //catch(java.io.IOException e) {
        //getLogger().severe(e.getStackTrace().toString());
        //}

        instance = null;
        purgeActive = false;
        isTimedPurgeActive = false;
        spawnEventActive = false;

        getLogger().info("BlazinUtils is disabled!");
    }

    public static BlazinUtils getInstance() {
        return instance;
    }

    public static boolean isPurgeActive() {
        return purgeActive;
    }

    public static void setPurgeActive(boolean value) {
        purgeActive = value;
    }

    public static boolean isSpawnEventActive() {
        return spawnEventActive;
    }

    public static void setSpawnEventActive(boolean value) {
        spawnEventActive = value;
    }

    public static boolean isTimedPurgeActive() {
        return isTimedPurgeActive;
    }

    public static void setTimedPurgeActive(boolean value) {
        isTimedPurgeActive = value;
    }
}
