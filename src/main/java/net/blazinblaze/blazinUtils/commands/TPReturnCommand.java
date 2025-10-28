package net.blazinblaze.blazinUtils.commands;

import net.blazinblaze.blazinUtils.BlazinUtils;
import net.blazinblaze.blazinUtils.data.PlayerMemory;
import net.blazinblaze.blazinUtils.utility.PlayerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TPReturnCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (commandSender instanceof Player plr) {
            PlayerMemory memory = PlayerUtil.getPlayerMemory(plr);
            Location savedLocation = memory.getLastLocationBeforeTP();

            BlazinUtils instance = BlazinUtils.getInstance();
            if(instance != null) {
                if(!(savedLocation.getWorld() == instance.getServer().getWorlds().getFirst() && savedLocation.getBlockX() == 0 && savedLocation.getBlockY() == 0 && savedLocation.getBlockZ() == 0)) {
                    plr.teleport(savedLocation);
                    memory.setLastLocationBeforeTP(new Location(instance.getServer().getWorlds().getFirst(), 0, 0, 0));
                    final Component component = Component.text("Successfully returned!")
                            .color(TextColor.color(42496));
                    commandSender.sendMessage(component);
                }else {
                    final Component component = Component.text("No saved location to return to.")
                            .color(TextColor.color(14430499));
                    commandSender.sendMessage(component);
                }
            }
        }else {
            BlazinUtils.getInstance().getLogger().warning("This command must be run by a player.");
        }
        return true;
    }
}
