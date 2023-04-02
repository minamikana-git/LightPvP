package org.hotal.lightpvp.util;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.hotal.lightpvp.LightPvP;
import org.hotal.lightpvp.map.Leaderboard;

import java.util.UUID;

public class Config {

    public static final String ITEM_FRAMES = "item-frames";
    public static final String LOBBY_LOCATION = "lobby-location";
    public static final String LEFT_SPAWN_LOCATION = "left-spawn-location";
    public static final String RIGHT_SPAWN_LOCATION = "right-spawn-location";
    public static final String LEADERBOARDS = "leaderboards";
    public static final String LEADERBOARD_SIZE = "size";

    public static void saveLeaderboard(Leaderboard leaderboard) {
        String section = LEADERBOARDS + "." + UUID.randomUUID() + ".";
        LightPvP.getPlugin().getConfig().set(section + ITEM_FRAMES, leaderboard.getItemFrames().stream().map(Entity::getUniqueId).map(UUID::toString).toList());
        LightPvP.getPlugin().getConfig().set(section + LEADERBOARD_SIZE, leaderboard.getSize().toString());
    }

    public static void clearLeaderboards() {
        LightPvP.getPlugin().getConfig().set(LEADERBOARDS, null);
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