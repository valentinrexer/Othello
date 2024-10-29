package de.lmu.bio.ifi;

import szte.mi.Move;

import java.util.ArrayList;
import java.util.List;

public class OthelloBoard {

    //The game board is represented as two longs
    public long blackCoins;
    public long whiteCoins;
    public long stableCoins;

    public OthelloBoard(){
        this.whiteCoins = 0L;
        this.blackCoins = 0L;
        this.stableCoins = 0L;

        setCoin(true, 4, 3);
        setCoin(true, 3, 4);
        setCoin(false, 3, 3);
        setCoin(false, 4, 4);
    }



    //________________________________________________
    //functions for board manipulation and information
    //________________________________________________

    public void setCoin(boolean color, int x, int y) {
        long mask = 1L << (y * 8 + x);

        blackCoins = color ? (blackCoins | mask) : (blackCoins & ~mask);
        whiteCoins = color ? (whiteCoins & ~mask) : (whiteCoins | mask);
    }

    public int coinAtPosition(int x, int y) {
        long mask = 1L << (y * 8 + x);

        if((whiteCoins & mask) != 0)
            return 2;

        else if((blackCoins & mask) != 0)
            return 1;

        else
            return 0;
    }


    //______________________________
    //Information about placed coins
    //______________________________

    public List<BetterMove> placedBlackCoins () {
        List<BetterMove> blackCoinsPlaced = new ArrayList<>();

        for(int i = 0; i < Long.SIZE; i++) {
            if((this.blackCoins & (1L << i)) != 0) {
                blackCoinsPlaced.add(new BetterMove( i % 8, i / 8));
            }
        }

        return blackCoinsPlaced;
    }
    public List<BetterMove> placedWhiteCoins () {
        List<BetterMove> whiteCoinsPlaced = new ArrayList<>();

        for(int i = 0; i < Long.SIZE; i++) {
            if((this.whiteCoins & (1L << i)) != 0) {
                whiteCoinsPlaced.add(new BetterMove( i % 8, i / 8));
            }
        }

        return whiteCoinsPlaced;
    }



    //_____________
    //Board control
    //_____________

    public static boolean isInBounds(int x, int y) {
        return !(x < 0 || x > 7 || y < 0 || y > 7);
    }

    public static boolean isInBounds(int pos) {
        return pos >= 0 && pos < 64;
    }
    public static boolean nextOutOfBounds (int pos, int inc) {

        long mask = 1L << pos;
        long movedMask = (moveMask(mask, inc));
        if ((mask & BoardMasks.RIGHT_EDGE_MASK) != 0 && (movedMask & BoardMasks.LEFT_EDGE_MASK) != 0) {
            return true;
        }


        if ((mask & BoardMasks.LEFT_EDGE_MASK) != 0 && (movedMask & BoardMasks.RIGHT_EDGE_MASK) != 0) {
            return true;
        }

        return !isInBounds(pos+inc);
    }

    @Override
    public String toString(){
        StringBuilder board = new StringBuilder();

        for(int y = 0; y < 8; y++) {
            for(int x = 0; x < 8; x++) {
                long mask = 1L << (y * 8 + x);

                if((whiteCoins & mask) != 0)
                    board.append("O");
                else if((blackCoins & mask) != 0)
                    board.append("X");
                else
                    board.append(".");


                if(x != 7)
                    board.append(" ");

                if(x == 7)
                    board.append("\n");
            }
        }
        return board.toString();
    }


    //corner
    public boolean cornersTouched() {
        return ((BoardMasks.cornersTouchedMask & blackCoins) != 0) || ((BoardMasks.cornersTouchedMask & whiteCoins) != 0);
    }

