package com.example.model;

import com.example.engine.OXEnum;

import java.time.LocalDateTime;

public class Game {
    private Integer gameId;
    private String playerX;
    private String playerO;
    private OXEnum winner;
    private LocalDateTime gameDateTime;

    public Game() {
    }

    public Game(Integer gameId, String playerX, String playerO, OXEnum winner, LocalDateTime gameDateTime) {
        this.gameId = gameId;
        this.playerX = playerX;
        this.playerO = playerO;
        this.winner = winner;
        this.gameDateTime = gameDateTime;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public String getPlayerX() {
        return playerX;
    }

    public void setPlayerX(String playerX) {
        this.playerX = playerX;
    }

    public String getPlayerO() {
        return playerO;
    }

    public void setPlayerO(String playerO) {
        this.playerO = playerO;
    }

    public OXEnum getWinner() {
        return winner;
    }

    public void setWinner(OXEnum winner) {
        this.winner = winner;
    }

    public LocalDateTime getGameDateTime() {
        return gameDateTime;
    }

    public void setGameDateTime(LocalDateTime gameDateTime) {
        this.gameDateTime = gameDateTime;
    }
}
