package de.lmu.bio.ifi;

import szte.mi.Move;
import szte.mi.Player;

import java.util.Random;

public class RandomComputer implements Player {
    Othello othello;
    boolean playerOne;
    Random rnd;

    @Override
    public void init(int order, long t, Random rnd) {
        this.othello = new Othello();
        this.playerOne = (order == 0);
        this.rnd = rnd;
    }

    @Override
    public Move nextMove(Move prevMove, long tOpponent, long t) {
        if(prevMove != null)
            othello.makeMove(!playerOne, prevMove.x, prevMove.y);

        int coinsPlaced = Long.bitCount(othello.board.blackCoins | othello.board.whiteCoins);
        Move goodMove;

        if(coinsPlaced < 34)
            goodMove = minimaxMove(othello, 5);

        else if(coinsPlaced < 52)
            goodMove = minimaxMove(othello, );

        else
            goodMove = minimaxMove(othello, 12);

        if(goodMove == null)
            return null;

        othello.makeMove(playerOne, goodMove.x, goodMove.y);
        return goodMove;


    }

    public Move minimaxMove(Othello game, int depth) {
        long legalMoves = game.legalMoves;

        if(legalMoves == 0)
            return null;

        if(game.nextToMove != game.playerOneToInt(playerOne))
            return null;

        int bestMove = -1;
        double bestScore = Double.NEGATIVE_INFINITY;

        while (legalMoves != 0) {
            int position = Long.numberOfTrailingZeros(legalMoves);

            if(bestMove == -1){

                bestMove = position;
                Othello newState = game.getNewState(position);
                bestScore = minimax(newState, depth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, newState.nextToMove == game.playerOneToInt(playerOne));

            } else {

                Othello newState = game.getNewState(position);
                double newScore = minimax(newState, depth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, newState.nextToMove == game.playerOneToInt(playerOne));

                if(newScore > bestScore){
                    bestScore = newScore;
                    bestMove = position;
                }
            }

            legalMoves ^= (1L << position);
        }

        return new Move(bestMove % 8, bestMove / 8);
    }

    public double minimax(Othello gameState, int depth, double alpha, double beta, boolean maximizingPlayer) {
        if(depth == 0 || gameState.gameStatus() != GameStatus.RUNNING) {
            return advancedPositionScore(gameState);
        }

        if(maximizingPlayer){
            double maxScore = Double.NEGATIVE_INFINITY;
            long possibleMoves = gameState.legalMoves;

            while (possibleMoves != 0) {
                int position = Long.numberOfTrailingZeros(possibleMoves);
                Othello newState = gameState.getNewState(position);
                double currentMaxScore = minimax(newState, depth - 1, alpha, beta, newState.nextToMove == gameState.playerOneToInt(playerOne));
                maxScore = Math.max(maxScore, currentMaxScore);
                alpha = Math.max(alpha, currentMaxScore);

                if(beta <= alpha)
                    break;

                possibleMoves ^= (1L << position);
            }

            return maxScore;


        } else {

            double minScore = Double.POSITIVE_INFINITY;
            long possibleMoves = gameState.legalMoves;

            while (possibleMoves != 0) {
                int position = Long.numberOfTrailingZeros(possibleMoves);
                Othello newState = gameState.getNewState(position);

                double currentMinScore = minimax(newState, depth - 1, alpha, beta, newState.nextToMove == gameState.playerOneToInt(playerOne));
                minScore = Math.min(minScore, currentMinScore);
                beta = Math.min(beta, currentMinScore);
                if(beta <= alpha)
                    break;

                possibleMoves ^= (1L << position);
            }


            return minScore;
        }
    }

    public double advancedPositionScore(Othello othello) {
        if(othello.gameStatus() != GameStatus.RUNNING) {

            if(othello.gameStatus() == GameStatus.PLAYER_1_WON)
                return playerOne ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;

            if(othello.gameStatus() == GameStatus.PLAYER_2_WON)
                return playerOne ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;

            else
                return 0.0;
        }

        int placedCoins = Long.bitCount(this.othello.board.blackCoins | this.othello.board.whiteCoins);
        double overallScore;

        if(placedCoins < 40)
            overallScore = 80 * cornerScore(othello) + 38 * cornerClosenessScore(othello) + 8 * mobilityScore(othello);

        else if(placedCoins < 52)
            overallScore = 80 * cornerScore(othello) + edgeStabilityScore(othello);

        else
            overallScore = distributionScore(othello);


        return overallScore;
    }

