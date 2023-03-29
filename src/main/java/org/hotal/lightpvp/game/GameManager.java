package org.hotal.lightpvp.game;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.hotal.lightpvp.battle.Battle;
import org.hotal.lightpvp.tournament.Tournament;
import org.hotal.lightpvp.tournament.TournamentEntry;
import org.hotal.lightpvp.tournament.impl.MatchNode;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    @Getter
    private static Tournament tournament;
    @Getter
    private static Battle currentBattle;
    @Getter
    private static final List<TournamentEntry> entries = new ArrayList<>();

    public static void register(Player player) {
        if (entries.stream().map(TournamentEntry::getUuid).noneMatch(uuid -> player.getUniqueId().equals(uuid))) {
            entries.add(new TournamentEntry(player.getUniqueId(), player.getName()));
        }
    }

    public static void unregister(Player player) {
        entries.removeIf(entry -> entry.getUuid().equals(player.getUniqueId()));
    }

    public static boolean start() {
        if (isStarted()) {
            return false;
        }
        if (entries.size() < 2) {
            return false;
        }
        tournament = Tournament.create(entries);
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

}
