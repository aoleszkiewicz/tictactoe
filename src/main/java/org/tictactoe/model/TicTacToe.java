package org.tictactoe.model;

import org.tictactoe.enums.OX;
import org.tictactoe.interfaces.ITicTacToe;

import java.util.Arrays;

public class TicTacToe implements ITicTacToe {
    public OX[] board;
    private OX turn;
    private OX winner;
    private int step;


    @Override
    public void init() {
        board = new OX[9];
        Arrays.fill(board, OX.EMPTY);
        step = 0;
        turn = OX.X;
        winner = OX.EMPTY;
    }

    @Override
    public void setField(int index) {
        board[index] = turn;

        if (checkWinner() == OX.EMPTY) {
            turn = (turn == OX.X) ? OX.O : OX.X;
            step++;
            return;
        }

        winner = turn;
    }

    @Override
    public OX getField(int index) {
        return board[index];
    }

    @Override
    public OX getTurn() {
        return turn;
    }

    @Override
    public OX getWinner() {
        return winner;
    }

    @Override
    public OX checkWinner() {
        for (int i = 0; i < 3; i++) {
            if (board[i * 3] != OX.EMPTY &&
                    board[i * 3] == board[i * 3 + 1] &&
                    board[i * 3] == board[i * 3 + 2]) {
                return getTurn();
            }
        }

        for (int i = 0; i < 3; i++) {
            if (board[i] != OX.EMPTY &&
                    board[i] == board[i + 3] &&
                    board[i] == board[i + 6]) {
                return getTurn();
            }
        }

        if (board[0] != OX.EMPTY &&
                board[0] == board[4] &&
                board[0] == board[8]) {
            return getTurn();
        }

        if (board[2] != OX.EMPTY &&
                board[2] == board[4] &&
                board[2] == board[6]) {
            return getTurn();
        }

        if (step == 9) {
            return OX.EMPTY;
        }

        return OX.EMPTY;
    }

    @Override
    public int getStep() {
        return step;
    }
}
