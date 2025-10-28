package net.blazinblaze.blazinUtils.utility;

import net.blazinblaze.blazinUtils.BlazinUtils;
import net.blazinblaze.blazinUtils.data.PlayerMemory;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerUtil {
    private static Map<String, PlayerMemory> playerMemory = new HashMap<>();

    public static PlayerMemory getPlayerMemory(Player p) {
        if(!playerMemory.containsKey(p.getUniqueId().toString())) {
            PlayerMemory m = new PlayerMemory();
            playerMemory.put(p.getUniqueId().toString(), m);
            return m;
        }
        return playerMemory.get(p.getUniqueId().toString());
    }

    public static void setPlayerMemory(Player p, PlayerMemory memory) {
        if(memory == null) playerMemory.remove(p.getUniqueId().toString());
        else playerMemory.put(p.getUniqueId().toString(), memory);
    }

    public static String getFolderPath(Player p) {
        return BlazinUtils.getInstance().getDataFolder().getAbsolutePath() + "/player/" + p.getUniqueId();
    }
}
