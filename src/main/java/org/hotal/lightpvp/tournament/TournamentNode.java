package org.hotal.lightpvp.tournament;

import lombok.Getter;
import lombok.Setter;

public class TournamentNode {

    @Getter
    private TournamentNode parent;
    @Getter
    private TournamentNode left;
    @Getter
    private TournamentNode right;
    @Getter
    private TournamentEntry playerEntry;
    @Getter
    private boolean isMatch;
    @Getter
    int depth;
    @Setter
    double pos = -1;
    int cMin;
    int cMax;
    @Getter
    @Setter
    WinnerType winnerType = WinnerType.NONE;

    public TournamentNode(TournamentNode parent, TournamentEntry player, int depth, int cMin, int cMax) {
        this.parent = parent;
        this.isMatch = false;
        this.playerEntry = player;
        this.depth = depth;
        this.cMin = cMin;
        this.cMax = cMax;
    }

    public void toMatch(TournamentEntry opponent) {
        isMatch = true;
        left = new TournamentNode(this, playerEntry, depth + 1, cMin, cMax - ((cMax - cMin + 1) / 2));
        right = new TournamentNode(this, opponent, depth + 1, cMin + ((cMax - cMin + 2) / 2), cMax);
        playerEntry = null;
    }

    public double getPos() {
        if (pos == -1) {
            if (isMatch) {
                pos = (left.getPos() + right.getPos()) / 2.0;
            } else {
                pos = cMin;
            }
        }
        return pos;
    }

    public TournamentEntry getWinner() {
        return switch (winnerType) {
            case LEFT -> getLeft().getWinner();
            case RIGHT -> getRight().getWinner();
            case NONE -> null;
        };
    }
}