package org.hotal.lightpvp.tournament;

import lombok.Getter;

import java.util.List;
import java.util.Stack;

public class Tournament {

    public static Tournament create(List<TournamentEntry> players) throws IllegalArgumentException {
        if (players.size() < 2) {
            throw new IllegalArgumentException("人数が少なすぎます");
        }
        return new Tournament(players);
    }

    @Getter
    private final TournamentNode root;
    private final Stack<TournamentNode> matchStack;
    @Getter
    private int depth;
    @Getter
    private final int numOfPlayers;

    private Tournament(List<TournamentEntry> players) {
        this.numOfPlayers = players.size();
        this.root = new TournamentNode(null, players.get(0), 0, 1, players.size());
        this.matchStack = new Stack<>();
        this.root.toMatch(players.get(1));
        this.matchStack.add(root);
        this.depth = 1;

        int nav = 0;
        int navMax = 1;
        int depth = 1;

        for (int i = 2; i < players.size(); i++) {
            TournamentNode currentNode = root;
            for (int d = 0; d < depth; d++) {
                if ((nav & (1 << d)) > 0) {
                    currentNode = currentNode.getRight();
                } else {
                    currentNode = currentNode.getLeft();
                }
            }
            currentNode.toMatch(players.get(i));
            matchStack.add(currentNode);
            if (nav == 0) {
                this.depth++;
            }
            if (nav == navMax) {
                depth++;
                navMax = (1 << depth) - 1;
                nav = 0;
            } else {
                nav++;
            }
        }
    }

    public TournamentNode nextMatch() {
        return matchStack.pop();
    }

    public boolean isEmpty() {
        return matchStack.isEmpty();
    }

}
