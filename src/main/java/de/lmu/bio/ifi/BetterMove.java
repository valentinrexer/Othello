package de.lmu.bio.ifi;

import szte.mi.Move;

import java.util.Objects;

public class BetterMove extends Move {
    /**
     * Constructs the move. It does not perform any checks on the values.
     *
     * @param x X coordinate on the board. It is indexed from 0, that is,
     *          its possible vales are 0, 1, etc.
     * @param y Y coordinate on the board. It is indexed from 0, that is,
     *          its possible vales are 0, 1, etc.
     */
    public BetterMove(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean equals(Object obj) {
        Move move = (Move) obj;
        return (move.x == x && move.y == y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
