package org.hotal.lightpvp.tournament;

import lombok.Getter;

import java.util.List;
import java.util.Stack;
import java.util.UUID;

public class Tournament {

    public static Tournament create(List<UUID> players) throws IllegalArgumentException {
        if (players.size() < 2) {
            throw new IllegalArgumentException("人数が少なすぎます");
        }
        return new Tournament(players);
    }

    @Getter
    private final TournamentNode root;
    private final Stack<TournamentNode> matchStack;

    private Tournament(List<UUID> players) {
        root = new TournamentNode(players.get(0));
        matchStack = new Stack<>();
        root.toMatch(players.get(1));
        matchStack.add(root);

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
