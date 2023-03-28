package org.hotal.lightpvp.tournament.impl;

import org.hotal.lightpvp.tournament.AbstractNode;
import org.hotal.lightpvp.tournament.TournamentEntry;

public class PlayerNode extends AbstractNode {

    private final TournamentEntry entry;
    private final double pos;

    public PlayerNode(TournamentEntry entry, double pos, int depth) {
        super(depth);
        this.entry = entry;
        this.pos = pos;
    }

    @Override
    public TournamentEntry getPlayerEntry() {
        return entry;
    }

    @Override
    public double getPos() {
        return pos;
    }

    @Override
    public int getDepth() {
        return 0;
    }

}
