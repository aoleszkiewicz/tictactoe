package com.example.dao;

import com.example.engine.OXEnum;
import com.example.model.Game;
import com.example.datasource.DataSource;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GameDAOImpl implements GameDAO {

    @Override
    public void saveGame(Game game) {
        String sql = "INSERT INTO game (player_o, player_x, winner, date_time) VALUES (?, ?, ?, ?)";

        try (Connection connection = DataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, game.getPlayerX());
            preparedStatement.setString(2, game.getPlayerO());
            preparedStatement.setString(3, game.getWinner().toString());

            // formatowanie daty i czasu
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = game.getGameDateTime().format(formatter);

            preparedStatement.setString(4, formattedDateTime);

            preparedStatement.executeUpdate();

            // pobierz wygenerowane IDki
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int gameId = generatedKeys.getInt(1);
                game.setGameId(gameId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Game> getGames(Integer fromRow, Integer numberOfRows) {
        List<Game> games = new ArrayList<>();
        String selectQuery = "SELECT * FROM game ORDER BY date_time DESC LIMIT ? OFFSET ?";

        try (Connection connection = DataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

            preparedStatement.setInt(1, numberOfRows);
            preparedStatement.setInt(2, fromRow);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int gameId = resultSet.getInt("game_id");
                    String playerX = resultSet.getString("player_x");
                    String playerO = resultSet.getString("player_o");
                    String winner = resultSet.getString("winner");
                    LocalDateTime dateTime = resultSet.getTimestamp("date_time").toLocalDateTime();
                    OXEnum winnerEnum = OXEnum.fromString(winner);
                    Game game = new Game(gameId, playerX, playerO, winnerEnum, dateTime);
                    games.add(game);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return games;
    }

    @Override
    public void deleteGame(int gameId) {
        String deleteQuery = "DELETE FROM game WHERE game_id = ?";

        try (Connection connection = DataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {

            preparedStatement.setInt(1, gameId);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Deleting game from database failed");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
