package de.lmu.bio.ifi;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import szte.mi.Move;

import java.util.Random;

public class OthelloGUI extends Application {
    Othello othello = new Othello();
    Button[][] buttons = new Button[8][8];
    Label statusLabel;
    boolean humanPlayer = true;
    RandomComputer computer = new RandomComputer();

    int circleSize = 30;
    int buttonSize = 80;
    final Color black = Color.BLACK;
    final Color white = Color.ANTIQUEWHITE;

    public static void main(String[] args)  {
        launch(args);
    }

    @Override
    public void start(Stage window) throws Exception {
        // die BorderPane dient als Layout zur Strukturierung der Seite
        // die GridPane modelliert das Spielbrett
        BorderPane layout = new BorderPane();
        GridPane gameBoard = new GridPane();

        // Die Spielfelder werden als Buttons repräsentiert und hier erstellt
        makeButtons(gameBoard);
        setButtonsOnAction();

        // Hier wird das für den GameStatus erstellt und angepasst
        statusLabel = new Label("RUNNING...");
        statusLabel.setPrefSize(300, 40);
        statusLabel.setFont(new Font(20));


        // Das Spielbrett wird aktualisiert
        layout.setCenter(gameBoard);
        layout.setBottom(statusLabel);
        updateBoard();

        // Erstellen des Players
        // Mensch hat die schwarzen Steine
        computer.init(1, 10000000, new Random());
        humanPlayer = othello.nextToMove == 1;


        // Die Scene wird erstellt und das Fenster geöffnet
        window.setTitle("Othello Valentin Rexer");
        window.setScene(new Scene(layout, 640, 680));
        window.show();

    }

    private void makeButtons(GridPane gameBoard) {
        for(int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Button b = new Button();
                b.setPrefSize(buttonSize, buttonSize);
                b.setStyle("-fx-background-color: darkseagreen; -fx-border-color: black; -fx-border-width: 1;");
                gameBoard.add(b, i, j);
                buttons[j][i] = b;
            }
        }
    }

    private void setButtonsOnAction() {
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                Button b = buttons[j][i];
                final int finI = i;
                final int finJ = j;

                b.setOnAction(e -> {
                    makeHumanMove(humanPlayer, finI, finJ);
                    makeComputerMove(new Move(finI, finJ));
                });

            }
        }
    }

    public void makeHumanMove(boolean humanPlayerOne, int x, int y) {
        //bei einem illegal Move wird dies ausgegeben und abgebrochen
        if (!othello.makeMove(humanPlayerOne, x, y)) {
            statusLabel.setText("Illegal Move!");
            return;
        }

        //der Move wird ausgeführt und die graphische Darstellung vom Board aktualisiert
        othello.makeMove(humanPlayerOne, x, y);
        updateBoard();
    }
    public void makeComputerMove(Move prevMove) {


        //Der Computer macht einen Zug; falls dieser gültig ist, wird er gespielt
        Move compMove = computer.nextMove(prevMove, 100000, 100000);

        if (compMove != null)
            othello.makeMove(!humanPlayer, compMove.x, compMove.y);

        updateBoard();
    }

    public void updateBoard() {
        for(int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

                if (othello.board.coinAtPosition(i, j) == 2) {
                    Circle c = new Circle(circleSize);
                    c.setFill(white);
                    c.setStroke(black);
                    buttons[j][i].setGraphic(c);
                }

                if (othello.board.coinAtPosition(i, j) == 1) {
                    Circle c = new Circle(circleSize);
                    c.setFill(black);
                    buttons[j][i].setGraphic(c);
                }
            }
        }


        if(othello.gameStatus() == GameStatus.PLAYER_1_WON) {
            statusLabel.setText("Player1 WON!   "+othello.board.placedBlackCoins().size()+" : "+othello.board.placedWhiteCoins().size());
        }
        if(othello.gameStatus() == GameStatus.PLAYER_2_WON) {
            statusLabel.setText("Player2 WON!   "+othello.board.placedBlackCoins().size()+" : "+othello.board.placedWhiteCoins().size());
        }
        if(othello.gameStatus() == GameStatus.DRAW) {
            statusLabel.setText("DRAW!   "+othello.board.placedBlackCoins().size()+" : "+othello.board.placedWhiteCoins().size());
        }
        if(othello.gameStatus() == GameStatus.RUNNING) {
            statusLabel.setText("RUNNING...   "+othello.board.placedBlackCoins().size()+" : "+othello.board.placedWhiteCoins().size());
        }

    }
}
