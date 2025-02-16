package org.tictactoe.model;

import org.tictactoe.interfaces.IGame;
import org.tictactoe.interfaces.IPlayer;

import java.time.LocalDateTime;

public class Game implements IGame {
    public int id;
    public IPlayer player1;
    public IPlayer player2;
    public IPlayer winner;
    public LocalDateTime time;

    public Game(int id, IPlayer player1, IPlayer player2, IPlayer winner, LocalDateTime time) {
        this.id = id;
        this.player1 = player1;
        this.player2 = player2;
        this.winner = winner;
        this.time = time;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public IPlayer getPlayer1() {
        return this.player1;
    }

    @Override
    public void setPlayer1(IPlayer player1) {
        this.player1 = player1;
    }

    @Override
    public IPlayer getPlayer2() {
        return this.player2;
    }

    @Override
    public void setPlayer2(IPlayer player2) {
        this.player2 = player2;
    }

    @Override
    public IPlayer getWinner() {
        return this.winner;
    }

    @Override
    public void setWinner(IPlayer player) {
        this.winner = player;
    }

    @Override
    public LocalDateTime getTime() {
        return this.time;
    }

    @Override
    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
