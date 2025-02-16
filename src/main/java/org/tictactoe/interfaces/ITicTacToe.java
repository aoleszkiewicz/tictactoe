package org.tictactoe.interfaces;

import org.tictactoe.enums.OX;

public interface ITicTacToe {
    void init();
    void setField(int index);
    OX getField(int index);
    OX getTurn();
    OX getWinner();
    OX checkWinner();
    int getStep();
}
