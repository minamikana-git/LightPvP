package org.hotal.lightpvp.tournament;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class TournamentNode {

    @Getter
    private TournamentNode left;
    @Getter
    private TournamentNode right;
    @Setter
    @Getter
    private UUID winner;
    @Getter
    private boolean isMatch;

    public TournamentNode(UUID player) {
        this.isMatch = false;
        winner = player;
    }

    public boolean isReady() {
        if (isMatch) {
            return left.getWinner() != null && right.getWinner() != null;
        } else {
            return true;
        }
    }

    public void toMatch(UUID opponent) {
        isMatch = true;
        left = new TournamentNode(winner);
        right = new TournamentNode(opponent);
    }

}