package net.blazinblaze.blazinUtils.commands;

import net.blazinblaze.blazinUtils.BlazinUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ServerVCCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            final Component component = Component.text("Proximity voice chat is available if you're interested! - ")
                    .decorate(TextDecoration.BOLD)
                    .color(TextColor.color(16777215))
                    .append(Component.text("Click here to install Simple Voice Chat (fabric/forge/etc)!", NamedTextColor.AQUA).decorate(TextDecoration.ITALIC).decorate(TextDecoration.UNDERLINED))
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL,"https://modrinth.com/plugin/simple-voice-chat"));
            player.sendMessage(component);

        }else {
            BlazinUtils.getInstance().getLogger().warning("This command must be run by a player.");
        }

        return true;
    }
}
