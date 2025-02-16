package org.tictactoe.interfaces;

import java.time.LocalDateTime;

public interface IGame {
    int getId();
    void setId(int id);
    IPlayer getPlayer1();
    void setPlayer1(IPlayer player1);
    IPlayer getPlayer2();
    void setPlayer2(IPlayer player2);
    IPlayer getWinner();
    void setWinner(IPlayer player);
    LocalDateTime getTime();
    void setTime(LocalDateTime time);
}
