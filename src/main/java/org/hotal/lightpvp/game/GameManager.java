package org.hotal.lightpvp.game;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.hotal.lightpvp.battle.Battle;
import org.hotal.lightpvp.tournament.Tournament;
import org.hotal.lightpvp.tournament.TournamentEntry;
import org.hotal.lightpvp.tournament.impl.MatchNode;
import org.hotal.lightpvp.util.LeaderBoardUtils;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    @Getter
    private static Tournament tournament;
    @Getter
    private static Battle currentBattle;
    @Getter
    private static final List<TournamentEntry> entries = new ArrayList<>();
    private static final List<GlowItemFrame> itemFrames = new ArrayList<>();

    public static void register(Player player) {
        if (entries.stream().map(TournamentEntry::getUuid).noneMatch(uuid -> player.getUniqueId().equals(uuid))) {
            entries.add(new TournamentEntry(player.getUniqueId(), player.getName()));
        }
    }

    public static void unregister(Player player) {
        entries.removeIf(entry -> entry.getUuid().equals(player.getUniqueId()));
    }

    public static boolean create() {
        if (isStarted()) {
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

    public static boolean addItemFrame(GlowItemFrame glowItemFrame) {
        if (itemFrames.size() < LeaderBoardUtils.ROWS * LeaderBoardUtils.COLUMNS) {
            itemFrames.add(glowItemFrame);
            return true;
        }
        return false;
    }

    public static void clearItemFrame() {
        itemFrames.clear();
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

}
