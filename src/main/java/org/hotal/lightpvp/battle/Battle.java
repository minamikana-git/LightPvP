package org.hotal.lightpvp.battle;

import lombok.Getter;
import org.hotal.lightpvp.tournament.impl.MatchNode;

public class Battle {

    @Getter
    private final MatchNode node;

    public Battle(MatchNode node) {
        this.node = node;
    }

    public void start() {

    }

}
