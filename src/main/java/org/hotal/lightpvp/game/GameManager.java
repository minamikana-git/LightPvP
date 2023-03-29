package org.hotal.lightpvp.game;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.hotal.lightpvp.LightPvP;
import org.hotal.lightpvp.battle.Battle;
import org.hotal.lightpvp.tournament.Tournament;
import org.hotal.lightpvp.tournament.TournamentEntry;
import org.hotal.lightpvp.tournament.impl.MatchNode;
import org.hotal.lightpvp.util.Config;
import org.hotal.lightpvp.util.LeaderBoardUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class GameManager {

    @Getter
    private static Tournament tournament;
    @Getter
    private static Battle currentBattle;
    @Getter
    private static final List<TournamentEntry> entries = new ArrayList<>();
    private static final List<GlowItemFrame> itemFrames = new ArrayList<>();

    @Getter
    private static Location lobbyLocation;
    @Getter
    private static Location leftSpawnLocation;
    @Getter
    private static Location rightSpawnLocation;

    public static void register(Player player) {
        if (entries.stream().map(TournamentEntry::getUuid).noneMatch(uuid -> player.getUniqueId().equals(uuid))) {
            entries.add(new TournamentEntry(player.getUniqueId(), player.getName()));
        }
    }

    public static void unregister(String name) {
        entries.removeIf(entry -> entry.getName().equals(name));
    }

    public static boolean create() {
        if (isStarted()) {
            return false;
        }
        try {
            loadConfig();
        } catch (Exception e) {
            return false;
        }
        if (entries.size() < 2) {
            return false;
        }
        tournament = new Tournament(entries);
        updateItemFrame();
        return true;
    }

    public static void nextMatch() {
        if (tournament == null) {
            return;
        }
        if (tournament.isEmpty()) {
            return;
        }
        MatchNode next = tournament.nextMatch();
        currentBattle = new Battle(next);
        currentBattle.start();
    }

    public static MatchNode getNextMatch() {
        if (tournament == null) {
            return null;
        }
        if (tournament.isEmpty()) {
            return null;
        }
        return tournament.getNextMatch();
    }

    public static void end() {
        tournament = null;
    }

    public static boolean isStarted() {
        return tournament != null && !tournament.isEmpty();
    }

    public static void updateItemFrame() {
        final List<MapRenderer> maps = LeaderBoardUtils.createMap(tournament);
        if (maps.size() > itemFrames.size()) {
            return;
        }
        for (int i = 0; i < maps.size(); i++) {
            MapView view = Bukkit.createMap(Bukkit.getWorlds().get(0));
            view.getRenderers().clear();
            view.addRenderer(maps.get(i));
            ItemStack map = new ItemStack(Material.FILLED_MAP);
            MapMeta mapMeta = (MapMeta) map.getItemMeta();
            mapMeta.setMapView(view);
            map.setItemMeta(mapMeta);
            itemFrames.get(i).setItem(map);
        }
    }

    private static void loadConfig() {
        FileConfiguration config = LightPvP.getPlugin().getConfig();
        lobbyLocation = Objects.requireNonNull(config.getLocation(Config.LOBBY_LOCATION));
        leftSpawnLocation = Objects.requireNonNull(config.getLocation(Config.LEFT_SPAWN_LOCATION));
        rightSpawnLocation = Objects.requireNonNull(config.getLocation(Config.RIGHT_SPAWN_LOCATION));
        itemFrames.clear();
        config.getStringList(Config.ITEM_FRAMES).stream().map(UUID::fromString).forEach(uuid -> {
            for (Entity entity : Bukkit.getWorlds().get(0).getEntities()) {
                if (entity.getType() != EntityType.GLOW_ITEM_FRAME) {
                    continue;
                }
                if (entity.getUniqueId().equals(uuid)) {
                    itemFrames.add((GlowItemFrame) entity);
                }
            }
        });
    }

}
