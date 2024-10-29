package de.lmu.bio.ifi;

import szte.mi.Move;

import java.util.Scanner;

public class BitBoardRunner {
    public static void main(String[] args) {
        int pos = 63;
        int inc = -8;
        long mask = 1L << pos;

        System.out.println(OthelloBoard.nextOutOfBounds(pos, inc));

    }


}
