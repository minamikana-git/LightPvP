package org.hotal.lightpvp.util;

public class MathUtils {

    public static int getHighestOneBitPos(int n) {
        int r = 0;
        while (n > 0) {
            n = n >> 1;
            r++;
        }
        return r;
    }

}
