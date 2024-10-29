package de.lmu.bio.ifi;


import szte.mi.Move;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Othello implements Game {
    public OthelloBoard board;
    public int nextToMove;
    long legalMoves;

    public Othello(){
        this.board = new OthelloBoard();
        this.nextToMove = 1;
        this.legalMoves = 0b100000010000000000100000010000000000000000000L;
    }

    //Execute Move
    @Override
    public boolean makeMove(boolean playerOne, int x, int y) {
        long moveMask = 1L << (8 * y + x);
        long validMoves = legalMoves;

        if (validMoves == 0L)
            return false;

        if(!OthelloBoard.isInBounds(x, y))
            return false;

        if((moveMask & validMoves) == 0)
            return false;

        flip(playerOne, x, y);
        updateNextToMove(playerOne);
        updateLegalMoves(nextToMove);
        return true;
    }

    public void forceMove(boolean playerOne, int x, int y){
        flip(playerOne, x, y);
        updateNextToMove(playerOne);
        updateLegalMoves(nextToMove);
    }

    public void updateLegalMoves(int nextToMove){
        //Masks for the player's, the opponents and the empty board are created
        long playerBoard = nextToMove == 1 ? board.blackCoins : board.whiteCoins;
        long opponentBoard = nextToMove == 1 ? board.whiteCoins : board.blackCoins;
        long emptyBoard = ~(playerBoard | opponentBoard);

        long foundLegalMoves = 0L;
        long potentialMoves;


        //Shifting upwards
        potentialMoves = (playerBoard >> 8) & BoardMasks.DOWN_MASK & opponentBoard;
        while (potentialMoves != 0L) {
            long tmp = (potentialMoves >> 8) & BoardMasks.DOWN_MASK;
            foundLegalMoves |= tmp & emptyBoard;
            potentialMoves = tmp & opponentBoard;
        }

        //Shifting downwards
        potentialMoves = (playerBoard << 8) & BoardMasks.UP_MASK & opponentBoard;
        while (potentialMoves != 0L) {
            long tmp = (potentialMoves << 8) & BoardMasks.UP_MASK;
            foundLegalMoves |= tmp & emptyBoard;
            potentialMoves = tmp & opponentBoard;
        }

        //Shifting left
        potentialMoves = (playerBoard >> 1) & BoardMasks.RIGHT_MASK & opponentBoard;
        while (potentialMoves != 0L) {
            long tmp = (potentialMoves >> 1) & BoardMasks.RIGHT_MASK;
            foundLegalMoves |= tmp & emptyBoard;
            potentialMoves = tmp & opponentBoard;
        }

        //Shifting right
        potentialMoves = (playerBoard << 1) & BoardMasks.LEFT_MASK & opponentBoard;
        while (potentialMoves != 0L) {
            long tmp = (potentialMoves << 1) & BoardMasks.LEFT_MASK;
            foundLegalMoves |= tmp & emptyBoard;
            potentialMoves = tmp & opponentBoard;
        }

        //shifting up left
        potentialMoves = (playerBoard >> (8 + 1)) & BoardMasks.RIGHT_MASK & BoardMasks.DOWN_MASK & opponentBoard;
        while (potentialMoves != 0L) {
            long tmp = (potentialMoves >> (8 + 1)) & BoardMasks.RIGHT_MASK & BoardMasks.DOWN_MASK;
            foundLegalMoves |= tmp & emptyBoard;
            potentialMoves = tmp & opponentBoard;
        }

        //shifting up right
        potentialMoves = (playerBoard >> (8 - 1)) & BoardMasks.LEFT_MASK & BoardMasks.DOWN_MASK & opponentBoard;
        while (potentialMoves != 0L) {
            long tmp = (potentialMoves >> (8 - 1)) & BoardMasks.LEFT_MASK & BoardMasks.DOWN_MASK;
            foundLegalMoves |= tmp & emptyBoard;
            potentialMoves = tmp & opponentBoard;
        }

        //shifting down left
        potentialMoves = (playerBoard << (8 - 1)) & BoardMasks.RIGHT_MASK & BoardMasks.UP_MASK & opponentBoard;
        while (potentialMoves != 0L) {
            long tmp = (potentialMoves << (8 - 1)) & BoardMasks.RIGHT_MASK & BoardMasks.UP_MASK;
            foundLegalMoves |= tmp & emptyBoard;
            potentialMoves = tmp & opponentBoard;
        }

        //shifting down right
        potentialMoves = (playerBoard << (8 + 1)) & BoardMasks.LEFT_MASK & BoardMasks.UP_MASK & opponentBoard;
        while (potentialMoves != 0L) {
            long tmp = (potentialMoves << (8 + 1)) & BoardMasks.LEFT_MASK & BoardMasks.UP_MASK;
            foundLegalMoves |= tmp & emptyBoard;
            potentialMoves = tmp & opponentBoard;
        }
        legalMoves = foundLegalMoves;
    }

    private void updateNextToMove(boolean playerOne){
        if(playerOne) {
            nextToMove = 2;
            updateLegalMoves(nextToMove);

            if(legalMoves == 0L) {
                nextToMove = 1;
                updateLegalMoves(nextToMove);
            }

        } else {
            nextToMove = 1;
            updateLegalMoves(nextToMove);

            if(legalMoves == 0L) {
                nextToMove = 2;
                updateLegalMoves(nextToMove);
            }
        }
    }

    private void flip(boolean playerOne, int x, int y) {
        board.setCoin(playerOne, x, y);
        for(int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++) {
                tryToFlip(playerOne, x, y, i, j);
            }
        }
    }

    private void tryToFlip(boolean playerOne, int x, int y, int incX, int incY) {
        if(incX == 0 && incY == 0)
            return;

        if(!OthelloBoard.isInBounds(x+incX, y+incY) || !OthelloBoard.isInBounds(x+(2*incX), y+(2*incY)))
            return;

        if(board.coinAtPosition(x+incX, y+incY) != playerOneToInt(!playerOne))
            return;

        for(int a = x+(2*incX), b = y+(2*incY); OthelloBoard.isInBounds(a, b); a+=incX, b+=incY) {

            //If the square is still empty it's a legal move
            if (board.coinAtPosition(a, b) == playerOneToInt(playerOne)) {
                for(int af = x+incX, bf = y+incY; board.coinAtPosition(af, bf) != playerOneToInt(playerOne); af+=incX, bf+=incY){
                    board.setCoin(playerOne, af, bf);
                }
                return;
            }


            //if the Square belongs to playerOne there is no possible move
            if(board.coinAtPosition(a, b) == 0)
                break;

            //if the next square will be out of bounds -> break;
            if(!OthelloBoard.isInBounds(a+incX, b+incY))
                break;
        }


    }

    @Override
    public GameStatus gameStatus() {
        updateLegalMoves(nextToMove);

        if(((board.blackCoins & board.whiteCoins) == (1L)) || legalMoves == 0L) {
            if(board.placedBlackCoins().size() > board.placedWhiteCoins().size())
                return GameStatus.PLAYER_1_WON;

            else if(board.placedBlackCoins().size() < board.placedWhiteCoins().size())
                return GameStatus.PLAYER_2_WON;

            else
                return GameStatus.DRAW;

        }

        return GameStatus.RUNNING;
    }


    //Possible Moves
    @Override
    public List<Move> getPossibleMoves(boolean playerOne) {
        //if playerOne is not to move -> return null
        if(playerOneToInt(playerOne) != nextToMove)
            return null;

        HashSet<BetterMove> possibleMoves = new HashSet<>();
        if(playerOne) {
            for(BetterMove move : board.placedBlackCoins()) {
                possibleMoves.addAll(validMovesFromPosition(true, move.x, move.y));

            }
        } else {
            for(BetterMove move : board.placedWhiteCoins()) {
                possibleMoves.addAll(validMovesFromPosition(false, move.x, move.y));
            }
        }

        //the HashSet is being converted to a List and returned
        return new ArrayList<>(possibleMoves);
    }

    private List<BetterMove> validMovesFromPosition(boolean playerOne, int x, int y) {
        List<BetterMove> validMoves = new ArrayList<>();

        for(int i = -1; i <= 1; i++) {
            for(int j = -1; j <= 1; j++){
                if(checkSlice(playerOne, x, y, i, j) != null)
                    validMoves.add(checkSlice(playerOne, x, y, i, j));
            }
        }

        return validMoves;
    }

    private BetterMove checkSlice(boolean playerOne, int x, int y, int incX, int incY) {
        //if the increment is 0, 0 -> return false;
        if(incX == 0 && incY == 0)
            return null;

        //if the coin doesn't belong to the tested player -> return false;
        if(board.coinAtPosition(x, y) != playerOneToInt(playerOne))
            return null;

        //if next or the next square is not in the board -> return false;
        if(!OthelloBoard.isInBounds(x+incX, y+incY) || !OthelloBoard.isInBounds(x+incX+incX, y+incY+incY))
            return null;


        //if the coin at the next square doesn't belong to the other player -> return false;
        if(board.coinAtPosition(x+incX, y+incY) != playerOneToInt(!playerOne))
            return null;

        //Iterate until a valid move is found or the move is invalid
        for(int a = x+(2*incX), b = y+(2*incY); OthelloBoard.isInBounds(a, b); a+=incX, b+=incY) {

            //If the square is still empty it's a legal move
            if (board.coinAtPosition(a, b) == 0)
                    return new BetterMove(a, b);


            //if the Square belongs to playerOne there is no possible move
            if(board.coinAtPosition(a, b) == playerOneToInt(playerOne))
                break;

            //if the next square will be out of bounds -> break;
            if(!OthelloBoard.isInBounds(a+incX, b+incY))
                break;
        }


        return null;
    }

    //Conversion
    public int playerOneToInt(boolean playerOne){
        return playerOne ? 1 : 2;
    }

    public Othello getNewState(Move move) {
        Othello othello = new Othello();

        //Copy coins
        othello.board.blackCoins = this.board.blackCoins;
        othello.board.whiteCoins = this.board.whiteCoins;

        //copy stableCoins, moveNumber and the CornersTouchedMask
        othello.board.stableCoins = this.board.stableCoins;

        //Copy nextToMove
        othello.nextToMove = this.nextToMove;

        othello.makeMove(othello.nextToMove == 1, move.x, move.y);
        return othello;
    }

    public Othello getNewState(int position) {
        Othello othello = new Othello();

        //Copy coins
        othello.board.blackCoins = this.board.blackCoins;
        othello.board.whiteCoins = this.board.whiteCoins;

        //copy stableCoins, moveNumber and the CornersTouchedMask
        othello.board.stableCoins = this.board.stableCoins;

        //Copy nextToMove
        othello.nextToMove = this.nextToMove;

        //copy current legal Moves
        othello.legalMoves = this.legalMoves;

        othello.forceMove(othello.nextToMove == 1, position % 8, position / 8);
        return othello;
    }

    @Override
    public String toString() {
        return board.toString();
    }

    public static void visualizeLong(long theLong) {
        StringBuilder board = new StringBuilder();

        for(int y = 0; y < 8; y++) {
            for(int x = 0; x < 8; x++) {
                long mask = 1L << (y * 8 + x);

                if((theLong & mask) != 0)
                    board.append("O");
                else
                    board.append(".");


                if(x != 7)
                    board.append(" ");

                if(x == 7)
                    board.append("\n");
            }
        }
        System.out.println(board.toString());
    }
}
