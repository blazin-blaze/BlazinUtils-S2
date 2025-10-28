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

public class SpawnEventToggleCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if(BlazinUtils.isSpawnEventActive()) {
                BlazinUtils.setSpawnEventActive(false);
                List<Player> list = new ArrayList<>(Bukkit.getOnlinePlayers());
                for(int i = 0; i < list.size(); i++) {
                    Player plr = list.get(i);
                    final Component component = Component.text("Spawn Event Ended.")
                            .decorate(TextDecoration.BOLD)
                            .color(TextColor.color(14430499));
                    final Component component2 = Component.text("/tpreturn to go home.")
                            .color(TextColor.color(5898240));
                    plr.showTitle(Title.title(component, component2, Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(5), Duration.ofSeconds(1))));
                    plr.playSound(plr.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10.0F, 1.0F);
                }
                final Component component = Component.text("Spawn event deactivated!")
                        .decorate(TextDecoration.BOLD)
                        .color(TextColor.color(14430499));
                sender.sendMessage(component);
            }else {
                BlazinUtils.setSpawnEventActive(true);
                List<Player> list = new ArrayList<>(Bukkit.getOnlinePlayers());
                for(int i = 0; i < list.size(); i++) {
                    Player plr = list.get(i);
                    final Component component = Component.text("A Spawn Event Has Begun!")
                            .decorate(TextDecoration.BOLD)
                            .color(TextColor.color(42496));//14430499
                    final Component component2 = Component.text("/tpspawn to join!")
                            .color(TextColor.color(24320));//5898240
                    plr.showTitle(Title.title(component, component2, Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(5), Duration.ofSeconds(1))));
                    plr.playSound(plr.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10.0F, 1.0F);
                }
                final Component component = Component.text("Spawn event activated!")
                        .decorate(TextDecoration.BOLD)
                        .color(TextColor.color(42496));
                sender.sendMessage(component);
            }
        }else {
            BlazinUtils.getInstance().getLogger().warning("This command must be run by a player.");
        }

        return true;
    }
}