    public double randomScore() {
        return rnd.nextInt(20);
    }

    public double getFrontierDisks(Othello othello){
        long playerBoard = playerOne ? othello.board.blackCoins : othello.board.whiteCoins;
        long opponentBoard = playerOne ? othello.board.whiteCoins : othello.board.blackCoins;
        long emptyBoard = ~(playerBoard | opponentBoard);

        int frontierDisks = 0;

        long coinsToCheck = (othello.nextToMove == 1) ? playerBoard : opponentBoard;

        while (coinsToCheck != 0) {
            int position = Long.numberOfTrailingZeros(coinsToCheck);

            if(checkAllFrontierDirs(position, emptyBoard))
                frontierDisks++;

            coinsToCheck ^= (1L << position);
        }

        return 2 * frontierDisks;

    }

    public boolean checkAllFrontierDirs(int pos, long emptyBoard) {
        if(((1L << pos+1) & emptyBoard) != 0)
            return true;

        if(((1L << pos-1) & emptyBoard) != 0)
            return true;

        if(((1L << pos+8) & emptyBoard) != 0)
            return true;

        if(((1L << pos-8) & emptyBoard) != 0)
            return true;

        if(((1L << pos+9) & emptyBoard) != 0)
            return true;

        if(((1L << pos-9) & emptyBoard) != 0)
            return true;

        if(((1L << pos+7) & emptyBoard) != 0)
            return true;

        return ((1L << pos-7) & emptyBoard) != 0;

    }


    public double cornerScore(Othello othello) {
        if(!othello.board.cornersTouched())
            return 0.0;

        //Calculate the Corner occupancy
        int blackCorners = 0;
        int whiteCorners = 0;

        for(long cornerMask : BoardMasks.corners){
            if((cornerMask & othello.board.whiteCoins) != 0)
                whiteCorners ++;

            else if((cornerMask & othello.board.blackCoins) != 0)
                blackCorners++;
        }

        double cornerScore = (double) 100 * (blackCorners-whiteCorners) / (blackCorners+whiteCorners);

        return playerOne ? cornerScore : -cornerScore;
    }

    public double mobilityScore(Othello othello) {

        double mobilityScore = Long.bitCount(othello.legalMoves);

        if(othello.nextToMove == othello.playerOneToInt(playerOne))
            return 10 * mobilityScore;
        else
            return -10 * mobilityScore;
    }

    public double distributionScore(Othello othello) {
        int blackStones = othello.board.placedBlackCoins().size();
        int whiteStones = othello.board.placedWhiteCoins().size();

        double distributionScore = (double) 100 * (blackStones - whiteStones) / (blackStones+whiteStones);

        return playerOne ? distributionScore : -distributionScore;
    }

    public double cornerClosenessScore(Othello othello) {
        int closeToCornerBlack = 0;
        int closeToCornerWhite = 0;

        for(long position : BoardMasks.closeCornerSquares) {

            if((position & othello.board.blackCoins) != 0)
                closeToCornerBlack++;

            if((position & othello.board.whiteCoins) != 0)
                closeToCornerWhite ++;
        }

        double closeToCorner = (double) 100 * (closeToCornerWhite - closeToCornerBlack) / 12.0;

        return playerOne ? closeToCorner : -closeToCorner;
    }

