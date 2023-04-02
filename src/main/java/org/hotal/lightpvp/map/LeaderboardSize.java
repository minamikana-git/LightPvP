package org.hotal.lightpvp.map;

import lombok.Getter;

public enum LeaderboardSize {

    NORMAL(5, 6),
    SMALL(5, 5);

    @Getter
    private final int rows;
    @Getter
    private final int columns;

    LeaderboardSize(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
    }

}
