package org.hotal.lightpvp.map;

import org.bukkit.map.MapRenderer;
import org.hotal.lightpvp.tournament.Tournament;

import java.util.List;

public interface IMapProvider {

    LeaderboardSize getTargetSize();

    List<MapRenderer> provide(Tournament tournament);

}