    public double dynamicCornerClosenessScore(Othello othello) {
        int closeToCornerBlack = 0;
        int closeToCornerWhite = 0;

        long blackMask = othello.board.blackCoins;
        long whiteMask = othello.board.whiteCoins;
        long emptyMask = ~(blackMask | whiteMask);

        if((1L & emptyMask) != 0){
            for(long position : BoardMasks.closeToCornerOne) {

                if((position & othello.board.blackCoins) != 0)
                    closeToCornerBlack++;

                if((position & othello.board.whiteCoins) != 0)
                    closeToCornerWhite ++;
            }
        }

        if((0b10000000L & emptyMask) != 0) {
            for(long position : BoardMasks.closeToCornerTwo) {

                if((position & othello.board.blackCoins) != 0)
                    closeToCornerBlack++;

                if((position & othello.board.whiteCoins) != 0)
                    closeToCornerWhite ++;
            }
        }

        if((0b100000000000000000000000000000000000000000000000000000000L & emptyMask) != 0) {
            for(long position : BoardMasks.closeToCornerThree) {

                if((position & othello.board.blackCoins) != 0)
                    closeToCornerBlack++;

                if((position & othello.board.whiteCoins) != 0)
                    closeToCornerWhite ++;
            }
        }

        if((0b1000000000000000000000000000000000000000000000000000000000000000L & emptyMask) != 0) {
            for(long position : BoardMasks.closeToCornerFour) {

                if((position & othello.board.blackCoins) != 0)
                    closeToCornerBlack++;

                if((position & othello.board.whiteCoins) != 0)
                    closeToCornerWhite ++;
            }
        }



        double closeToCorner = (double) 100 * (closeToCornerWhite - closeToCornerBlack) / 12.0;

        return playerOne ? closeToCorner : -closeToCorner;
    }

    public double stabilityScore(Othello othello) {
        int stableBlackCoins = 0;
        int stableWhiteCoins = 0;

        for(Move blackMove : othello.board.placedBlackCoins()){
            if(othello.board.coinStable(blackMove.x, blackMove.y))
                stableBlackCoins++;

        }

        for(Move whiteMove : othello.board.placedWhiteCoins()){
            if(othello.board.coinStable(whiteMove.x, whiteMove.y))
                stableWhiteCoins++;
        }

        double stabilityScore =  (double) 100 * (stableBlackCoins - stableWhiteCoins) / (stableBlackCoins +  stableWhiteCoins);

        return playerOne ? stabilityScore : -stabilityScore;
    }

    public double minimineScore(Othello othello) {
        if(playerOne) {
            if (othello.board.blackCoins == 0L)
                return Double.NEGATIVE_INFINITY;

            if (othello.board.whiteCoins == 0L)
                return Double.POSITIVE_INFINITY;
        }
        else {
            if (othello.board.blackCoins == 0L)
                return Double.POSITIVE_INFINITY;

            if (othello.board.whiteCoins == 0L)
                return Double.NEGATIVE_INFINITY;
        }
        double score = (double) Long.bitCount(othello.board.blackCoins) - Long.bitCount(othello.board.whiteCoins);

        if(playerOne)
            return -score;

        return score;

    }

    public double edgeStabilityScore(Othello othello) {
        int stableEdgeCoinsBlack = 0;
        int stableEdgeCoinsWhite = 0;

        for(long square : BoardMasks.edgeSquares) {
            if((square & othello.board.blackCoins) == 0 && (square & othello.board.whiteCoins) == 0)
                continue;

            if((square & othello.board.blackCoins & othello.board.stableCoins) != 0) {
                stableEdgeCoinsBlack++;
                continue;
            }

            if((square & othello.board.whiteCoins & othello.board.stableCoins) != 0) {
                stableEdgeCoinsWhite++;
                continue;
            }

            int position = Long.numberOfTrailingZeros(square);
            int x = position % 8;
            int y = position / 8;

            if (othello.board.coinStable(x, y)){
                if((othello.board.blackCoins & square) != 0) {
                    stableEdgeCoinsBlack++;
                    continue;
                }

                if((othello.board.whiteCoins & square) != 0) {
                    stableEdgeCoinsWhite++;
                }
            }
        }

        double edgeStabilityScore = (double) 100 * (stableEdgeCoinsBlack - stableEdgeCoinsWhite) / (stableEdgeCoinsBlack + stableEdgeCoinsWhite);

        return playerOne ? edgeStabilityScore : -edgeStabilityScore;
    }

    public double matrixScore(Othello othello) {
        double score = 0.0;
        long blackBoard = othello.board.blackCoins;
        long whiteBoard = othello.board.whiteCoins;


        while (blackBoard != 0) {
            int position = Long.numberOfTrailingZeros(blackBoard);
            score += BoardMasks.SCORE_MATRIX[position];
            blackBoard ^= (1L << position);
        }

        while (whiteBoard != 0) {
            int position = Long.numberOfTrailingZeros(whiteBoard);
            score -= BoardMasks.SCORE_MATRIX[position];
            whiteBoard ^= (1L << position);
        }

        return playerOne ? score : -score;
    }

}
