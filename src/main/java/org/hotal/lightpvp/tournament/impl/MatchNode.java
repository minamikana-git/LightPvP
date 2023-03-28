package org.hotal.lightpvp.tournament.impl;

import lombok.Getter;
import lombok.Setter;
import org.hotal.lightpvp.tournament.AbstractNode;
import org.hotal.lightpvp.tournament.INode;
import org.hotal.lightpvp.tournament.TournamentEntry;
import org.hotal.lightpvp.tournament.WinnerType;

public class MatchNode extends AbstractNode {

    @Setter
    @Getter
    private INode left;
    @Setter
    @Getter
    private INode right;
    @Setter
    @Getter
    private WinnerType winnerType = WinnerType.NONE;
    private double pos = -1;

    public MatchNode(int depth) {
        super(depth);
    }

    @Override
    public TournamentEntry getPlayerEntry() {
        return switch (winnerType) {
            case LEFT -> left.getPlayerEntry();
            case RIGHT -> right.getPlayerEntry();
            case NONE -> null;
        };
    }

    @Override
    public double getPos() {
        if (pos < 0) {
            pos = (left.getPos() + right.getPos()) / 2;
        }
        return pos;
    }

}
