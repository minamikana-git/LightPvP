package org.hotal.lightpvp.tournament;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hotal.lightpvp.tournament.impl.MatchNode;
import org.hotal.lightpvp.tournament.impl.PlayerNode;
import org.hotal.lightpvp.util.MathUtils;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class Tournament {

    public static Tournament create(List<TournamentEntry> players) throws IllegalArgumentException {
        if (players.size() < 2) {
            throw new IllegalArgumentException("人数が少なすぎます");
        }
        return new Tournament(players);
    }

    @Getter
    private final List<TournamentEntry> players;
    @Getter
    private final MatchNode root;
    @Getter
    private final int depth;
    private final Stack<MatchNode> matchStack;

    private Tournament(List<TournamentEntry> players) {
        this.players = players;
        this.root = new MatchNode(0);
        this.matchStack = new Stack<>();
        this.matchStack.add(root);
        this.depth = MathUtils.getHighestOneBitPos(players.size() - 1);

        Queue<NodeEntry> nodeEntries = new ArrayDeque<>();

        nodeEntries.add(new NodeEntry(root, 0, players.size(), 0));

        while (!nodeEntries.isEmpty()) {
            NodeEntry nodeEntry = nodeEntries.poll();
            MatchNode parent = nodeEntry.getParent();
            int leftMin = nodeEntry.getMinIndex();
            int leftCount = (nodeEntry.getCount() + 1) / 2;
            int rightMin = nodeEntry.getMinIndex() + leftCount;
            int rightCount = nodeEntry.getCount() / 2;

            if (leftCount == 1) {
                parent.setLeft(new PlayerNode(players.get(leftMin), (leftMin + 1.0) / (players.size() + 1.0), nodeEntry.getDepth() + 1));
            } else {
                MatchNode left = new MatchNode(nodeEntry.getDepth() + 1);
                parent.setLeft(left);
                matchStack.add(left);
                nodeEntries.add(new NodeEntry(left, leftMin, leftCount, left.getDepth()));
            }

            if (rightCount == 1) {
                parent.setRight(new PlayerNode(players.get(rightMin), (rightMin + 1.0) / (players.size() + 1.0), nodeEntry.getDepth() + 1));
            } else {
                MatchNode right = new MatchNode(nodeEntry.getDepth() + 1);
                parent.setRight(right);
                matchStack.add(right);
                nodeEntries.add(new NodeEntry(right, rightMin, rightCount, right.getDepth()));
            }
        }
    }

    public MatchNode nextMatch() {
        return matchStack.pop();
    }

    public MatchNode getNextMatch() {
        return matchStack.peek();
    }

    public boolean isEmpty() {
        return matchStack.isEmpty();
    }

    @AllArgsConstructor
    @Getter
    private static class NodeEntry {

        private MatchNode parent;
        private int minIndex;
        private int count;
        private int depth;
    }

}
