package org.hotal.lightpvp.tournament;

public abstract class AbstractNode implements INode {

    private final int depth;

    protected AbstractNode(int depth) {
        this.depth = depth;
    }

    @Override
    public int getDepth() {
        return depth;
    }
}
