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

public class YouTubeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            final Component component = Component.text("BlazinBlaze - ")
                    .decorate(TextDecoration.BOLD)
                    .color(TextColor.color(14680064))
                    .append(Component.text("[Main YouTube, click here!]", NamedTextColor.AQUA).decorate(TextDecoration.ITALIC).decorate(TextDecoration.UNDERLINED))
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL,"https://youtube.com/@BlazinBlaze"));
            player.sendMessage(component);

        }else {
            BlazinUtils.getInstance().getLogger().warning("This command must be run by a player.");
        }

        return true;
    }
}
