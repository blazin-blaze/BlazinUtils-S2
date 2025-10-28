package net.blazinblaze.blazinUtils.commands;

import net.blazinblaze.blazinUtils.BlazinUtils;
import net.blazinblaze.blazinUtils.data.PlayerMemory;
import net.blazinblaze.blazinUtils.utility.PlayerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static net.blazinblaze.blazinUtils.BlazinUtils.*;

public class TPSpawnCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (commandSender instanceof Player plr) {
            if(jailedPlayers != null) {
                boolean isJailed = false;
                String uuid = plr.getUniqueId().toString();
                for(String str : jailedPlayers) {
                    if(str.matches(uuid)) {
                        isJailed = true;
                        break;
                    }
                }
                if(!isJailed) {
                    BlazinUtils instance = BlazinUtils.getInstance();
                    if(instance != null) {
                        PlayerMemory memory = PlayerUtil.getPlayerMemory(plr);
                        memory.setLastLocationBeforeTP(plr.getLocation());
                        plr.teleport(new Location(instance.getServer().getWorlds().getFirst(), 1406, 63, -527));
                        final Component component = Component.text("Successfully teleported to spawn!")
                                .color(TextColor.color(42496));
                        commandSender.sendMessage(component);
                    }
                }else {
                    final Component component = Component.text("You cannot teleport to spawn while jailed!").decorate(TextDecoration.BOLD)
                            .color(TextColor.color(14430499));
                    commandSender.sendMessage(component);
                }
            }else {
                final Component component = Component.text("An unexpected error occurred while trying to teleport to spawn.").decorate(TextDecoration.BOLD)
                        .color(TextColor.color(14430499));
                commandSender.sendMessage(component);
            }
            //if(BlazinUtils.isSpawnEventActive()) {

            //}else {
                //final Component component = Component.text("A spawn event is not currently active.")
                        //.color(TextColor.color(14430499));
                //commandSender.sendMessage(component);
            //}
        }else {
            BlazinUtils.getInstance().getLogger().warning("This command must be run by a player.");
        }
        return true;
    }
}
