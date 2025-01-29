package com.example.engine;

import java.util.Arrays;

public class OXGameImpl implements OXGame {
    private OXEnum[] board;
    private int step;
    private OXEnum turn;
    private OXEnum winner;

    @Override
    public void initialize() {
        board = new OXEnum[9];
        Arrays.fill(board, OXEnum.NONE);
        step = 0;
        turn = OXEnum.X;
        winner = OXEnum.NONE;
    }

    @Override
    public void setField(int index) {
        board[index] = turn;
        if (checkWinner() != OXEnum.NONE) {
            winner = turn;
        } else {
            turn = (turn == OXEnum.X) ? OXEnum.O : OXEnum.X;
            step++;
        }
    }

    @Override
    public OXEnum getField(int index) {
        return board[index];
    }

    @Override
    public OXEnum getTurn() {
        return turn;
    }

    @Override
    public OXEnum getWinner() {
        return winner;
    }

    @Override
    public int getStep() {
        return step;
    }

    private OXEnum checkWinner() {
        // sprawdz linie poziome
        for (int i = 0; i < 3; i++) {
            if (board[i * 3] != OXEnum.NONE &&
                    board[i * 3] == board[i * 3 + 1] &&
                    board[i * 3] == board[i * 3 + 2]) {
                return getTurn();
            }
        }

        // sprawdz pionowe linie
        for (int i = 0; i < 3; i++) {
            if (board[i] != OXEnum.NONE &&
                    board[i] == board[i + 3] &&
                    board[i] == board[i + 6]) {
                return getTurn();
            }
        }

        // sprwadz przekatne
        if (board[0] != OXEnum.NONE &&
                board[0] == board[4] &&
                board[0] == board[8]) {
            return getTurn();
        }

        if (board[2] != OXEnum.NONE &&
                board[2] == board[4] &&
                board[2] == board[6]) {
            return getTurn();
        }

        // sprawdz czy remis
        if (step == 9) {
            return OXEnum.NONE;
        }

        // Brak zwycięzcy
        return OXEnum.NONE;
    }
}