    //Determine if the coin at a specific position is stable
    public boolean coinStable(int x, int y) {

        long coinMask = 1L << (8 * y) + x;

        if((coinMask & stableCoins) != 0)
            return true;

        for(int[] dir : BoardMasks.stableCheckDirections) {
            if(!coinStableInDirection(x, y, dir[0], dir[1]))
                return false;
        }

        stableCoins = stableCoins | coinMask;
        return true;
    }
    private boolean coinStableInDirection(int x, int y, int incX, int incY){
        int position = y * 8 + x;

        if ((stableCoins & (1L << position)) != 0) {
            return true;
        }


        int coinColor = coinAtPosition(x, y);
        int oppCoinColor = (coinColor == 1) ? 2 : 1;



        //we start in one direction, if we find an opposite colored stone we move to the other direction
        //if we don't find an empty field in the other direction we declare it stable
        for(int i = x+incX, j = y+incY; isInBounds(i, j); i+=incX, j+=incY){

            //we find a coin of the opposite color -> we move to the other direction
            if(coinAtPosition(i, j) == oppCoinColor) {
                for(int a = x-incX, b = y-incY; isInBounds(a, b); a-=incX, b-=incY){
                    if(coinAtPosition(a, b) == 0)
                        return false;
                }
            }

            //we find a coin of the opposite color -> we move to the other direction
            if(coinAtPosition(i, j) == 0){
                for(int a = x-incX, b = y-incY; isInBounds(a, b); a-=incX, b-=incY){
                    if(coinAtPosition(a, b) == oppCoinColor || coinAtPosition(a, b) == 0)
                        return false;
                }
            }
        }

        stableCoins |= (1L << position);
        return true;
    }

    public long getValidMoves(boolean playerOne) {

        long boardPlayer = playerOne ? blackCoins : whiteCoins;
        long boardOpponent = playerOne ? whiteCoins : blackCoins;
        long emptyBoard = ~(whiteCoins | blackCoins);

        long validMoves = 0L;

        long checkBoard = boardPlayer;
        while (checkBoard != 0) {
            int position = Long.numberOfTrailingZeros(checkBoard);
            validMoves |= validMovesFromPosition(boardPlayer, boardOpponent, emptyBoard, position);
            checkBoard ^= (1L << position);
        }

        return validMoves;
    }


    public long validMovesFromPosition(long boardPlayer, long boardOpponent, long emptySquares, int position) {
        long validMoves = 0L;

        validMoves |= checkSlice(boardPlayer, boardOpponent, emptySquares, position, 1);
        validMoves |= checkSlice(boardPlayer, boardOpponent, emptySquares, position, -1);
        validMoves |= checkSlice(boardPlayer, boardOpponent, emptySquares, position, 7);
        validMoves |= checkSlice(boardPlayer, boardOpponent, emptySquares, position, -7);
        validMoves |= checkSlice(boardPlayer, boardOpponent, emptySquares, position, 8);
        validMoves |= checkSlice(boardPlayer, boardOpponent, emptySquares, position, -8);
        validMoves |= checkSlice(boardPlayer, boardOpponent, emptySquares, position, 9);
        validMoves |= checkSlice(boardPlayer, boardOpponent, emptySquares, position, -9);


        return validMoves;
    }

    public long checkSlice(long boardPlayer, long boardOpponent, long emptySquares, int position, int dirInc) {
        long startMask = 1L << position;

        if(nextOutOfBounds(position, dirInc) || nextOutOfBounds(position+dirInc, dirInc))
            return 0;

        if((moveMask(startMask, dirInc) & emptySquares) != 0)
            return 0;

        if((moveMask(startMask, dirInc) & boardOpponent) == 0)
            return 0;

        for(int pos = position+(2*dirInc); !nextOutOfBounds(pos-dirInc, dirInc); pos+=dirInc) {
            long posMask = 1L << pos;

            if((posMask & emptySquares) != 0) {
                return posMask;
            }

            if((posMask & boardPlayer) != 0)
                return 0;
        }


        return 0;
    }

    public static long moveMask(long l, int inc) {
        int pos = Long.numberOfTrailingZeros(l) + inc;
        return 1L << pos;
    }


}
