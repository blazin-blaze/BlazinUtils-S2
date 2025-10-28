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
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class PurgeTimedCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 1) {
            if (sender instanceof Player) {
                int timeValue;
                try {
                    timeValue = Integer.parseInt(args[0]);
                }
                catch (NumberFormatException e) {
                    final Component component = Component.text("Invalid time. Resulting to default of 60 seconds.")
                            .color(TextColor.color(14430499));
                    sender.sendMessage(component);
                    timeValue = 60;
                }
                Player player = (Player) sender;
                if (BlazinUtils.isPurgeActive() || BlazinUtils.isTimedPurgeActive()) {
                    final Component component = Component.text("A purge is already active!")
                            .decorate(TextDecoration.BOLD)
                            .color(TextColor.color(14430499));
                    sender.sendMessage(component);
                }
                else {
                    BlazinUtils.setTimedPurgeActive(true);
                    List<Player> list = new ArrayList<>(Bukkit.getOnlinePlayers());
                    for (int i = 0; i < list.size(); i++)
                    {
                        Player plr = list.get(i);
                        final Component component = Component.text("THE PURGE HAS BEGUN.")
                                .decorate(TextDecoration.BOLD)
                                .color(TextColor.color(14430499));
                        final Component component2 = Component.text("Protect your claims!")
                                .color(TextColor.color(5898240));
                        plr.showTitle(Title.title(component, component2, Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(5), Duration.ofSeconds(1))));
                        plr.playSound(plr.getLocation(), Sound.ENTITY_WITHER_DEATH, 10.0F, 1.0F);
                    }
                    final Component component = Component.text("Purge activated!")
                            .decorate(TextDecoration.BOLD)
                            .color(TextColor.color(14430499));
                    sender.sendMessage(component);
                    PurgeCommandTask commandTask = new PurgeCommandTask();
                    commandTask.runTaskLater(BlazinUtils.getInstance(), 20L * timeValue);
                }
            }
        } else if (args.length > 1) {
            final Component component = Component.text("Please use the following command syntax: /claimpurgetimed <time-in-seconds>")
                    .color(TextColor.color(14430499));
            sender.sendMessage(component);
        } else {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (BlazinUtils.isPurgeActive() || BlazinUtils.isTimedPurgeActive()) {
                    final Component component = Component.text("A purge is already active!")
                            .decorate(TextDecoration.BOLD)
                            .color(TextColor.color(14430499));
                    sender.sendMessage(component);
                }
                else {
                    BlazinUtils.setTimedPurgeActive(true);
                    List<Player> list = new ArrayList<>(Bukkit.getOnlinePlayers());
                    for (int i = 0; i < list.size(); i++) {
                        Player plr = list.get(i);
                        final Component component = Component.text("THE PURGE HAS BEGUN.")
                                .decorate(TextDecoration.BOLD)
                                .color(TextColor.color(14430499));
                        final Component component2 = Component.text("Protect your claims!")
                                .color(TextColor.color(5898240));
                        plr.showTitle(Title.title(component, component2, Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(5), Duration.ofSeconds(1))));
                        plr.playSound(plr.getLocation(), Sound.ENTITY_WITHER_DEATH, 10.0F, 1.0F);
                    }
                    final Component component = Component.text("Purge activated!")
                            .decorate(TextDecoration.BOLD)
                            .color(TextColor.color(14430499));
                    sender.sendMessage(component);
                    PurgeCommandTask commandTask = new PurgeCommandTask();
                    commandTask.runTaskLater(BlazinUtils.getInstance(), 20L * 60);
                }
            }
        }

        return true;
    }

    private static class PurgeCommandTask extends BukkitRunnable
    {
        @Override
        public void run() {
            BlazinUtils.setTimedPurgeActive(false);
            List<Player> list = new ArrayList<>(Bukkit.getOnlinePlayers());
            for(int i = 0; i < list.size(); i++) {
                Player plr = list.get(i);
                final Component component = Component.text("THE PURGE HAS ENDED!")
                        .decorate(TextDecoration.BOLD)
                        .color(TextColor.color(42496));
                final Component component2 = Component.text("You may rest.")
                        .color(TextColor.color(24320));
                plr.showTitle(Title.title(component, component2, Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(5), Duration.ofSeconds(1))));
                plr.playSound(plr.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10.0F, 1.0F);
            }
        }
    }
}
