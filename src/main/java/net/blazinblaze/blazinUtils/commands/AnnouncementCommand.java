package net.blazinblaze.blazinUtils.commands;

import net.blazinblaze.blazinUtils.BlazinUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if(args.length > 0) {
                StringBuilder sb = new StringBuilder();
                for(int i = 0; i < args.length; i++) {
                    if(i == 0) {
                        sb.append(args[i]);
                    }else {
                        sb.append(" ").append(args[i]);
                    }
                }
                String message = sb.toString();
                List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                for(int i2 =0; i2 < players.size(); i2++) {
                    Player iPlayer = players.get(i2);
                    iPlayer.showTitle(Title.title(
                           Component.text("Announcement!")
                                   .color(TextColor.color(13657113))
                                   .decorate(TextDecoration.BOLD),
                           Component.text(message)
                                   .color(TextColor.color(13185315)), Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(10), Duration.ofSeconds(1))));
                    iPlayer.playSound(iPlayer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10.0F, 1.0F);
                }
            }else {
                final Component component = Component.text("Please use the following command syntax: /announcement <message>")
                        .color(TextColor.color(13185315));
                player.sendMessage(component);
            }
        }else {
            BlazinUtils.getInstance().getLogger().warning("This command must be run by a player.");
        }

        return true;
    }
}
