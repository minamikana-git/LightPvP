package org.hotal.lightpvp.map;

import org.bukkit.map.MapRenderer;
import org.hotal.lightpvp.game.GameManager;
import org.hotal.lightpvp.map.impl.NormalMapProvider;
import org.hotal.lightpvp.map.impl.SmallMapProvider;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class LeaderboardManager {

    private static final Map<LeaderboardSize, IMapProvider> providerMap = new EnumMap<>(LeaderboardSize.class);
    private static final Map<LeaderboardSize, List<MapRenderer>> mapCache = new EnumMap<>(LeaderboardSize.class);

    public static void init() {
        registerProvider(new NormalMapProvider());
        registerProvider(new SmallMapProvider());
    }

    public static void registerProvider(IMapProvider provider) {
        providerMap.put(provider.getTargetSize(), provider);
    }

    public static void update() {
        providerMap.forEach((key, value) -> mapCache.put(key, value.provide(GameManager.getTournament())));
    }

    @Nullable
    public static List<MapRenderer> getMap(LeaderboardSize size) {
        return mapCache.get(size);
    }

}
