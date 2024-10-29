package de.lmu.bio.ifi;

public class BoardMasks {

    public static long cornersTouchedMask = 0b1000000100000000000000000000000000000000000000000000000010000001L;

    public static final long RIGHT_EDGE_MASK = 0b1000000010000000100000001000000010000000100000001000000010000000L;
    public static final long LEFT_EDGE_MASK = 0b100000001000000010000000100000001000000010000000100000001L;
    public static final long DOWN_MASK = 0b11111111111111111111111111111111111111111111111111111111L;
    public static final long UP_MASK = 0b1111111111111111111111111111111111111111111111111111111100000000L;
    public static final long RIGHT_MASK = 0b111111101111111011111110111111101111111011111110111111101111111L;
    public static final long LEFT_MASK = 0b1111111011111110111111101111111011111110111111101111111011111110L;



    public static final int[] SCORE_MATRIX = {10000, -3000, 1000, 800, 800, 1000, -3000, 10000, -3000, -5000, -450, -500, -500, -450, -5000, -3000, 1000, -450, 30, 10, 10, 30, -450, 1000, 800, -500, 10, 50, 50, 10, -500, 800, 800, -500, 10, 50, 50, 10, -500, 800, 1000, -450, 30, 10, 10, 30, -450, 1000, -3000, -5000, -450, -500, -500, -450, -5000, -3000, 10000, -3000, 1000, 800, 800, 1000, -3000, 10000};


    public static final int[] DIRECTIONS = {
            1, -1, 9, -9, 8, -8, 7, -7
    };

    public static final long[] closeToCornerOne = {
            1L << 8,  // {1,0}
            1L << 9,  // {1,1}
            1L << 1  // {0,1}
    };

    public static final long[] closeToCornerTwo = {
            1L << 6,  // {6,0}
            1L << 14,  // {6,1}
            1L << 15  // {7,1}
    };

    public static final long[] closeToCornerThree = {
            1L << 48,  // {0,6}
            1L << 49,  // {1,6}
            1L << 57  // {1,7}
    };

    public static final long[] closeToCornerFour = {
            1L << 55,  // {7,6}
            1L << 54,  // {6,6}
            1L << 62   // {6,7}
    };


    public static final long[] closeCornerSquares = {
            1L << 8,  // {1,0}
            1L << 9,  // {1,1}
            1L << 1,  // {0,1}

            1L << 6,  // {6,0}
            1L << 14,  // {6,1}
            1L << 15,  // {7,1}

            1L << 48,  // {0,6}
            1L << 49,  // {1,6}
            1L << 57,  // {1,7}

            1L << 55,  // {7,6}
            1L << 54,  // {6,6}
            1L << 62   // {6,7}
    };
    public static final long[] edgeSquares = {
            1L,              // 0,0
            1L << 1,         // 1,0
            1L << 2,         // 2,0
            1L << 3,         // 3,0
            1L << 4,         // 4,0
            1L << 5,         // 5,0
            1L << 6,         // 6,0
            1L << 7,         // 7,0
            1L << 8,         // 0,1
            1L << 16,        // 0,2
            1L << 24,        // 0,3
            1L << 32,        // 0,4
            1L << 40,        // 0,5
            1L << 48,        // 0,6
            1L << 56,        // 0,7
            1L << 15,        // 7,1
            1L << 23,        // 7,2
            1L << 31,        // 7,3
            1L << 39,        // 7,4
            1L << 47,        // 7,5
            1L << 55,        // 7,6
            1L << 63,        // 7,7
            1L << 57,        // 1,7
            1L << 58,        // 2,7
            1L << 59,        // 3,7
            1L << 60,        // 4,7
            1L << 61,        // 5,7
            1L << 62         // 6,7
    };
    public static final int[][] stableCheckDirections = {
            {1, 0}, {0, 1}, {1, 1}, {1, -1}
    };
    public static final long[] corners = {
            1L, 1L << 7, 1L << 56, 1L << 63
    };

}
