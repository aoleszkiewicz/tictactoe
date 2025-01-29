package com.example.dao;

import com.example.model.Game;

import java.util.List;

public interface GameDAO {
    void saveGame(Game game);
    List<Game> getGames(Integer fromRow, Integer numberOfRows);
    void deleteGame(int gameId);
}
