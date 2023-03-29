package org.hotal.lightpvp.util;

import org.bukkit.Location;
import org.bukkit.entity.GlowItemFrame;
import org.hotal.lightpvp.LightPvP;

import java.util.List;

public class Config {

    public static final String ITEM_FRAMES = "item-frames";
    public static final String LOBBY_LOCATION = "lobby-location";
    public static final String LEFT_SPAWN_LOCATION = "left-spawn-location";
    public static final String RIGHT_SPAWN_LOCATION = "right-spawn-location";


    public static boolean addItemFrame(GlowItemFrame glowItemFrame) {
        final List<String> frames = LightPvP.getPlugin().getConfig().getStringList(ITEM_FRAMES);
        if (frames.size() < LeaderBoardUtils.ROWS * LeaderBoardUtils.COLUMNS) {
            frames.add(glowItemFrame.getUniqueId().toString());
            LightPvP.getPlugin().getConfig().set(ITEM_FRAMES, frames);
            return true;
        }
        return false;
    }

    public static void clearItemFrame() {
        LightPvP.getPlugin().getConfig().set(ITEM_FRAMES, null);
    }

    public static void setLobbyLocation(Location location) {
        LightPvP.getPlugin().getConfig().set(LOBBY_LOCATION, location);
    }

    public static void setLeftSpawnLocation(Location location) {
        LightPvP.getPlugin().getConfig().set(LEFT_SPAWN_LOCATION, location);
    }

    public static void setRightSpawnLocation(Location location) {
        LightPvP.getPlugin().getConfig().set(RIGHT_SPAWN_LOCATION, location);
    }

}