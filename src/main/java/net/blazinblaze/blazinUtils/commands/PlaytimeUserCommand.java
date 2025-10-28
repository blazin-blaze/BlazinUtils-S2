package net.blazinblaze.blazinUtils.commands;

import net.blazinblaze.blazinUtils.data.PlayerMemory;
import net.blazinblaze.blazinUtils.utility.PlayerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PlaytimeUserCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if(strings.length == 1) {
            if (commandSender instanceof Player) {
                Player specifiedUser = null;

                List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                for(int i = 0; i < players.size(); i++) {
                    Player listPlr = players.get(i);
                    if(listPlr.displayName().equals(Component.text(strings[0]))) {
                        specifiedUser = listPlr;
                    }
                }

                if(specifiedUser != null) {
                    PlayerMemory memory = PlayerUtil.getPlayerMemory(specifiedUser);
                    int seconds = memory.getSecondsPlayed();
                    int day = (int) TimeUnit.SECONDS.toDays(seconds);
                    long hours = TimeUnit.SECONDS.toHours(seconds) - (day *24);
                    long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
                    long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);

                    final Component component = (specifiedUser.displayName().append(Component.text("'s play-time: ")))
                            .decorate(TextDecoration.BOLD)
                            .color(TextColor.color(14912323))
                            .append(Component.text(day + "d" + hours + "h" + minute + "m" + second + "s"));
                    commandSender.sendMessage(component);
                }else {
                    File f = new File((Bukkit.getPluginsFolder().getAbsolutePath() + "/player/" + Bukkit.getOfflinePlayer(strings[0]).getUniqueId()) + "/general.yml");
                    if(f.exists()) {
                        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                        int seconds = cfg.getInt("data.secondsPlayed");
                        int day = (int) TimeUnit.SECONDS.toDays(seconds);
                        long hours = TimeUnit.SECONDS.toHours(seconds) - (day *24);
                        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
                        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);

                        final Component component = (Component.text(Bukkit.getOfflinePlayer(strings[0]).getName() + "'s play-time: "))
                                .decorate(TextDecoration.BOLD)
                                .color(TextColor.color(14912323))
                                .append(Component.text(day + "d" + hours + "h" + minute + "m" + second + "s"));
                        commandSender.sendMessage(component);
                    }else {
                        final Component component = Component.text("Player data could not be found for specified user.")
                                .color(TextColor.color(14430499));
                        commandSender.sendMessage(component);
                    }
                }
            }
        } else {
            final Component component = Component.text("Please use the following command syntax: /playtimeuser <user>")
                    .color(TextColor.color(14430499));
            commandSender.sendMessage(component);
        }
        return true;
    }
}
